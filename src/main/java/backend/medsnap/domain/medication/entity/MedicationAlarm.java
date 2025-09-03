package backend.medsnap.domain.medication.entity;

import backend.medsnap.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MedicationAlarm extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 복용 시간
    private LocalDateTime doseTime;

    // 복용 요일
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_id", insertable = false, updatable = false)
    private Medication medication;

    @Builder
    public MedicationAlarm(LocalDateTime doseTime, DayOfWeek dayOfWeek, Medication medication) {
        this.doseTime = doseTime;
        this.dayOfWeek = dayOfWeek;
        this.medication = medication;
    }
}
