package backend.medsnap.domain.medication.entity;

import java.time.LocalTime;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;

import backend.medsnap.global.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MedicationAlarm extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 복용 시간
    @JsonFormat(pattern = "HH:mm")
    @Column(nullable = false)
    private LocalTime doseTime;

    // 복용 요일
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek dayOfWeek;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_id", nullable = false)
    private Medication medication;

    @Builder
    public MedicationAlarm(LocalTime doseTime, DayOfWeek dayOfWeek, Medication medication) {
        this.doseTime = doseTime;
        this.dayOfWeek = dayOfWeek;
        this.medication = medication;
    }
}
