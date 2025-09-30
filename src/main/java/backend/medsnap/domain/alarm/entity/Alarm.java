package backend.medsnap.domain.alarm.entity;

import java.time.LocalTime;

import jakarta.persistence.*;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonFormat;

import backend.medsnap.domain.medication.entity.Medication;
import backend.medsnap.global.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "medication_alarms")
@SQLDelete(sql = "UPDATE medication_alarms SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Alarm extends BaseEntity {

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
    public Alarm(LocalTime doseTime, DayOfWeek dayOfWeek, Medication medication) {
        this.doseTime = doseTime;
        this.dayOfWeek = dayOfWeek;
        this.medication = medication;
    }
}
