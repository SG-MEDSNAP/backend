package backend.medsnap.domain.notification.client;

import backend.medsnap.domain.notification.exception.NotificationException;
import backend.medsnap.global.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExpoReceiptClient {

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    /**
     * 푸시 리시트 조회
     * @param ticketIds 푸시 티켓 ID 목록
     * @return 리시트 응답 데이터
     */
    public ReceiptResponse getReceipts(List<String> ticketIds) {
        try {
            log.info("푸시 리시트 조회 요청: {}개 티켓", ticketIds.size());

            // 요청 본문 생성
            Map<String, Object> requestBody = Map.of("ids", ticketIds);
            String jsonBody = objectMapper.writeValueAsString(requestBody);

            // HTTP 요청 생성
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://exp.host/--/api/v2/push/getReceipts"))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("Accept-Encoding", "gzip, deflate")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .timeout(Duration.ofSeconds(30))
                    .build();

            // 요청 실행
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // 응답 처리
            if (response.statusCode() == 200) {
                ReceiptResponse receiptResponse = objectMapper.readValue(response.body(), ReceiptResponse.class);
                log.info("푸시 리시트 조회 성공: {}개 리시트", receiptResponse.getData().size());
                return receiptResponse;
            } else {
                log.error("푸시 리시트 조회 실패: HTTP {}, 응답: {}", response.statusCode(), response.body());
                throw new NotificationException(ErrorCode.NOTIFICATION_SEND_FAIL, 
                    "푸시 리시트 조회 실패: HTTP " + response.statusCode());
            }

        } catch (IOException | InterruptedException e) {
            log.error("푸시 리시트 조회 중 예외 발생", e);
            throw new NotificationException(ErrorCode.NOTIFICATION_SEND_FAIL, 
                "푸시 리시트 조회 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * 푸시 리시트 응답 데이터 클래스
     */
    public static class ReceiptResponse {
        private Map<String, Receipt> data;
        private List<Error> errors;

        public Map<String, Receipt> getData() { return data; }
        public void setData(Map<String, Receipt> data) { this.data = data; }
        public List<Error> getErrors() { return errors; }
        public void setErrors(List<Error> errors) { this.errors = errors; }

        public static class Receipt {
            private String status;
            private String message;
            private Details details;

            public String getStatus() { return status; }
            public void setStatus(String status) { this.status = status; }
            public String getMessage() { return message; }
            public void setMessage(String message) { this.message = message; }
            public Details getDetails() { return details; }
            public void setDetails(Details details) { this.details = details; }

            public static class Details {
                private String error;

                public String getError() { return error; }
                public void setError(String error) { this.error = error; }
            }
        }

        public static class Error {
            private String code;
            private String message;

            public String getCode() { return code; }
            public void setCode(String code) { this.code = code; }
            public String getMessage() { return message; }
            public void setMessage(String message) { this.message = message; }
        }
    }
}
