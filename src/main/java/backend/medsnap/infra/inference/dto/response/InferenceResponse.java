package backend.medsnap.infra.inference.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private Result result;
    private ErrorInfo error;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Result {
        private Boolean has_medicine;
        private Double confidence;
        private ModelInfo model_info;
        private TimingMs timing_ms;
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
