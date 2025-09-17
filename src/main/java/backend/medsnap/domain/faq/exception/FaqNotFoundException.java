package backend.medsnap.domain.faq.exception;

import backend.medsnap.global.exception.ErrorCode;

public class FaqNotFoundException extends FaqException {

    public FaqNotFoundException(Long faqId) {
        super(ErrorCode.FAQ_NOT_FOUND);
    }
}
