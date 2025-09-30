package backend.medsnap.domain.notification.service;

import backend.medsnap.domain.notification.client.ExpoReceiptClient;
import backend.medsnap.domain.notification.entity.Notification;
import backend.medsnap.domain.notification.entity.NotificationStatus;
import backend.medsnap.domain.notification.repository.NotificationRepository;
import backend.medsnap.domain.pushToken.repository.PushTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 푸시 리시트 확인을 위한 워커
 * 15분마다 실행되어 SENT 상태의 알림들의 실제 배달 상태를 확인
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationReceiptWorker {

    private final NotificationRepository notificationRepository;
    private final PushTokenRepository pushTokenRepository;
    private final ExpoReceiptClient expoReceiptClient;

    /**
     * 15분마다 푸시 리시트 확인
     */
    @Scheduled(fixedDelay = 900000) // 15분 = 900,000ms
    public void checkPushReceipts() {
        log.info("--- [푸시 리시트 확인 시작] ---");
        
        try {
            // SENT 상태인 알림들 조회 (최근 24시간 이내, updatedAt 기준)
            LocalDateTime since = LocalDateTime.now().minusHours(24);
            List<Notification> sentNotifications = notificationRepository.findRecentSentWithTickets(since);
            
            if (sentNotifications.isEmpty()) {
                log.info("확인할 푸시 리시트가 없습니다.");
                return;
            }

            log.info("푸시 리시트 확인 대상: {}개 알림", sentNotifications.size());

            // 티켓 ID 추출
            List<String> ticketIds = extractTicketIds(sentNotifications);
            
            if (ticketIds.isEmpty()) {
                log.info("유효한 티켓 ID가 없습니다.");
                return;
            }

            // 1000개씩 청크로 나누어 처리 (Expo API 제한)
            List<List<String>> chunks = chunks(ticketIds, 1000);
            
            for (List<String> chunk : chunks) {
                processReceiptChunk(chunk, sentNotifications);
            }

            log.info("--- [푸시 리시트 확인 완료] ---");
            
        } catch (Exception e) {
            log.error("푸시 리시트 확인 중 예외 발생", e);
        }
    }

    /**
     * 리시트 청크 처리
     */
    @Transactional
    protected void processReceiptChunk(List<String> ticketIds, List<Notification> sentNotifications) {
        try {
            // Expo API로 리시트 조회
            ExpoReceiptClient.ReceiptResponse response = expoReceiptClient.getReceipts(ticketIds);
            
            // 각 알림의 상태 업데이트
            for (Notification notification : sentNotifications) {
                if (notification.getProviderMessageId() == null) {
                    continue;
                }

                String[] ticketIdArray = notification.getProviderMessageId().split(",");
                boolean hasAnySuccess = false;
                boolean hasDeviceNotRegistered = false;

                for (String ticketId : ticketIdArray) {
                    ticketId = ticketId.trim();
                    ExpoReceiptClient.ReceiptResponse.Receipt receipt = response.getData().get(ticketId);
                    
                    if (receipt != null) {
                        if ("ok".equals(receipt.getStatus())) {
                            hasAnySuccess = true;
                            log.debug("푸시 배달 성공: notificationId={}, ticketId={}", notification.getId(), ticketId);
                        } else if ("error".equals(receipt.getStatus())) {
                            String error = receipt.getDetails() != null ? receipt.getDetails().getError() : "Unknown";
                            log.warn("푸시 배달 실패: notificationId={}, ticketId={}, error={}", 
                                notification.getId(), ticketId, error);
                            
                            // DeviceNotRegistered 에러인 경우 플래그 설정
                            if ("DeviceNotRegistered".equals(error)) {
                                hasDeviceNotRegistered = true;
                            }
                        }
                    } else {
                        // 리시트가 없는 경우 (아직 처리 중이거나 만료)
                        log.debug("리시트 없음: notificationId={}, ticketId={}", notification.getId(), ticketId);
                    }
                }

                // 하나라도 성공하면 DELIVERED 상태로 업데이트
                if (hasAnySuccess) {
                    notification.markDelivered();
                    log.info("알림 배달 완료: notificationId={}", notification.getId());
                }

                // DeviceNotRegistered가 있으면 토큰 비활성화 (한 번만)
                if (hasDeviceNotRegistered) {
                    pushTokenRepository.deactivateAllByUserId(notification.getUser().getId(), "DeviceNotRegistered");
                    log.info("무효 토큰 비활성화: userId={}", notification.getUser().getId());
                }
            }

        } catch (Exception e) {
            log.error("리시트 청크 처리 중 예외 발생: chunkSize={}", ticketIds.size(), e);
        }
    }

    /**
     * 알림 목록에서 티켓 ID 추출
     */
    private List<String> extractTicketIds(List<Notification> notifications) {
        List<String> ticketIds = new ArrayList<>();
        
        for (Notification notification : notifications) {
            if (notification.getProviderMessageId() != null) {
                String[] ticketIdArray = notification.getProviderMessageId().split(",");
                for (String ticketId : ticketIdArray) {
                    String trimmed = ticketId.trim();
                    if (!trimmed.isEmpty()) {
                        ticketIds.add(trimmed);
                    }
                }
            }
        }
        
        return ticketIds;
    }

    /**
     * 리스트를 지정된 크기의 청크로 분할
     */
    private static <T> List<List<T>> chunks(List<T> list, int size) {
        List<List<T>> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            result.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return result;
    }
}
