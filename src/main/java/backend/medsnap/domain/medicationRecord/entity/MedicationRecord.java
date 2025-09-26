package backend.medsnap.domain.medicationRecord.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;

import backend.medsnap.domain.medication.entity.Medication;
import backend.medsnap.global.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "medication_records")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MedicationRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Medication medication;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MedicationRecordStatus status;

    @JsonFormat(pattern = "HH:mm")
    @Column(nullable = false)
    private LocalTime doseTime;

    private String imageUrl;

    private LocalDateTime checkedAt;

    private LocalDateTime caregiverNotifiedAt;

    private LocalDateTime firstAlarmAt;

    private LocalDateTime secondAlarmAt;

    @Builder
    private MedicationRecord(
            Medication medication,
            MedicationRecordStatus status,
            LocalTime doseTime,
            String imageUrl,
            LocalDateTime checkedAt,
            LocalDateTime caregiverNotifiedAt,
            LocalDateTime firstAlarmAt,
            LocalDateTime secondAlarmAt) {
        this.medication = medication;
        this.status = status != null ? status : MedicationRecordStatus.PENDING;
        this.doseTime = doseTime;
        this.imageUrl = imageUrl;
        this.checkedAt = checkedAt;
        this.caregiverNotifiedAt = caregiverNotifiedAt;
        this.firstAlarmAt = firstAlarmAt;
        this.secondAlarmAt = secondAlarmAt;
    }
}
