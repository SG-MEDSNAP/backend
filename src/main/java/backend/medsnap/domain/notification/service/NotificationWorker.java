package backend.medsnap.domain.notification.service;

import backend.medsnap.domain.notification.client.ExpoPushClient;
import backend.medsnap.domain.notification.entity.Notification;
import backend.medsnap.domain.notification.entity.NotificationStatus;
import backend.medsnap.domain.notification.repository.NotificationRepository;
import backend.medsnap.domain.notification.util.RateLimiter;
import backend.medsnap.domain.pushToken.entity.PushToken;
import backend.medsnap.domain.pushToken.repository.PushTokenRepository;
import com.niamedtech.expo.exposerversdk.response.Status;
import com.niamedtech.expo.exposerversdk.response.TicketResponse;
import com.niamedtech.expo.exposerversdk.request.PushNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationWorker {

    private final NotificationRepository notificationRepository;
    private final PushTokenRepository pushTokenRepository;
    private final ExpoPushClient expoClient;
    private final RateLimiter rateLimiter;

    @Scheduled(fixedDelay = 5000)
    public void dispatchDue() {
        // 트랜잭션 안에서 배치 조회
        var batch = fetchBatch();

        if (!batch.isEmpty()) {
            log.info("처리할 알림 {}개 조회", batch.size());
        }
        
        // 각 알림을 개별 트랜잭션으로 처리
        for (Notification n : batch) {
            try {
                processOneNotification(n);
            } catch (Exception e) {
                log.error("알림 처리 중 예외 발생: notificationId={}, error={}", n.getId(), e.getMessage(), e);
                // 개별 알림 실패가 다른 알림에 영향을 주지 않도록 계속 진행
            }
        }
    }
    
    /**
     * 트랜잭션 안에서 배치 조회
     */
    @Transactional
    protected List<Notification> fetchBatch() {
        return notificationRepository.pickDueForDispatch(200);
    }
    
    /**
     * 개별 알림 처리
     */
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    protected void processOneNotification(Notification n) {
        log.info("알림 전송 준비: notificationId={}, userId={}", n.getId(), n.getUser().getId());
        
        var tokens = pushTokenRepository.findActiveTokensByUserId(n.getUser().getId());

        if (tokens.isEmpty()) {
            log.warn("활성 푸시 토큰 없음: userId={}", n.getUser().getId());
            n.markProviderError("No Active Push Token");
            return;
        }

        var tokenStrings = tokens.stream()
                .map(PushToken::getToken)
                .toList();
        log.info("Expo 전송 대상 토큰 {}개", tokenStrings.size());

        // 외부 API 호출
        ExpoSendResult expoResult = sendToExpo(n, tokenStrings);

        // 결과 처리
        processExpoResult(n, expoResult);
    }
    
    /**
     * Expo API 호출
     */
    private ExpoSendResult sendToExpo(Notification n, List<String> tokenStrings) {
        List<String> allSuccessTicketIds = new ArrayList<>();
        boolean hasError = false;
        String errorMessage = null;
        List<String> invalidTokens = new ArrayList<>();

        try {
            for (List<String> chunk : chunks(tokenStrings, 100)) {
                // Rate Limiting 적용
                rateLimiter.acquire(chunk.size());
                
                PushNotification pushNotification = new PushNotification();
                pushNotification.setTo(chunk);
                pushNotification.setTitle(n.getTitle());
                pushNotification.setBody(n.getBody());
                pushNotification.setData(n.getData());

                List<TicketResponse.Ticket> tickets = expoClient.sendWithRetry(List.of(pushNotification), 3);

                boolean allOk = true;
                List<String> successTicketIds = new ArrayList<>();

                for (TicketResponse.Ticket t : tickets) {
                    Status status = t.getStatus();

                    if (status == Status.OK) {
                        if (t.getId() != null) successTicketIds.add(t.getId());
                    } else {
                        allOk = false;

                        TicketResponse.Ticket.Details details = t.getDetails();
                        TicketResponse.Ticket.Error errEnum = (details != null) ? details.getError() : null;
                        String error = (errEnum != null) ? errEnum.name() : "Unknown";

                        log.warn("Expo 티켓 에러: notificationId={}, error={}", n.getId(), error);
                        errorMessage = error;

                        // 무효 토큰 수집
                        if ("DeviceNotRegistered".equals(error)) {
                            invalidTokens.addAll(chunk);
                        }
                    }
                }

                if (allOk) {
                    allSuccessTicketIds.addAll(successTicketIds);
                    log.info("청크 전송 성공: notificationId={}, tickets={}", n.getId(), successTicketIds);
                } else {
                    hasError = true;
                    log.error("청크 전송 실패: notificationId={}, error={}", n.getId(), errorMessage);
                }
            }
        } catch (Exception e) {
            hasError = true;
            errorMessage = "Expo API 호출 실패: " + e.getMessage();
            log.error("Expo API 호출 중 예외 발생: notificationId={}, error={}", n.getId(), e.getMessage(), e);
        }

        return new ExpoSendResult(allSuccessTicketIds, hasError, errorMessage, invalidTokens);
    }
    
    /**
     * Expo API 결과 처리
     */
    private void processExpoResult(Notification n, ExpoSendResult expoResult) {
        // 무효 토큰 정리
        if (!expoResult.getInvalidTokens().isEmpty()) {
            pushTokenRepository.deactivateAllByTokenIn(expoResult.getInvalidTokens(), "DeviceNotRegistered");
            log.info("무효 토큰 비활성화 {}개 처리", expoResult.getInvalidTokens().size());
        }

        // 알림 상태 업데이트
        if (!expoResult.getSuccessTicketIds().isEmpty() && !expoResult.hasError()) {
            n.markSent(appendIds(n.getProviderMessageId(), expoResult.getSuccessTicketIds()));
            log.info("알림 전송 성공: notificationId={}, tickets={}", n.getId(), expoResult.getSuccessTicketIds());
        } else {
            n.markProviderError(expoResult.hasError() ? expoResult.getErrorMessage() : "Expo 전송 실패");
            log.error("알림 전송 실패: notificationId={}, error={}", n.getId(), expoResult.getErrorMessage());
        }
    }

    private static <T> List<List<T>> chunks(List<T> list, int size) {
        List<List<T>> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            result.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return result;
    }

    private static String appendIds(String current, List<String> add) {
        String joined = String.join(",", add);
        if (current == null || current.isBlank()) return joined;
        return current + "," + joined;
    }
    
    /**
     * Expo API 호출 결과를 담는 내부 클래스
     */
    private static class ExpoSendResult {
        private final List<String> successTicketIds;
        private final boolean hasError;
        private final String errorMessage;
        private final List<String> invalidTokens;
        
        public ExpoSendResult(List<String> successTicketIds, boolean hasError, String errorMessage, List<String> invalidTokens) {
            this.successTicketIds = successTicketIds;
            this.hasError = hasError;
            this.errorMessage = errorMessage;
            this.invalidTokens = invalidTokens;
        }
        
        public List<String> getSuccessTicketIds() { return successTicketIds; }
        public boolean hasError() { return hasError; }
        public String getErrorMessage() { return errorMessage; }
        public List<String> getInvalidTokens() { return invalidTokens; }
    }
}

