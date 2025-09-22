package backend.medsnap.infra.oauth.exception;

import backend.medsnap.global.exception.BusinessException;
import backend.medsnap.global.exception.ErrorCode;

public class JwkProviderInitializationException extends BusinessException {

    public JwkProviderInitializationException(Throwable cause) {
        super(ErrorCode.AUTH_OIDC_INVALID, "JWK Provider 초기화에 실패했습니다.", cause);
    }
}
