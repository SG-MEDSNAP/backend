package backend.medsnap.scheduler;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import backend.medsnap.domain.alarm.entity.Alarm;
import backend.medsnap.domain.alarm.entity.DayOfWeek;
import backend.medsnap.domain.alarm.repository.AlarmRepository;
import backend.medsnap.domain.medicationRecord.entity.MedicationRecord;
import backend.medsnap.domain.medicationRecord.entity.MedicationRecordStatus;
import backend.medsnap.domain.medicationRecord.repository.MedicationRecordRepository;
import backend.medsnap.domain.medicationRecord.service.MedicationRecordService;
import backend.medsnap.domain.notification.dto.request.NotificationCreateRequest;
import backend.medsnap.domain.notification.repository.NotificationRepository;
import backend.medsnap.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class MedicationRecordScheduler {

    private final MedicationRecordRepository medicationRecordRepository;
    private final AlarmRepository alarmRepository;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;
    private final Clock clock;

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    @Transactional
    public void createTodayMedicationRecord() {
        log.info("--- [스케줄러 시작] 오늘 복약 예정 기록 생성 (KST 자정) ---");

        // 오늘 날짜와 요일 확인
        LocalDate todayDate = LocalDate.now(clock);
        DayOfWeek todayDayOfWeek = convertJavaToDomain(todayDate.getDayOfWeek());

        // 오늘 요일에 해당하는 모든 알람 조회
        List<Alarm> todayAlarms = alarmRepository.findAllByDayOfWeek(todayDayOfWeek);

        if (todayAlarms.isEmpty()) {
            log.info("{}는 예약된 복약 알람이 없습니다. 스케줄러 종료.", todayDate);
            return;
        }

        // 멱등성 체크 및 등록일 필터링을 위한 시간 범위 설정
        LocalDateTime startOfDay = todayDate.atStartOfDay();
        LocalDateTime endOfDay = todayDate.plusDays(1).atStartOfDay();

        // 등록일 필터링 (메모리에서 처리)
        List<Alarm> validAlarms =
                todayAlarms.stream()
                        .filter(
                                alarm -> {
                                    LocalDate medicationCreatedDate =
                                            alarm.getMedication().getCreatedAt().toLocalDate();
                                    if (todayDate.isBefore(medicationCreatedDate)) {
                                        log.debug(
                                                "Skipping: 약 등록일 {}이 오늘 {}보다 미래입니다.",
                                                medicationCreatedDate,
                                                todayDate);
                                        return false;
                                    }
                                    return true;
                                })
                        .toList();

        if (validAlarms.isEmpty()) {
            log.info("등록일 필터링 후 생성할 알람이 없습니다.");
            return;
        }

        // 기존 기록 일괄 조회 (N+1 문제 해결)
        List<Long> medicationIds =
                validAlarms.stream()
                        .map(alarm -> alarm.getMedication().getId())
                        .distinct()
                        .toList();

        Set<String> existingKeys =
                medicationRecordRepository.findExistingRecordKeys(
                        todayDate, todayDate, medicationIds);

        // 메모리에서 필터링 및 생성
        List<MedicationRecord> recordsToSave =
                validAlarms.stream()
                        .filter(
                                alarm -> {
                                    String timeString =
                                            alarm.getDoseTime()
                                                    .format(
                                                            DateTimeFormatter.ofPattern(
                                                                    "HH:mm:ss"));

                                    String key = alarm.getMedication().getId() + "_" + timeString;

                                    boolean exists = existingKeys.contains(key);

                                    if (exists) {
                                        log.debug(
                                                "Skipping: MedicationId={}의 {} 시각에 이미 기록이 존재합니다.",
                                                alarm.getMedication().getId(),
                                                alarm.getDoseTime());
                                    }
                                    return !exists;
                                })
                        .map(
                                alarm ->
                                        MedicationRecord.builder()
                                                .medication(alarm.getMedication())
                                                .status(MedicationRecordStatus.PENDING)
                                                .doseTime(alarm.getDoseTime())
                                                .recordDate(todayDate)
                                                .build())
                        .toList();

        // 4. DB에 일괄 저장
        if (!recordsToSave.isEmpty()) {
            medicationRecordRepository.saveAll(recordsToSave);
            log.info(
                    "스케줄링 성공: {}개의 복약 예정 기록 (PENDING)이 {} 날짜로 생성되었습니다.",
                    recordsToSave.size(),
                    todayDate);
            
            // 복약 기록 생성 시 알림도 함께 생성
            createNotificationsForRecords(recordsToSave, todayDate);
        } else {
            log.info("추가로 생성할 복약 예정 기록이 없습니다. 스케줄러 종료.");
        }
    }

    /** 복약 기록에 대한 알림 생성 */
    private void createNotificationsForRecords(List<MedicationRecord> records, LocalDate recordDate) {
        try {
            for (MedicationRecord record : records) {
                var medication = record.getMedication();
                LocalTime doseTime = record.getDoseTime();
                
                // 알림 예약 시간: 복용 시간에 맞춰 설정
                LocalDateTime notificationTime = recordDate.atTime(doseTime);
                
                // 사전 알림이 활성화되어 있다면 10분 전에 알림 생성
                if (Boolean.TRUE.equals(medication.getPreNotify())) {
                    LocalDateTime preNotificationTime = notificationTime.minusMinutes(10);
                    if (preNotificationTime.isAfter(LocalDateTime.now())) {
                        createMedicationNotification(
                            medication.getUser().getId(),
                            medication.getName(),
                            doseTime,
                            preNotificationTime,
                            "메드스냅",
                            String.format("%s 복용 시간이 10분 남았습니다.", medication.getName())
                        );
                    }
                }
                
                // 정시 알림 생성
                if (notificationTime.isAfter(LocalDateTime.now(clock))) {
                    createMedicationNotification(
                        medication.getUser().getId(),
                        medication.getName(),
                        doseTime,
                        notificationTime,
                        "메드스냅",
                        String.format("%s 복용 시간입니다.", medication.getName())
                    );
                }
            }
            log.info("스케줄러: 복약 기록 {}개에 대한 알림 생성 완료", records.size());
        } catch (Exception e) {
            log.error("스케줄러: 복약 기록 알림 생성 중 오류 발생", e);
        }
    }
    
    /** 약물 복용 알림 생성 헬퍼 메서드 */
    private void createMedicationNotification(Long userId, String medicationName, LocalTime doseTime, 
                                            LocalDateTime scheduledAt, String title, String body) {
        try {
            // 과거 시간 필터링 (KST 기준으로 비교)
            LocalDateTime now = LocalDateTime.now(clock);
            if (!scheduledAt.isAfter(now)) {
                log.debug("스케줄러: 과거 알림 건너뜀: 사용자 ID {}, 시간 {}", userId, scheduledAt);
                return;
            }
            
            // 중복 알림 체크 (강화된 키: userId + scheduledAt + title + body)
            if (notificationRepository.existsByUserIdAndScheduledAtAndTitleAndBody(userId, scheduledAt, title, body)) {
                log.debug("스케줄러: 중복 알림 건너뜀: 사용자 ID {}, 시간 {}, 제목 {}, 본문 {}", userId, scheduledAt, title, body);
                return;
            }
            
            Map<String, Object> data = Map.of(
                "type", "medication",
                "medicationName", medicationName,
                "doseTime", doseTime.toString(),
                "scheduledAt", scheduledAt.toString()
            );
            
            NotificationCreateRequest request = NotificationCreateRequest.builder()
                .title(title)
                .body(body)
                .data(data)
                .scheduledAt(scheduledAt)
                .build();
                
            notificationService.createNotification(userId, request);
            log.debug("스케줄러: 알림 생성 완료: 사용자 ID {}, 약물 {}, 시간 {}", userId, medicationName, doseTime);
        } catch (Exception e) {
            log.error("스케줄러: 알림 생성 실패: 사용자 ID {}, 약물 {}, 시간 {}", userId, medicationName, doseTime, e);
        }
    }

    /** Java DayOfWeek를 도메인 DayOfWeek로 변환 */
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
