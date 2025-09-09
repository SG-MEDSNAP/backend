package backend.medsnap.domain.medication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import backend.medsnap.domain.medication.entity.MedicationAlarm;

@Repository
public interface MedicationAlarmRepository extends JpaRepository<MedicationAlarm, Long> {}
