package backend.medsnap.domain.medication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import backend.medsnap.domain.medication.entity.Medication;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicationRepository extends JpaRepository<Medication, Long> {

    /** 약 이름 중복 체크 */
    boolean existsByNameAndUserId(String name, Long userId);

    /** 특정 ID를 제외한 약 이름 중복 체크 */
    boolean existsByNameAndUserIdAndIdNot(String name, Long userId, Long id);

    /** 사용자의 특정 약 조회 */
    Optional<Medication> findByIdAndUserId(Long medicationId, Long userId);
}
