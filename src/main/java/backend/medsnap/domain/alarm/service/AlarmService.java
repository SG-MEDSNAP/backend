package backend.medsnap.domain.alarm.service;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.medsnap.domain.alarm.dto.response.AlarmDeleteResponse;
import backend.medsnap.domain.alarm.entity.Alarm;
import backend.medsnap.domain.alarm.entity.DayOfWeek;
import backend.medsnap.domain.alarm.repository.AlarmRepository;
import backend.medsnap.domain.medication.entity.Medication;
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
                expandedDays.stream().distinct()
                        .flatMap(
                                day ->
                                        doseTimes.stream().distinct()
                                                .map(
                                                        timeStr -> {
                                                            try {
                                                                LocalTime time = LocalTime.parse(timeStr);
                                                                return createAlarm(medication, time, day);
                                                            } catch (java.time.format.DateTimeParseException e) {
                                                                throw new IllegalArgumentException("잘못된 시간 형식: " + timeStr);
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
    public AlarmDeleteResponse deleteAlarm(Medication medication, List<Long> alarmIds) {
        log.info("약 ID: {}의 선택된 알람 삭제 시작 - 요청된 알람 IDs: {}", medication.getId(), alarmIds);

        // 존재하는 알람 ID 조회
        List<Long> existingAlarmIds =
                alarmRepository.findExistingAlarmIds(alarmIds, medication.getId());
        Set<Long> existingSet = existingAlarmIds.stream().collect(Collectors.toSet());

        // 삭제
        if (!existingAlarmIds.isEmpty()) {
            int deletedCount =
                    alarmRepository.deleteByIdsAndMedicationId(
                            existingAlarmIds, medication.getId());
            log.info("약 ID: {}의 {}개 알람이 삭제되었습니다.", medication.getId(), deletedCount);
        }

        // 응답 생성
        return createDeleteResponse(medication, alarmIds, existingSet);
    }

    /** 약 전체 삭제 응답 생성 */
    public AlarmDeleteResponse createDeleteAllResponse(Medication medication, int alarmCount) {

        return AlarmDeleteResponse.builder()
                .medicationId(medication.getId())
                .medicationName(medication.getName())
                .deletedAlarmCount(alarmCount)
                .failedAlarmCount(0)
                .message("약과 관련된 모든 알람이 삭제되었습니다.")
                .details(null)
                .build();
    }

    /** 삭제 응답 생성 */
    private AlarmDeleteResponse createDeleteResponse(
            Medication medication, List<Long> alarmIds, Set<Long> existingSet) {
        // 각 알람별 상세 정보 생성
        List<AlarmDeleteResponse.AlarmDeleteDetail> details =
                alarmIds.stream()
                        .map(
                                alarmId -> {
                                    AlarmDeleteResponse.DeleteStatus status =
                                            existingSet.contains(alarmId)
                                                    ? AlarmDeleteResponse.DeleteStatus.DELETED
                                                    : AlarmDeleteResponse.DeleteStatus.NOT_FOUND;

                                    return AlarmDeleteResponse.AlarmDeleteDetail.builder()
                                            .alarmId(alarmId)
                                            .status(status)
                                            .build();
                                })
                        .toList();

        // 성공/실패 개수 계산
        int deletedCount =
                (int)
                        details.stream()
                                .filter(
                                        detail ->
                                                detail.getStatus()
                                                        == AlarmDeleteResponse.DeleteStatus.DELETED)
                                .count();
        int failedCount = details.size() - deletedCount;

        // 메시지 생성
        String message =
                failedCount > 0
                        ? String.format(
                                "총 %d개 중 %d개 알람이 삭제되었습니다. %d개는 찾을 수 없어 삭제되지 않았습니다.",
                                details.size(), deletedCount, failedCount)
                        : String.format("요청된 %d개 알람이 모두 삭제되었습니다.", deletedCount);

        return AlarmDeleteResponse.builder()
                .medicationId(medication.getId())
                .medicationName(medication.getName())
                .deletedAlarmCount(deletedCount)
                .failedAlarmCount(failedCount)
                .message(message)
                .details(details)
                .build();
    }
}
