package backend.medsnap.domain.faq.exception;

import backend.medsnap.global.exception.BusinessException;
import backend.medsnap.global.exception.ErrorCode;

public class FaqException extends BusinessException {

    public FaqException(ErrorCode errorCode) {
        super(errorCode);
    }
}
