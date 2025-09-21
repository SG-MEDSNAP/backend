package backend.medsnap.domain.auth.exception;

import backend.medsnap.global.exception.BusinessException;
import backend.medsnap.global.exception.ErrorCode;

public class SocialAccountAlreadyExistsException extends BusinessException {

    public SocialAccountAlreadyExistsException() {
        super(ErrorCode.AUTH_SOCIAL_ACCOUNT_ALREADY_EXISTS);
    }
}
