package backend.medsnap.domain.medication.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import backend.medsnap.domain.alarm.entity.Alarm;
import backend.medsnap.global.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "medications")
public class Medication extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "citext", unique = true)
    private String name;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private Boolean notifyCaregiver;

    @Column(nullable = false)
    private Boolean preNotify;

    @OneToMany(
            mappedBy = "medication",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<Alarm> alarms = new ArrayList<>();

    @Builder
    public Medication(String name, String imageUrl, Boolean notifyCaregiver, Boolean preNotify) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.notifyCaregiver = notifyCaregiver;
        this.preNotify = preNotify;
    }

    public void updateMedicationDetails(
            String name, String imageUrl, Boolean notifyCaregiver, Boolean preNotify) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.notifyCaregiver = notifyCaregiver;
        this.preNotify = preNotify;
    }
}
