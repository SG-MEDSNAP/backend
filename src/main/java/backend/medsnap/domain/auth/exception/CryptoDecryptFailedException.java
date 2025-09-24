package backend.medsnap.domain.auth.exception;

import backend.medsnap.global.exception.BusinessException;
import backend.medsnap.global.exception.ErrorCode;

/** 복호화 실패 */
public class CryptoDecryptFailedException extends BusinessException {

    public CryptoDecryptFailedException() {
        super(ErrorCode.AUTH_CRYPTO_DECRYPT_FAIL);
    }
}
