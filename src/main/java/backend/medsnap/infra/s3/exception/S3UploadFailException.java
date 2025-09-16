package backend.medsnap.infra.s3.exception;

import backend.medsnap.global.exception.BusinessException;
import backend.medsnap.global.exception.ErrorCode;

public class S3UploadFailException extends BusinessException {
    public S3UploadFailException() {
        super(ErrorCode.S3_UPLOAD_FAIL);
    }

    public S3UploadFailException(String message, Throwable cause) {
        super(ErrorCode.S3_UPLOAD_FAIL, message, cause);
    }
}
