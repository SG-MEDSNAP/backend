package backend.medsnap.scheduler;

import backend.medsnap.domain.alarm.entity.Alarm;
import backend.medsnap.domain.alarm.entity.DayOfWeek;
import backend.medsnap.domain.alarm.repository.AlarmRepository;
import backend.medsnap.domain.medicationRecord.entity.MedicationRecord;
import backend.medsnap.domain.medicationRecord.entity.MedicationRecordStatus;
import backend.medsnap.domain.medicationRecord.repository.MedicationRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MedicationRecordScheduler {

    private final MedicationRecordRepository medicationRecordRepository;
    private final AlarmRepository alarmRepository;

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    @Transactional
    public void createTodayMedicationRecord() {
        log.info("--- [스케줄러 시작] 오늘 복약 예정 기록 생성 (KST 자정) ---");

        // 오늘 날짜와 요일 확인
        LocalDate todayDate = LocalDate.now();
        DayOfWeek todayDayOfWeek = convertJavaToDomain(todayDate.getDayOfWeek());

        // 오늘 요일에 해당하는 모든 알람 조회
        List<Alarm> todayAlarms = alarmRepository.findAllByDayOfWeek(todayDayOfWeek);

        if (todayAlarms.isEmpty()) {
            log.info("{}는 예약된 복약 알람이 없습니다. 스케줄러 종료.", todayDate);
            return;
        }

        // 멱등성 체크 및 등록일 필터링을 위한 시간 범위 설정
        LocalDateTime startOfDay = todayDate.atStartOfDay();
        LocalDateTime endOfDay = todayDate.plusDays(1).atStartOfDay().minusNanos(1);

        List<MedicationRecord> recordsToSave = todayAlarms.stream()
                .filter(alarm -> {
                    Long medicationId = alarm.getMedication().getId();
                    LocalTime doseTime = alarm.getDoseTime();

                    // [멱등성 체크] (약ID + doseTime) 조합으로 오늘 이미 기록이 있는지 확인
                    boolean exists = medicationRecordRepository.existsRecordForScheduledDay(
                            medicationId, doseTime, startOfDay, endOfDay);

                    if (exists) {
                        log.debug("Skipping: MedicationId={}의 {} 시각에 이미 기록이 존재합니다.", medicationId, doseTime);
                        return false;
                    }

                    // [등록일 필터링] 약 등록일이 오늘 이후이면 건너뜀
                    LocalDate medicationCreatedDate = alarm.getMedication().getCreatedAt().toLocalDate();
                    if (todayDate.isBefore(medicationCreatedDate)) {
                        log.debug("Skipping: 약 등록일 {}이 오늘 {}보다 미래입니다.", medicationCreatedDate, todayDate);
                        return false;
                    }

                    return true;
                })
                .map(alarm -> {
                    // 3. MedicationRecord 엔티티 생성
                    return MedicationRecord.builder()
                            .medication(alarm.getMedication())
                            .status(MedicationRecordStatus.PENDING)
                            .doseTime(alarm.getDoseTime())
                            .build();
                })
                .toList();

        // 4. DB에 일괄 저장
        if (!recordsToSave.isEmpty()) {
            medicationRecordRepository.saveAll(recordsToSave);
            log.info("스케줄링 성공: {}개의 복약 예정 기록 (PENDING)이 {} 날짜로 생성되었습니다.",
                    recordsToSave.size(), todayDate);
        } else {
            log.info("추가로 생성할 복약 예정 기록이 없습니다. 스케줄러 종료.");
        }
    }

    /**
     * Java DayOfWeek를 도메인 DayOfWeek로 변환
     */
    private DayOfWeek convertJavaToDomain(java.time.DayOfWeek javaDayOfWeek) {
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
