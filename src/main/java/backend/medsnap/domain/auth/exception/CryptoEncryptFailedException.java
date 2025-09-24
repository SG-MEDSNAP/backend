package backend.medsnap.domain.auth.exception;

import backend.medsnap.global.exception.BusinessException;
import backend.medsnap.global.exception.ErrorCode;

/** 암호화 실패 */
public class CryptoEncryptFailedException extends BusinessException {

    public CryptoEncryptFailedException() {
        super(ErrorCode.AUTH_CRYPTO_ENCRYPT_FAIL);
    }
}
