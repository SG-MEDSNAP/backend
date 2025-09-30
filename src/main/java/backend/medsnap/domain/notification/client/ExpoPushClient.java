package backend.medsnap.domain.notification.client;

import backend.medsnap.domain.notification.exception.NotificationException;
import backend.medsnap.global.exception.ErrorCode;
import com.niamedtech.expo.exposerversdk.ExpoPushNotificationClient;
import com.niamedtech.expo.exposerversdk.request.PushNotification;
import com.niamedtech.expo.exposerversdk.response.TicketResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExpoPushClient {

    private final ExpoPushNotificationClient expoClient;

    /**
     * 429/5xx 대비 재시도 래퍼
     */
    public List<TicketResponse.Ticket> sendWithRetry(List<PushNotification> batch, int maxAttempts) {
        int attempt = 0;
        while (true) {
            try {

                log.info("Expo 푸시 전송 시도 (총 {}건, {}회차)", batch.size(), attempt + 1);
                List<TicketResponse.Ticket> tickets = expoClient.sendPushNotifications(batch);
                log.info("Expo 푸시 전송 성공 (응답 티켓 {}건)", tickets.size());
                return tickets;

            } catch (IOException ex) {
                attempt++;
                log.warn("Expo 푸시 전송 실패 (네트워크/서버 오류) - {}회차, 재시도 예정", attempt, ex);

                if (attempt >= maxAttempts) {
                    log.error("Expo 푸시 전송 최종 실패 (총 {}회 시도 후 포기)", attempt, ex);
                    throw new NotificationException(ErrorCode.NOTIFICATION_SEND_FAIL, "Expo 푸시 전송 실패");
                }
                backoff(attempt);

            } catch (Exception ex) {
                // 잘못된 페이로드 등 비재시도 오류
                log.error("Expo 푸시 전송 오류 (재시도 불가)", ex);
                throw new NotificationException(ErrorCode.NOTIFICATION_SEND_FAIL, "Expo 푸시 전송 중 비재시도 오류 발생");
            }
        }
    }

    private void backoff(int attempt) {

        long sleepMs = 1000L << (attempt - 1);
        log.info("{}ms 대기 후 재시도", sleepMs);

        try {
            Thread.sleep(sleepMs);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
