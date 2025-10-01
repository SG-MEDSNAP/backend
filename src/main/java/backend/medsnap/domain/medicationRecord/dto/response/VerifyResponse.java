package backend.medsnap.domain.medicationRecord.dto.response;

import backend.medsnap.domain.medicationRecord.entity.MedicationRecord;
import backend.medsnap.domain.medicationRecord.entity.MedicationRecordStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class VerifyResponse {

    private Long recordId;
    private String alarmTime;
    private Long medicationId;
    private String medicationName;
    private MedicationRecordStatus status;
    private String imageUrl;
    private LocalDateTime checkedAt;
    private LocalDateTime firstAlarmAt;
    private LocalDateTime secondAlarmAt;
    private LocalDateTime caregiverNotifiedAt;

    public static VerifyResponse from(MedicationRecord record) {
        return VerifyResponse.builder()
                .recordId(record.getId())
                .alarmTime(record.getDoseTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                .medicationId(record.getMedication().getId())
                .medicationName(record.getMedication().getName())
                .status(record.getStatus())
                .imageUrl(record.getImageUrl())
                .checkedAt(record.getCheckedAt())
                .firstAlarmAt(record.getFirstAlarmAt())
                .secondAlarmAt(record.getSecondAlarmAt())
                .caregiverNotifiedAt(record.getCaregiverNotifiedAt())
                .build();
    }
}
