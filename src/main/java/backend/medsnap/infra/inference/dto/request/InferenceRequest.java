package backend.medsnap.infra.inference.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InferenceRequest {

    private String requestId;
    private String imageUrl;

    public static InferenceRequest of(String imageUrl) {
        return InferenceRequest.builder()
                .requestId(UUID.randomUUID().toString())
                .imageUrl(imageUrl)
                .build();
    }
}
