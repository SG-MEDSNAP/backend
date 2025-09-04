package backend.medsnap.global.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ApiResponse<T> {
    private final Boolean success;
    private final T data;
    private final String error;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(String errorMessage) {
        return ApiResponse.<T>builder()
                .success(false)
                .data(null)
                .error(errorMessage)
                .build();
    }
}
