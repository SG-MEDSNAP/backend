package backend.medsnap.infra.s3.exception;

import backend.medsnap.global.exception.BusinessException;
import backend.medsnap.global.exception.ErrorCode;

public class S3DeleteFailException extends BusinessException {

    public S3DeleteFailException(String message, Throwable cause) {
        super(ErrorCode.S3_DELETE_FAIL, message, cause);
    }
}
