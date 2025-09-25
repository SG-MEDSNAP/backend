package backend.medsnap.domain.medication.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /** 사용자의 모든 약 목록 조회 */
    @Query(
            "SELECT m FROM Medication m JOIN FETCH m.alarms a JOIN FETCH m.user u WHERE m.user.id = :userId")
    List<Medication> findByUserIdWithAlarms(@Param("userId") Long userId);
}
