package backend.medsnap.domain.medication.exception;

import backend.medsnap.global.exception.BusinessException;
import backend.medsnap.global.exception.ErrorCode;

/** 약(Medication) 도메인의 기본 예외 클래스 */
public class MedicationException extends BusinessException {

    public MedicationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
