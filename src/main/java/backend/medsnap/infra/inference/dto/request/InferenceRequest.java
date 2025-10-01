package backend.medsnap.infra.inference.dto.request;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InferenceRequest {

    @JsonProperty("request_id")
    private String requestId;

    @JsonProperty("image_url")
    private String imageUrl;

    public static InferenceRequest of(String requestId, String imageUrl) {
        return InferenceRequest.builder().requestId(requestId).imageUrl(imageUrl).build();
    }

    public static InferenceRequest of(String imageUrl) {
        return of(UUID.randomUUID().toString(), imageUrl);
    }
}
