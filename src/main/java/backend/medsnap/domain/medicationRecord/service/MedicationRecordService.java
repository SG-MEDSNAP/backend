package backend.medsnap.domain.medicationRecord.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import backend.medsnap.domain.medicationRecord.exception.MedicationRecordException;
import backend.medsnap.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.medsnap.domain.alarm.entity.Alarm;
import backend.medsnap.domain.alarm.entity.DayOfWeek;
import backend.medsnap.domain.alarm.repository.AlarmRepository;
import backend.medsnap.domain.medication.entity.Medication;
import backend.medsnap.domain.medicationRecord.dto.response.DayListResponse;
import backend.medsnap.domain.medicationRecord.entity.MedicationRecord;
import backend.medsnap.domain.medicationRecord.entity.MedicationRecordStatus;
import backend.medsnap.domain.medicationRecord.repository.MedicationRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MedicationRecordService {

    private final MedicationRecordRepository medicationRecordRepository;
    private final AlarmRepository alarmRepository;

    private static final Duration GRACE_PERIOD = Duration.ofMinutes(45);

    /**
     * 특정 월에 복약 기록이 있는 모든 날짜를 조회 (달력 점 표시 기준)
     */
    @Transactional(readOnly = true)
    public Set<LocalDate> getDatesWithRecordsByMonth(Long userId, int year, int month) {
        log.info("사용자 ID: {}의 {}년 {}월 복약 기록 날짜 조회 시작", userId, year, month);

        if (year < 2000 || month < 1 || month > 12) {
            throw new MedicationRecordException(ErrorCode.COMMON_VALIDATION_ERROR, "유효하지 않은 년도 또는 월 정보입니다.");
        }

        // 조회 기간 설정
        LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
        LocalDate lastDayOfMonth = firstDayOfMonth.with(TemporalAdjusters.lastDayOfMonth());

        Set<LocalDate> dates = medicationRecordRepository.findDatesByMonth(
                userId, firstDayOfMonth, lastDayOfMonth);

        log.info("조회 완료: 총 {}개의 복약 기록 날짜를 찾았습니다.", dates.size());

        return dates;
    }

    /** 약 등록 시 당일의 복약 기록 생성 (오늘 등록한 약만) */
    @Transactional
    public void createTodayRecordsForMedication(Medication medication) {
        LocalDate today = LocalDate.now();
        DayOfWeek todayDayOfWeek = convertJavaToDayOfWeek(today.getDayOfWeek());

        // 오늘 요일에 해당하는 알람들만 조회
        List<Alarm> todayAlarms =
                medication.getAlarms().stream()
                        .filter(alarm -> alarm.getDayOfWeek() == todayDayOfWeek)
                        .toList();

        if (todayAlarms.isEmpty()) {
            log.info("약 ID: {} - 오늘({})에 해당하는 알람이 없어 복약 기록을 생성하지 않습니다.", medication.getId(), today);
            return;
        }

        // 각 알람에 대해 복약 기록 생성
        List<MedicationRecord> records =
                todayAlarms.stream()
                        .filter(
                                alarm -> {

                                    // 이미 존재하는지 체크 (중복 방지)
                                    boolean exists =
                                            medicationRecordRepository.existsByMedication_IdAndDoseTimeAndRecordDate(
                                                    medication.getId(),
                                                    alarm.getDoseTime(),
                                                    today);
                                    if (exists) {
                                        log.debug(
                                                "약 ID: {}, 시간: {} - 이미 기록이 존재하여 건너뜀",
                                                medication.getId(),
                                                alarm.getDoseTime());
                                    }
                                    return !exists;
                                })
                        .map(
                                alarm -> {
                                    MedicationRecordStatus status =
                                            determineStatus(null, today, alarm.getDoseTime());

                                    return MedicationRecord.builder()
                                            .medication(medication)
                                            .status(status)
                                            .doseTime(alarm.getDoseTime())
                                            .recordDate(today)
                                            .build();
                                })
                        .toList();

        if (!records.isEmpty()) {
            medicationRecordRepository.saveAll(records);
            log.info(
                    "약 ID: {} - 오늘({})에 대한 {}개의 복약 기록이 생성되었습니다.",
                    medication.getId(),
                    today,
                    records.size());
        } else {
            log.info("약 ID: {} - 오늘({})에 생성할 새로운 기록이 없습니다.", medication.getId(), today);
        }
    }

    @Transactional(readOnly = true)
    public DayListResponse getDayList(Long userId, LocalDate date) {

        log.info("사용자 ID: {}의 {}일 복용 목록 조회", userId, date);

        // 해당 요일의 모든 알람 조회
        DayOfWeek dayOfWeek = convertJavaToDayOfWeek(date.getDayOfWeek());
        List<Alarm> alarms = alarmRepository.findByUserAndDay(userId, dayOfWeek);

        // 해당 날짜의 모든 복용 기록 조회
        List<MedicationRecord> records = medicationRecordRepository.findByUserRecord(userId, date);

        // (약 ID, 복용 시간) 조합 기록 매핑
        Map<String, MedicationRecord> recordMap =
                records.stream()
                        .collect(
                                Collectors.toMap(
                                        record ->
                                                createRecordKey(
                                                        record.getMedication().getId(),
                                                        record.getDoseTime()),
                                        record -> record,
                                        (existing, replacement) -> existing // 중복 시 첫 번째 유지
                                        ));

        // 알람 기준으로 아이템 생성 (약 등록일부터 오늘까지만 포함)
        LocalDate today = LocalDate.now();
        List<DayListResponse.Item> items =
                alarms.stream()
                        .filter(
                                alarm -> {
                                    // 약이 언제 등록되었는지 확인
                                    LocalDate medicationCreatedDate =
                                            alarm.getMedication().getCreatedAt().toLocalDate();

                                    // 두 가지 조건을 모두 만족해야 표시:
                                    // 1. 조회 날짜가 약 등록일 이후여야 함 (약이 존재했던 날)
                                    // 2. 조회 날짜가 오늘 이후가 아니어야 함 (미래 날짜 제외)
                                    boolean afterCreationDate =
                                            !date.isBefore(medicationCreatedDate);
                                    boolean notFutureDate = !date.isAfter(today);

                                    return afterCreationDate && notFutureDate;
                                })
                        .map(
                                alarm -> {
                                    String key =
                                            createRecordKey(
                                                    alarm.getMedication().getId(),
                                                    alarm.getDoseTime());
                                    MedicationRecord record = recordMap.get(key);

                                    MedicationRecordStatus status =
                                            determineStatus(record, date, alarm.getDoseTime());

                                    DayListResponse.Item.ItemBuilder itemBuilder =
                                            DayListResponse.Item.builder()
                                                    .alarmTime(alarm.getDoseTime())
                                                    .medicationId(alarm.getMedication().getId())
                                                    .medicationName(alarm.getMedication().getName())
                                                    .status(status);

                                    if (record != null) {
                                        itemBuilder
                                                .recordId(record.getId())
                                                .imageUrl(record.getImageUrl())
                                                .checkedAt(record.getCheckedAt())
                                                .firstAlarmAt(record.getFirstAlarmAt())
                                                .secondAlarmAt(record.getSecondAlarmAt())
                                                .caregiverNotifiedAt(
                                                        record.getCaregiverNotifiedAt());
                                    }

                                    return itemBuilder.build();
                                })
                        .sorted(Comparator.comparing(DayListResponse.Item::getAlarmTime))
                        .toList();

        log.info("사용자 ID: {}의 {}일 복용 목록 조회 완료 - 총 {}개 항목", userId, date, items.size());

        return DayListResponse.builder().date(date).items(items).build();
    }

    /** (약 ID, 복용 시간) 조합으로 키 생성 */
    private String createRecordKey(Long medicationId, LocalTime doseTime) {
        return medicationId + "_" + doseTime.toString();
    }

    /** 복용 상태 결정 */
    private MedicationRecordStatus determineStatus(
            MedicationRecord record, LocalDate date, LocalTime alarmTime) {
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

    /** Java DayOfWeek를 도메인 DayOfWeek로 변환 */
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
