package backend.medsnap.infra.inference.client;

import java.time.Duration;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import backend.medsnap.global.exception.ErrorCode;
import backend.medsnap.infra.inference.dto.request.InferenceRequest;
import backend.medsnap.infra.inference.dto.response.InferenceResponse;
import backend.medsnap.infra.inference.exception.InferenceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@Profile({"prod", "production"})
@RequiredArgsConstructor
public class FastAPIInferenceClient implements InferenceClient {

    private final WebClient inferenceWebClient;

    @Override
    public InferenceResponse verify(String imageUrl) {
        String requestId = UUID.randomUUID().toString();
        log.info("추론 요청 시작 - requestId: {}, imageUrl: {}", requestId, imageUrl);

        try {
            return inferenceWebClient
                    .patch()
                    .uri("/predict") // FastAPI 추론 엔드포인트
                    .header("X-Request-Id", requestId)
                    .bodyValue(InferenceRequest.of(requestId, imageUrl))
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::is4xxClientError,
                            response ->
                                    response.bodyToMono(String.class)
                                            .flatMap(
                                                    body ->
                                                            Mono.error(
                                                                    new InferenceException(
                                                                            ErrorCode
                                                                                    .INFERENCE_INVALID_REQUEST,
                                                                            "잘못된 요청입니다. Body: "
                                                                                    + body))))
                    .onStatus(
                            HttpStatusCode::is5xxServerError,
                            response ->
                                    response.bodyToMono(String.class)
                                            .flatMap(
                                                    body ->
                                                            Mono.error(
                                                                    new InferenceException(
                                                                            ErrorCode
                                                                                    .INFERENCE_COMMUNICATION_ERROR,
                                                                            "추론 서버에 오류가 발생했습니다. Body: "
                                                                                    + body))))
                    .bodyToMono(InferenceResponse.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();
        } catch (InferenceException e) {
            log.error("추론 서버 통신에 실패했습니다 - requestId: {}", requestId, e);
            throw e;
        } catch (Exception e) {
            log.error("추론 서버 통신에 실패했습니다 - requestId: {}", requestId, e);

            // 연결 실패 시 더 구체적인 에러 메시지 제공
            if (e.getMessage() != null && e.getMessage().contains("Connection refused")) {
                throw new InferenceException(
                        ErrorCode.INFERENCE_COMMUNICATION_ERROR,
                        "AI 서버에 연결할 수 없습니다. 서버가 실행 중인지 확인해주세요.");
            }

            throw new InferenceException("추론 서버 통신 실패", e);
        }
    }
}
