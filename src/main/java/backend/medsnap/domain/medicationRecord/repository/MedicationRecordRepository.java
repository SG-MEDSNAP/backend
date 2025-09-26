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

    /** 당일 복약 내역 */
    @Query(
            """
        SELECT mr FROM MedicationRecord mr
        JOIN FETCH mr.medication m
        WHERE m.user.id = :userId
        AND mr.recordDate = :recordDate
        ORDER BY mr.doseTime ASC
        """)
    List<MedicationRecord> findByUserRecord(
            @Param("userId") Long userId, @Param("recordDate") LocalDate recordDate);

    /** [멱등성 체크용] 복약 내역 생성 중복 방지 */
    boolean existsByMedication_IdAndDoseTimeAndRecordDate(
            Long medicationId, LocalTime doseTime, LocalDate recordDate);

    /** [스케줄러용] 기존 기록 키 일괄 조회 */
    @Query(
            value =
                    "SELECT CONCAT(m.id, '_', TO_CHAR(mr.dose_time, 'HH24:MI:SS')) "
                            + "FROM medication_records mr "
                            + "JOIN medications m ON mr.medication_id = m.id "
                            + "WHERE mr.record_date >= :start AND mr.record_date <= :end "
                            + "AND m.id IN :medicationIds",
            nativeQuery = true)
    Set<String> findExistingRecordKeys(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("medicationIds") List<Long> medicationIds);

    /** 달력 도트 조회 (월별 날짜 목록) */
    @Query(
            """
        SELECT DISTINCT mr.recordDate FROM MedicationRecord mr
        JOIN mr.medication m
        WHERE m.user.id = :userId
        AND mr.recordDate >= :startDate AND mr.recordDate <= :endDate
        """)
    Set<LocalDate> findDatesByMonth(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
