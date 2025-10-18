package backend.medsnap.domain.medication.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import backend.medsnap.domain.alarm.entity.Alarm;
import backend.medsnap.domain.medicationRecord.entity.MedicationRecord;
import backend.medsnap.domain.user.entity.User;
import backend.medsnap.global.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "medications")
@SQLDelete(sql = "UPDATE medications SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Medication extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "citext")
    private String name;

    @Column(nullable = false)
    private String imageUrl;

    // @Column(nullable = false)
    // private Boolean notifyCaregiver;

    @Column(nullable = false)
    private Boolean preNotify;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(
            mappedBy = "medication",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<Alarm> alarms = new ArrayList<>();

    @OneToMany(mappedBy = "medication", fetch = FetchType.LAZY)
    private List<MedicationRecord> medicationRecords = new ArrayList<>();

    @Builder
    public Medication(String name, String imageUrl, Boolean preNotify, User user) {
        this.name = name;
        this.imageUrl = imageUrl;
        // this.notifyCaregiver = notifyCaregiver;
        this.preNotify = preNotify;
        this.user = user;
    }

    public void updateMedicationDetails(String name, String imageUrl, Boolean preNotify) {
        this.name = name;
        this.imageUrl = imageUrl;
        // this.notifyCaregiver = notifyCaregiver;
        this.preNotify = preNotify;
    }

    @Override
    public void softDelete() {

        alarms.forEach(Alarm::softDelete);

        super.softDelete();
    }
}
