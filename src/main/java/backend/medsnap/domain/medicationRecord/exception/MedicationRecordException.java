package backend.medsnap.domain.medicationRecord.exception;

import backend.medsnap.global.exception.BusinessException;
import backend.medsnap.global.exception.ErrorCode;

public class MedicationRecordException extends BusinessException {

    public MedicationRecordException(ErrorCode errorCode) {
        super(errorCode);
    }

    public MedicationRecordException(ErrorCode errorCode, String detailMessage) {
        super(errorCode, detailMessage);
    }
}
