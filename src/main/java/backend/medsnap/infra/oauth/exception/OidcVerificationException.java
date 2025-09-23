package backend.medsnap.infra.oauth.exception;

import backend.medsnap.global.exception.BusinessException;
import backend.medsnap.global.exception.ErrorCode;

public class OidcVerificationException extends BusinessException {

    public OidcVerificationException(String message, Throwable cause) {
        super(ErrorCode.AUTH_OIDC_INVALID, message, cause);
    }
}
