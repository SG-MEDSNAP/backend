package backend.medsnap.domain.alarm.service;

import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.medsnap.domain.alarm.entity.Alarm;
import backend.medsnap.domain.alarm.entity.DayOfWeek;
import backend.medsnap.domain.alarm.repository.AlarmRepository;
import backend.medsnap.domain.medication.entity.Medication;
import backend.medsnap.global.exception.BusinessException;
import backend.medsnap.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;

    /** 알람 생성 */
    public void createAlarms(
            Medication medication, List<String> doseTimes, List<DayOfWeek> doseDays) {
        List<DayOfWeek> expandedDays = DayOfWeek.expandDays(doseDays);

        List<Alarm> alarms =
                expandedDays.stream()
                        .distinct()
                        .flatMap(
                                day ->
                                        doseTimes.stream()
                                                .distinct()
                                                .map(
                                                        timeStr -> {
                                                            try {
                                                                LocalTime time =
                                                                        LocalTime.parse(timeStr);
                                                                return createAlarm(
                                                                        medication, time, day);
                                                            } catch (
                                                                    java.time.format
                                                                                    .DateTimeParseException
                                                                            e) {
                                                                throw new BusinessException(
                                                                        ErrorCode.COMMON_VALIDATION_ERROR,
                                                                        "잘못된 시간 형식: " + timeStr);
                                                            }
                                                        }))
                        .toList();

        medication.getAlarms().addAll(alarms);
        log.info("약 ID: {}에 대해 {}개의 알람이 생성되었습니다.", medication.getId(), alarms.size());
    }

    /** 단일 알람 생성 */
    private Alarm createAlarm(Medication medication, LocalTime time, DayOfWeek day) {
        return Alarm.builder().doseTime(time).dayOfWeek(day).medication(medication).build();
    }

    /** 선택된 알람들 삭제 */
    @Transactional
    public void deleteAlarm(Medication medication, List<Long> alarmIds) {
        log.info("약 ID: {}의 선택된 알람 삭제 시작 - 요청된 알람 IDs: {}", medication.getId(), alarmIds);

        // 존재하는 알람 ID 조회
        List<Long> existingAlarmIds =
                alarmRepository.findExistingAlarmIds(alarmIds, medication.getId());

        // 삭제
        if (!existingAlarmIds.isEmpty()) {
            int deletedCount =
                    alarmRepository.deleteByIdsAndMedicationId(
                            existingAlarmIds, medication.getId());
            log.info("약 ID: {}의 {}개 알람이 삭제되었습니다.", medication.getId(), deletedCount);
        }
    }
}
