package backend.medsnap.domain.medicationRecord.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import backend.medsnap.domain.medicationRecord.entity.MedicationRecord;

@Repository
public interface MedicationRecordRepository extends JpaRepository<MedicationRecord, Long> {

    /** 당일 복약 내역 (삭제되지 않은 것만) */
    @Query(
            """
        SELECT mr FROM MedicationRecord mr
        JOIN FETCH mr.medication m
        WHERE m.user.id = :userId
        AND mr.recordDate = :recordDate
        AND mr.deletedAt IS NULL
        ORDER BY mr.doseTime ASC
        """)
    List<MedicationRecord> findByUserRecord(
            @Param("userId") Long userId, @Param("recordDate") LocalDate recordDate);

    /** [멱등성 체크용] 복약 내역 생성 중복 방지 (삭제되지 않은 것만) */
    @Query("SELECT COUNT(mr) > 0 FROM MedicationRecord mr WHERE mr.medication.id = :medicationId AND mr.doseTime = :doseTime AND mr.recordDate = :recordDate AND mr.deletedAt IS NULL")
    boolean existsByMedication_IdAndDoseTimeAndRecordDate(
            @Param("medicationId") Long medicationId, @Param("doseTime") LocalTime doseTime, @Param("recordDate") LocalDate recordDate);

    /** [스케줄러용] 기존 기록 키 일괄 조회 (삭제되지 않은 것만) */
    @Query(
            value =
                    "SELECT CONCAT(m.id, '_', TO_CHAR(mr.dose_time, 'HH24:MI:SS')) "
                            + "FROM medication_records mr "
                            + "JOIN medications m ON mr.medication_id = m.id "
                            + "WHERE mr.record_date >= :start AND mr.record_date <= :end "
                            + "AND m.id IN (:medicationIds) "
                            + "AND mr.deleted_at IS NULL "
                            + "AND m.deleted_at IS NULL",
            nativeQuery = true)
    Set<String> findExistingRecordKeys(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("medicationIds") List<Long> medicationIds);

    /** 달력 도트 조회 (월별 날짜 목록) (삭제되지 않은 것만) */
    @Query(
            """
        SELECT DISTINCT mr.recordDate FROM MedicationRecord mr
        JOIN mr.medication m
        WHERE m.user.id = :userId
        AND mr.recordDate >= :startDate AND mr.recordDate <= :endDate
        AND mr.deletedAt IS NULL
        """)
    Set<LocalDate> findDatesByMonth(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /** 알림 시간 업데이트용 - 사용자, 날짜, 시간으로 복약 기록 조회 */
    @Query(
            """
        SELECT mr FROM MedicationRecord mr
        JOIN FETCH mr.medication m
        WHERE m.user.id = :userId
        AND mr.recordDate = :recordDate
        AND mr.doseTime = :doseTime
        AND mr.deletedAt IS NULL
        """)
    List<MedicationRecord> findByMedicationUserAndRecordDateAndDoseTime(
            @Param("userId") Long userId,
            @Param("recordDate") LocalDate recordDate,
            @Param("doseTime") LocalTime doseTime);
}
