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

    public ReceiptResponse getReceipts(List<String> ticketIds) {
        try {
            log.info("푸시 리시트 조회 요청: {}개 티켓", ticketIds.size());

            // 요청 본문 생성
            Map<String, Object> requestBody = Map.of("ids", ticketIds);
            String jsonBody = objectMapper.writeValueAsString(requestBody);

            // HTTP 요청 생성 - Accept-Encoding 제거 (압축 비활성화)
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://exp.host/--/api/v2/push/getReceipts"))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("Accept-Encoding", "identity") // ← 압축 비활성화
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .timeout(Duration.ofSeconds(30))
                    .build();

            // 요청 실행
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // 응답 상태 확인
            if (response.statusCode() != 200) {
                log.error("푸시 리시트 조회 실패: HTTP {}, 응답: {}", response.statusCode(), response.body());
                throw new NotificationException(ErrorCode.NOTIFICATION_SEND_FAIL, 
                    "푸시 리시트 조회 실패: HTTP " + response.statusCode());
            }

            // 응답 본문 정리
            String rawBody = response.body();
            log.debug("Expo 원본 응답 길이: {} bytes", rawBody.length());
            
            String cleanedBody = cleanResponse(rawBody);
            
            // JSON 파싱
            ReceiptResponse receiptResponse = objectMapper.readValue(cleanedBody, ReceiptResponse.class);
            log.info("푸시 리시트 조회 성공: {}개 리시트", 
                receiptResponse.getData() != null ? receiptResponse.getData().size() : 0);
            
            return receiptResponse;

        } catch (IOException | InterruptedException e) {
            log.error("푸시 리시트 조회 중 예외 발생", e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new NotificationException(ErrorCode.NOTIFICATION_SEND_FAIL, 
                "푸시 리시트 조회 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * 응답에서 제어 문자 및 BOM 제거
     */
    private String cleanResponse(String response) {
        if (response == null || response.isEmpty()) {
            return response;
        }
        
        // BOM(Byte Order Mark) 제거
        if (response.startsWith("\uFEFF")) {
            response = response.substring(1);
            log.debug("BOM 제거됨");
        }
        
        // 제어 문자 제거 (ASCII 0-31, 127 중 \r, \n, \t 제외)
        String cleaned = response.replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]", "");
        
        if (!cleaned.equals(response)) {
            log.warn("응답에서 제어 문자 제거됨: 원본 {} bytes → 정리 후 {} bytes", 
                response.length(), cleaned.length());
        }
        
        return cleaned;
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
