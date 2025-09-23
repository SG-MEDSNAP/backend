package backend.medsnap.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // Common
    COMMON_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C001", "내부 서버 오류가 발생했습니다."),
    COMMON_VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "C002", "입력값 검증에 실패했습니다."),

    // Medication
    MED_NOT_FOUND(HttpStatus.NOT_FOUND, "M001", "약 정보를 찾을 수 없습니다."),
    MED_DUPLICATE_NAME(HttpStatus.CONFLICT, "M002", "이미 등록된 약 이름입니다."),
    MED_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "M003", "약 등록에 실패했습니다."),

    // S3 File
    S3_UPLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "S001", "파일 업로드에 실패했습니다."),
    S3_DELETE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "S002", "파일 삭제에 실패했습니다."),

    // FAQ
    FAQ_NOT_FOUND(HttpStatus.NOT_FOUND, "F001", "FAQ 정보를 찾을 수 없습니다."),

    // Authentication
    AUTH_OIDC_INVALID(HttpStatus.UNAUTHORIZED, "A001", "유효하지 않은 아이디 토큰입니다."),
    AUTH_SOCIAL_ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "A002", "가입되지 않은 소셜 계정입니다."),
    AUTH_SOCIAL_ACCOUNT_ALREADY_EXISTS(HttpStatus.CONFLICT, "A003", "이미 가입된 소셜 계정입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
