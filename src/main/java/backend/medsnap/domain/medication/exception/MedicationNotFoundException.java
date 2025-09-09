package backend.medsnap.domain.medication.exception;

import backend.medsnap.global.exception.ErrorCode;

/** 약 정보를 찾을 수 없을 때 발생하는 예외 */
public class MedicationNotFoundException extends MedicationException {

    public MedicationNotFoundException() {
        super(ErrorCode.MED_NOT_FOUND);
    }
}
