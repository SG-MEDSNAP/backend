package backend.medsnap.domain.notification.service;

import backend.medsnap.domain.notification.client.ExpoPushClient;
import backend.medsnap.domain.notification.entity.Notification;
import backend.medsnap.domain.notification.entity.NotificationStatus;
import backend.medsnap.domain.notification.repository.NotificationRepository;
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

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void dispatchDue() {

        var batch = notificationRepository.pickDueForDispatch(200);

        for (Notification n : batch) {
            log.info("알림 전송 준비: notificationId={}, userId={}", n.getId(), n.getUser().getId());
            var tokens = pushTokenRepository.findActiveTokensByUserId(n.getUser().getId());

            if (tokens.isEmpty()) {
                log.warn("활성 푸시 토큰 없음: userId={}", n.getUser().getId());
                n.markProviderError("No Active Push Token");
                continue;
            }

            var tokenStrings = tokens.stream()
                    .map(PushToken::getToken)
                    .toList();
            log.info("Expo 전송 대상 토큰 {}개", tokenStrings.size());

            for (List<String> chunk : chunks(tokenStrings, 100)) {
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
                        n.markProviderError(error);

                        // 무효 토큰 정리
                        if ("DeviceNotRegistered".equals(error)) {
                            pushTokenRepository.deactivateAllByTokenIn(chunk, "DeviceNotRegistered");
                            log.info("무효 토큰 비활성화 {}개 처리", chunk.size());
                        }
                    }
                }

                if (allOk && !successTicketIds.isEmpty()) {
                    n.markSent(appendIds(n.getProviderMessageId(), successTicketIds));
                    log.info("알림 전송 성공: notificationId={}, tickets={}", n.getId(), successTicketIds);
                } else if (!allOk && n.getStatus() != NotificationStatus.PROVIDER_ERROR) {
                    n.markProviderError("Expo 전송 실패");
                    log.error("알림 전송 실패(상태 반영): notificationId={}", n.getId());
                }
            }
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
}
