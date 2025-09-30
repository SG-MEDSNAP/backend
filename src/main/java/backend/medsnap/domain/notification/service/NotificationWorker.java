package backend.medsnap.domain.notification.service;

import backend.medsnap.domain.notification.entity.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationWorker {

    private final NotificationDispatchService dispatchService;

    @Scheduled(fixedDelay = 5000)
    public void dispatchDue() {
        // 배치 조회
        var batch = dispatchService.fetchBatch(200);

        if (!batch.isEmpty()) {
            log.info("처리할 알림 {}개 조회", batch.size());
        }
        
        // 각 알림을 개별 트랜잭션으로 처리
        for (Notification n : batch) {
            try {
                dispatchService.processOne(n); // 프록시 경유 → @Transactional 유효
            } catch (Exception e) {
                log.error("알림 처리 중 예외 발생: notificationId={}, error={}", n.getId(), e.getMessage(), e);
                // 개별 알림 실패가 다른 알림에 영향을 주지 않도록 계속 진행
            }
        }
    }
}