package backend.medsnap.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // Common
    COMMON_INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "C001", "잘못된 요청 파라미터입니다."),
    COMMON_ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "C002", "요청한 리소스를 찾을 수 없습니다."),
    COMMON_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C003", "내부 서버 오류가 발생했습니다."),
    COMMON_METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C004", "허용되지 않은 HTTP 메서드입니다."),
    COMMON_VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "C005", "입력값 검증에 실패했습니다."),

    // Medication
    MED_NOT_FOUND(HttpStatus.NOT_FOUND, "M001", "약 정보를 찾을 수 없습니다."),
    MED_INVALID_NAME(HttpStatus.BAD_REQUEST, "M002", "유효하지 않은 약 이름입니다."),
    MED_INVALID_IMAGE_URL(HttpStatus.BAD_REQUEST, "M003", "유효하지 않은 이미지 URL입니다."),
    MED_INVALID_DOSE_TIME(HttpStatus.BAD_REQUEST, "M004", "유효하지 않은 복용 시간입니다."),
    MED_INVALID_DOSE_DAY(HttpStatus.BAD_REQUEST, "M005", "유효하지 않은 복용 요일입니다."),
    MED_DUPLICATE_NAME(HttpStatus.CONFLICT, "M006", "이미 등록된 약 이름입니다."),
    MED_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "M007", "약 등록에 실패했습니다."),

    // S3 File Upload
    S3_UPLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "S001", "파일 업로드에 실패했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return status;
    }
}
