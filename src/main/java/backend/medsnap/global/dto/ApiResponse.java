package backend.medsnap.global.dto;

import backend.medsnap.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;

@Getter
@Builder
@AllArgsConstructor
public class ApiResponse<T> {
    private final String code;
    private final Integer httpStatus;
    private final String message;
    private final T data;
    private final Object error;

    // 성공 응답
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .code("SUCCESS")
                .httpStatus(HttpStatus.OK.value())
                .message("요청이 성공적으로 처리되었습니다.")
                .data(data)
                .build();
    }

    // 에러 응답 (ErrorCode)
    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return ApiResponse.<T>builder()
                .code(errorCode.getCode())
                .httpStatus(errorCode.getStatus().value())
                .message(errorCode.getMessage())
                .data(null)
                .build();
    }

    // 에러 응답 (Error data)
    public static <T> ApiResponse<T> error(ErrorCode errorCode, T data) {
        return ApiResponse.<T>builder()
                .code(errorCode.getCode())
                .httpStatus(errorCode.getStatus().value())
                .message(errorCode.getMessage())
                .data(data)
                .build();
    }
}
