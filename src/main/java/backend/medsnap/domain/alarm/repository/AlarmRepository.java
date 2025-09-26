package backend.medsnap.domain.alarm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import backend.medsnap.domain.alarm.entity.Alarm;
import backend.medsnap.domain.alarm.entity.DayOfWeek;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    /** 특정 약의 남은 알람 개수 조회 */
    int countByMedicationId(Long medicationId);

    /** 특정 약의 선택된 알람들 삭제 (개별 알람 삭제용) */
    @Modifying
    @Query("DELETE FROM Alarm a WHERE a.id IN :alarmIds AND a.medication.id = :medicationId")
    int deleteByIdsAndMedicationId(
            @Param("alarmIds") List<Long> alarmIds, @Param("medicationId") Long medicationId);

    /** 특정 약의 선택된 알람들 중 존재하는 알람 ID 조회 (개별 알람 삭제용) */
    @Query("SELECT a.id FROM Alarm a WHERE a.id IN :alarmIds AND a.medication.id = :medicationId")
    List<Long> findExistingAlarmIds(
            @Param("alarmIds") List<Long> alarmIds, @Param("medicationId") Long medicationId);

    /** 특정 약에 연결된 모든 알람 삭제 */
    @Modifying
    @Query("DELETE FROM Alarm a WHERE a.medication.id = :medicationId")
    void deleteByMedicationId(@Param("medicationId") Long medicationId);

    /** 특정 사용자의 특정 요일 알람 조회 */
    @Query(
            """
        SELECT a FROM Alarm a
        JOIN FETCH a.medication m
        WHERE m.user.id = :userId
        AND a.dayOfWeek = :dayOfWeek
        ORDER BY a.doseTime ASC
        """)
    List<Alarm> findByUserAndDay(
            @Param("userId") Long userId, @Param("dayOfWeek") DayOfWeek dayOfWeek);

    /** 스케줄러용: 특정 요일의 모든 알람 조회 */
    @Query(
            """
        SELECT a FROM Alarm a
        JOIN FETCH a.medication m
        JOIN FETCH m.user u
        WHERE a.dayOfWeek = :dayOfWeek
        ORDER BY m.id, a.doseTime ASC
        """)
    List<Alarm> findAllByDayOfWeek(@Param("dayOfWeek") DayOfWeek dayOfWeek);
}
