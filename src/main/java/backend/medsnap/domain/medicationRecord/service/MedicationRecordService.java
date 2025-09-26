package backend.medsnap.domain.medicationRecord.service;

import backend.medsnap.domain.alarm.entity.Alarm;
import backend.medsnap.domain.alarm.entity.DayOfWeek;
import backend.medsnap.domain.alarm.repository.AlarmRepository;
import backend.medsnap.domain.medicationRecord.dto.response.DayListResponse;
import backend.medsnap.domain.medicationRecord.entity.MedicationRecord;
import backend.medsnap.domain.medicationRecord.entity.MedicationRecordStatus;
import backend.medsnap.domain.medicationRecord.repository.MedicationRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MedicationRecordService {

    private final MedicationRecordRepository medicationRecordRepository;
    private final AlarmRepository alarmRepository;

    private static final Duration GRACE_PERIOD = Duration.ofMinutes(45);

    @Transactional(readOnly = true)
    public DayListResponse getDayList(Long userId, LocalDate date) {

        log.info("사용자 ID: {}의 {}일 복용 목록 조회", userId, date);

        // 해당 요일의 모든 알람 조회
        DayOfWeek dayOfWeek = convertJavaToDayOfWeek(date.getDayOfWeek());
        List<Alarm> alarms = alarmRepository.findByUserAndDay(userId, dayOfWeek);

        // 해당 날짜의 모든 복용 기록 조회
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        List<MedicationRecord> records = medicationRecordRepository
                .findByUserAndDateRange(userId, startOfDay, endOfDay);

        // (약 ID, 복용 시간) 조합 기록 매핑
        Map<String, MedicationRecord> recordMap = records.stream()
                .collect(Collectors.toMap(
                        record -> createRecordKey(record.getMedication().getId(), record.getDoseTime()),
                        record -> record,
                        (existing, replacement) -> existing // 중복 시 첫 번째 유지
                ));

        // 알람 기준으로 아이템 생성
        List<DayListResponse.Item> items = alarms.stream()
                .map(alarm -> {
                    String key = createRecordKey(alarm.getMedication().getId(), alarm.getDoseTime());
                    MedicationRecord record = recordMap.get(key);

                    MedicationRecordStatus status = determineStatus(record, date, alarm.getDoseTime());

                    return DayListResponse.Item.builder()
                            .alarmTime(alarm.getDoseTime())
                            .medicationId(alarm.getMedication().getId())
                            .medicationName(alarm.getMedication().getName())
                            .status(status)
                            .build();
                })
                .sorted(Comparator.comparing(DayListResponse.Item::getAlarmTime))
                .toList();

        log.info("사용자 ID: {}의 {}일 복용 목록 조회 완료 - 총 {}개 항목", userId, date, items.size());

        return DayListResponse.builder()
                .date(date)
                .items(items)
                .build();
    }

    /**
     * (약 ID, 복용 시간) 조합으로 키 생성
     */
    private String createRecordKey(Long medicationId, LocalTime doseTime) {
        return medicationId + "_" + doseTime.toString();
    }

    /**
     * 복용 상태 결정
     */
    private MedicationRecordStatus determineStatus(MedicationRecord record, LocalDate date, LocalTime alarmTime) {
        // 기록이 있으면 DB 저장된 상태 사용
        if (record != null) {
            return record.getStatus();
        }

        // 기록이 없을 때 시간 기준으로 상태 판정
        LocalDateTime alarmDateTime = LocalDateTime.of(date, alarmTime);
        LocalDateTime graceEndTime = alarmDateTime.plus(GRACE_PERIOD);
        LocalDateTime currentTime = LocalDateTime.now();

        if (currentTime.isAfter(graceEndTime)) {
            return MedicationRecordStatus.SKIPPED;
        } else {
            return MedicationRecordStatus.PENDING;
        }
    }

    /**
     * Java DayOfWeek를 도메인 DayOfWeek로 변환
     */
    private DayOfWeek convertJavaToDayOfWeek(java.time.DayOfWeek javaDayOfWeek) {
        return switch (javaDayOfWeek) {
            case MONDAY -> DayOfWeek.MON;
            case TUESDAY -> DayOfWeek.TUE;
            case WEDNESDAY -> DayOfWeek.WED;
            case THURSDAY -> DayOfWeek.THU;
            case FRIDAY -> DayOfWeek.FRI;
            case SATURDAY -> DayOfWeek.SAT;
            case SUNDAY -> DayOfWeek.SUN;
        };
    }
}
