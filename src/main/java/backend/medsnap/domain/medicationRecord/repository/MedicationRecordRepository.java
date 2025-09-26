package backend.medsnap.domain.medicationRecord.repository;

import backend.medsnap.domain.medicationRecord.entity.MedicationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MedicationRecordRepository extends JpaRepository<MedicationRecord, Long> {


    /**
     * 특정 사용자의 특정 요일 알람 조회
     */
    @Query("""
        SELECT mr FROM MedicationRecord mr
        JOIN FETCH mr.medication m
        WHERE m.user.id = :userId
        AND (
            (mr.firstAlarmAt BETWEEN :start AND :end)
            OR (mr.secondAlarmAt BETWEEN :start AND :end)
            OR (mr.checkedAt BETWEEN :start AND :end)
        )
        ORDER BY
            CASE
                WHEN mr.firstAlarmAt IS NOT NULL THEN mr.firstAlarmAt
                WHEN mr.secondAlarmAt IS NOT NULL THEN mr.secondAlarmAt
                ELSE mr.checkedAt
            END ASC
        """)
    List<MedicationRecord> findByUserAndDateRange(
            @Param("userId") Long userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
