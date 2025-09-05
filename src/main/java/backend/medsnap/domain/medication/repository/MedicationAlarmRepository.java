package backend.medsnap.domain.medication.repository;

import backend.medsnap.domain.medication.entity.MedicationAlarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicationAlarmRepository extends JpaRepository<MedicationAlarm, Long> {
}
