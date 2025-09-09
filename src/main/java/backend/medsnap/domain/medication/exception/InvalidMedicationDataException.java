package backend.medsnap.domain.medication.exception;

import backend.medsnap.global.exception.ErrorCode;

/** 약 데이터가 유효하지 않을 때 발생하는 예외 */
public class InvalidMedicationDataException extends MedicationException {

    public InvalidMedicationDataException(ErrorCode errorCode) {
        super(errorCode);
    }

    // 중복 이름 예외
    public static InvalidMedicationDataException duplicateName(String name) {
        return new InvalidMedicationDataException(ErrorCode.MED_DUPLICATE_NAME);
    }

    // 약 생성 실패 예외
    public static InvalidMedicationDataException creationFailed() {
        return new InvalidMedicationDataException(ErrorCode.MED_CREATION_FAILED);
    }
}
