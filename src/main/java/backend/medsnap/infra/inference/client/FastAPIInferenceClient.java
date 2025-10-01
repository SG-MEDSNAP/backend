package backend.medsnap.infra.inference.client;

import backend.medsnap.global.exception.ErrorCode;
import backend.medsnap.infra.inference.dto.request.InferenceRequest;
import backend.medsnap.infra.inference.dto.response.InferenceResponse;
import backend.medsnap.infra.inference.exception.InferenceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Component
@Profile("!local")
@RequiredArgsConstructor
public class FastAPIInferenceClient implements InferenceClient {

    private final WebClient inferenceWebClient;

    @Override
    public InferenceResponse verify(String imageUrl) {
        String requestId = UUID.randomUUID().toString();
        log.info("추론 요청 시작 - requestId: {}, imageUrl: {}", requestId, imageUrl);

        try {
            return inferenceWebClient.post()
                    .uri("/v1/infer") // 실제 FastAPI API 엔드포인트
                    .header("X-Request-Id", requestId)
                    .bodyValue(InferenceRequest.of(requestId, imageUrl))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, response ->
                            response.bodyToMono(String.class).flatMap(body ->
                                    Mono.error(new InferenceException(ErrorCode.INFERENCE_INVALID_REQUEST, "잘못된 요청입니다. Body: " + body)))
                    )
                    .onStatus(HttpStatusCode::is5xxServerError, response ->
                            response.bodyToMono(String.class).flatMap(body ->
                                    Mono.error(new InferenceException(ErrorCode.INFERENCE_COMMUNICATION_ERROR, "추론 서버에 오류가 발생했습니다. Body: " + body)))
                    )
                    .bodyToMono(InferenceResponse.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();
        } catch (InferenceException e) {
            log.error("추론 서버 통신에 실패했습니다 - requestId: {}", requestId, e);
            throw e;
        } catch (Exception e) {
            log.error("추론 서버 통신에 실패했습니다 - requestId: {}", requestId, e);
            throw new InferenceException("추론 서버 통신 실패", e);
        }
    }

}
