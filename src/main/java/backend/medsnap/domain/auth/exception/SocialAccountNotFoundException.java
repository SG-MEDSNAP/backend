package backend.medsnap.domain.auth.exception;

import backend.medsnap.global.exception.BusinessException;
import backend.medsnap.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class SocialAccountNotFoundException extends BusinessException {

    private final String nameHint;

    public SocialAccountNotFoundException() {
        this(null);
    }

    public SocialAccountNotFoundException(String nameHint) {
        super(ErrorCode.AUTH_SOCIAL_ACCOUNT_NOT_FOUND);
        this.nameHint = nameHint;
    }
}
