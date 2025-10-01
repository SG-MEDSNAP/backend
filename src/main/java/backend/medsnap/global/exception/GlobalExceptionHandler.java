package backend.medsnap.global.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import backend.medsnap.global.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * @Valid 검증 실패 처리 (@RequestBody)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(
            MethodArgumentNotValidException e) {

        log.warn("Validation error occurred: {}", e.getMessage());

        ApiResponse<Object> response =
                ApiResponse.error(ErrorCode.COMMON_VALIDATION_ERROR, "입력값 검증에 실패했습니다.");

        return ResponseEntity.status(ErrorCode.COMMON_VALIDATION_ERROR.getStatus()).body(response);
    }

    /**
     * @Valid 검증 실패 처리 (Method Parameter)
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiResponse<Object>> handleHandlerValidationException(
            HandlerMethodValidationException e) {

        log.warn("Handler method validation error occurred");

        ApiResponse<Object> response = ApiResponse.error(ErrorCode.COMMON_VALIDATION_ERROR);

        return ResponseEntity.status(ErrorCode.COMMON_VALIDATION_ERROR.getStatus()).body(response);
    }

    /** BusinessException 처리 */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException e) {
        log.warn("BusinessException: {}", e.getMessage());

        ErrorCode errorCode = e.getErrorCode();
        ApiResponse<Object> response = ApiResponse.error(errorCode);

        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    /** IllegalArgumentException 처리 (400 Bad Request) */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(
            IllegalArgumentException e) {
        log.warn("IllegalArgumentException: {}", e.getMessage());

        ApiResponse<Object> response =
                ApiResponse.error(ErrorCode.COMMON_VALIDATION_ERROR, e.getMessage());

        return ResponseEntity.status(ErrorCode.COMMON_VALIDATION_ERROR.getStatus()).body(response);
    }

    /** EntityNotFoundException 처리 (404 Not Found) */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleEntityNotFoundException(
            EntityNotFoundException e) {
        log.warn("EntityNotFoundException: {}", e.getMessage());

        ApiResponse<Object> response = ApiResponse.error(ErrorCode.COMMON_NOT_FOUND, null);

        return ResponseEntity.status(ErrorCode.COMMON_NOT_FOUND.getStatus()).body(response);
    }

    /** 기타 모든 예외 처리 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(
            Exception e, HttpServletRequest request) {

        log.error(
                """
                [500 SERVER ERROR]
                Method: {}
                URI: {}
                Query: {}
                RemoteAddr: {}
                Exception: {}
                """,
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString(),
                request.getRemoteAddr(),
                e.toString(),
                e);

        ApiResponse<Object> response = ApiResponse.error(ErrorCode.COMMON_INTERNAL_SERVER_ERROR);

        return ResponseEntity.status(ErrorCode.COMMON_INTERNAL_SERVER_ERROR.getStatus())
                .body(response);
    }
}
