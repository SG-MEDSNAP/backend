package backend.medsnap.domain.auth.exception;

import backend.medsnap.global.exception.BusinessException;
import backend.medsnap.global.exception.ErrorCode;

public class SocialAccountNotFoundException extends BusinessException {

    public SocialAccountNotFoundException() {
        super(ErrorCode.AUTH_SOCIAL_ACCOUNT_NOT_FOUND);
    }
}
