package backend.medsnap.domain.auth.exception;

import backend.medsnap.global.exception.BusinessException;
import backend.medsnap.global.exception.ErrorCode;

/** 키 형식/길이 오류 */
public class CryptoKeyInvalidException extends BusinessException {

    public CryptoKeyInvalidException() {
        super(ErrorCode.AUTH_CRYPTO_KEY_INVALID);
    }
}
