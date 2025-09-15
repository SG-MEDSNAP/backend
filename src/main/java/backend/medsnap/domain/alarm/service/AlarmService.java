package backend.medsnap.domain.alarm.service;

import backend.medsnap.domain.alarm.entity.Alarm;
import backend.medsnap.domain.alarm.entity.DayOfWeek;
import backend.medsnap.domain.alarm.repository.AlarmRepository;
import backend.medsnap.domain.medication.entity.Medication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;

    /**
     * 알람 생성
     */
    public void createAlarms(Medication medication, List<String> doseTimes, List<DayOfWeek> doseDays) {
        List<DayOfWeek> expandedDays = DayOfWeek.expandDays(doseDays);

        List<Alarm> alarms = expandedDays.stream()
                .flatMap(day -> doseTimes.stream()
                        .map(timeStr -> {
                            LocalTime time = LocalTime.parse(timeStr);
                            return createAlarm(medication, time, day);
                        }))
                .toList();

        medication.getAlarms().addAll(alarms);
        log.info("약 ID: {}에 대해 {}개의 알람이 생성되었습니다.", medication.getId(), alarms.size());
    }

    /** 단일 알람 생성 */
    private Alarm createAlarm(Medication medication, LocalTime time, DayOfWeek day) {
        return Alarm.builder()
                .doseTime(time)
                .dayOfWeek(day)
                .medication(medication)
                .build();
    }
}
