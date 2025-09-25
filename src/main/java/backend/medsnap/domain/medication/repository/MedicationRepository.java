package backend.medsnap.domain.medication.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import backend.medsnap.domain.medication.entity.Medication;

@Repository
public interface MedicationRepository extends JpaRepository<Medication, Long> {

    /** 약 이름 중복 체크 */
    boolean existsByNameAndUserId(String name, Long userId);

    /** 특정 ID를 제외한 약 이름 중복 체크 */
    boolean existsByNameAndUserIdAndIdNot(String name, Long userId, Long id);

    /** 사용자의 특정 약 조회 */
    Optional<Medication> findByIdAndUserId(Long medicationId, Long userId);
}
