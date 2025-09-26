package backend.medsnap.domain.medicationRecord.repository;

import backend.medsnap.domain.medicationRecord.entity.MedicationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.LocalTime;
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

    /**
     * 특정 사용자의 특정 날짜 범위에 생성된 복약 기록 조회 (createdAt 기준)
     */
    @Query("""
        SELECT mr FROM MedicationRecord mr
        JOIN FETCH mr.medication m
        WHERE m.user.id = :userId
        AND mr.createdAt >= :start AND mr.createdAt < :end
        ORDER BY mr.doseTime ASC
        """)
    List<MedicationRecord> findByUserAndCreatedAtRange(
            @Param("userId") Long userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    /**
     * [멱등성 체크용] 특정 약, 특정 복용 시간 조합이 오늘 날짜에 이미 존재하는지 확인
     * PENDING 상태의 레코드를 createdAt 기준으로 확인
     */
    @Query("""
        SELECT COUNT(mr) > 0 FROM MedicationRecord mr
        JOIN mr.medication m
        WHERE m.id = :medicationId
          AND mr.doseTime = :doseTime
          AND mr.status = 'PENDING'
          AND mr.createdAt BETWEEN :start AND :end 
    """)
    boolean existsRecordForScheduledDay(
            @Param("medicationId") Long medicationId,
            @Param("doseTime") LocalTime doseTime,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
