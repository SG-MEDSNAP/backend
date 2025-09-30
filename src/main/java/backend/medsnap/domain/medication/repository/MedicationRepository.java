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

    /** 약 이름 중복 체크 (삭제되지 않은 것만) */
    @Query("SELECT COUNT(m) > 0 FROM Medication m WHERE m.name = :name AND m.user.id = :userId AND m.deletedAt IS NULL")
    boolean existsByNameAndUserId(@Param("name") String name, @Param("userId") Long userId);

    /** 특정 ID를 제외한 약 이름 중복 체크 (삭제되지 않은 것만) */
    @Query("SELECT COUNT(m) > 0 FROM Medication m WHERE m.name = :name AND m.user.id = :userId AND m.id != :id AND m.deletedAt IS NULL")
    boolean existsByNameAndUserIdAndIdNot(@Param("name") String name, @Param("userId") Long userId, @Param("id") Long id);

    /** 사용자의 특정 약 조회 (삭제되지 않은 것만) */
    @Query("SELECT m FROM Medication m WHERE m.id = :medicationId AND m.user.id = :userId AND m.deletedAt IS NULL")
    Optional<Medication> findByIdAndUserId(@Param("medicationId") Long medicationId, @Param("userId") Long userId);

    /** 사용자의 모든 약 목록 조회 (삭제되지 않은 것만) */
    @Query(
            """
        SELECT DISTINCT m
        FROM Medication m
        LEFT JOIN FETCH m.alarms a
        JOIN FETCH m.user u
        WHERE m.user.id = :userId
        AND m.deletedAt IS NULL
        """)
    List<Medication> findByUserIdWithAlarms(@Param("userId") Long userId);
}
