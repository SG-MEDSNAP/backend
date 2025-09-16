package backend.medsnap.domain.alarm.repository;

import backend.medsnap.domain.alarm.entity.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    /**
     * 특정 약의 선택된 알람들 삭제 (개별 알람 삭제용)
     */
    @Modifying
    @Query("DELETE FROM Alarm a WHERE a.id IN :alarmIds AND a.medication.id = :medicationId")
    int deleteByIdsAndMedicationId(@Param("alarmIds") List<Long> alarmIds, @Param("medicationId") Long medicationId);

    /**
     * 특정 약의 선택된 알람들 중 존재하는 알람 ID 조회 (개별 알람 삭제용)
     */
    @Query("SELECT a.id FROM Alarm a WHERE a.id IN :alarmIds AND a.medication.id = :medicationId")
    List<Long> findExistingAlarmIds(@Param("alarmIds") List<Long> alarmIds, @Param("medicationId") Long medicationId);

    /**
     * 특정 약의 알람들을 ID 목록으로 조회 (개별 알람 삭제용)
     */
    @Query("SELECT a FROM Alarm a WHERE a.id IN :alarmIds AND a.medication.id = :medicationId")
    List<Alarm> findByIdsAndMedicationId(@Param("alarmIds") List<Long> alarmIds, @Param("medicationId") Long medicationId);
}
