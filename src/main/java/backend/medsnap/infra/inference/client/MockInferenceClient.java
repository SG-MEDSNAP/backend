package backend.medsnap.infra.inference.client;

import backend.medsnap.infra.inference.dto.response.InferenceResponse;
import backend.medsnap.infra.inference.exception.InferenceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@Profile("local")
public class MockInferenceClient implements InferenceClient {

    @Override
    public InferenceResponse verify(String imageUrl) {
        log.warn("===== INFERENCE CLIENT IS RUNNING IN MOCK MODE =====");

        try {
            Thread.sleep(300);

            return InferenceResponse.builder()
                    .requestId("mock-" + UUID.randomUUID().toString().substring(0, 8))
                    .success(true)
                    .code("OK")
                    .message("Mock verification successful")
                    .data(InferenceResponse.Data.builder()
                            .hasMedicine(true)
                            .confidence(0.98)
                            .modelInfo(InferenceResponse.ModelInfo.builder()
                                    .name("mock-model")
                                    .version("1.0.0")
                                    .build())
                            .build())
                    .build();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InferenceException("Mock client 실행 중 오류 발생", e);
        }
    }
}
