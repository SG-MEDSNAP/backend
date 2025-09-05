package backend.medsnap.domain.medication.repository;

import backend.medsnap.domain.medication.entity.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicationRepository extends JpaRepository<Medication, Long> {

    /**
     * 약 이름 중복 체크
     */
    boolean existsByName(String name);

    /**
     * 약 이름으로 조회
     */
    Optional<Medication> findByName(String name);
}
