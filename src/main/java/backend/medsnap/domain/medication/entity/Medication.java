package backend.medsnap.domain.medication.entity;

import backend.medsnap.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "medications")
public class Medication extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "citext")
    private String name;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private Boolean notifyCaregiver;

    @Column(nullable = false)
    private Boolean preNotify;

    @OneToMany(mappedBy = "medication", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MedicationAlarm> alarms = new ArrayList<>();

    @Builder
    public Medication(String name, String imageUrl, Boolean notifyCaregiver, Boolean preNotify) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.notifyCaregiver = notifyCaregiver;
        this.preNotify = preNotify;
    }
}
