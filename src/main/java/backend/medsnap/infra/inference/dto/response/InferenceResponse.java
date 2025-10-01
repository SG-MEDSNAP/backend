package backend.medsnap.infra.inference.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InferenceResponse {

    private String requestId;
    private boolean success;
    private String code;
    private String message;
    private Data data;
    private ErrorInfo error;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {
        @JsonProperty("has_medicine")
        private Boolean hasMedicine;
        private Double confidence;
        @JsonProperty("model_info")
        private ModelInfo modelInfo;
        @JsonProperty("timing_ms")
        private TimingMs timingMs;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModelInfo {
        private String name;
        private String version;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimingMs {
        private Double total;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorInfo {
        private String type;
        private Map<String, Object> details;
    }
}
