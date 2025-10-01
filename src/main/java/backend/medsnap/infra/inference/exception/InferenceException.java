package backend.medsnap.infra.inference.exception;

import backend.medsnap.global.exception.BusinessException;
import backend.medsnap.global.exception.ErrorCode;

public class InferenceException extends BusinessException {

    public InferenceException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public InferenceException(String message, Throwable cause) {
        super(ErrorCode.INFERENCE_COMMUNICATION_ERROR, message, cause);
    }
}
