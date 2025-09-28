package backend.medsnap.domain.pushToken.exception;

import backend.medsnap.global.exception.BusinessException;
import backend.medsnap.global.exception.ErrorCode;

public class PushTokenException extends BusinessException {

    public PushTokenException(ErrorCode errorCode) {
        super(errorCode);
    }
}
