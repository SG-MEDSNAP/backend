package backend.medsnap.domain.auth.exception;

import backend.medsnap.global.exception.BusinessException;
import backend.medsnap.global.exception.ErrorCode;

public class InvalidJwtTokenException extends BusinessException {

    public InvalidJwtTokenException() {
        super(ErrorCode.AUTH_INVALID_JWT_TOKEN);
    }
}
