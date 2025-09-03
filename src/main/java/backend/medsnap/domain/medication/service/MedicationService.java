package backend.medsnap.domain.medication.service;

import backend.medsnap.domain.medication.dto.request.MedicationCreateRequest;
import backend.medsnap.domain.medication.dto.response.MedicationResponse;
import backend.medsnap.domain.medication.entity.DayOfWeek;
import backend.medsnap.domain.medication.entity.Medication;
import backend.medsnap.domain.medication.entity.MedicationAlarm;
import backend.medsnap.domain.medication.repository.MedicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicationService {

    private final MedicationRepository medicationRepository;

    @Transactional
    public MedicationResponse createMedication(MedicationCreateRequest request) {

        // 1. Medication 생성
        Medication medication = Medication.builder()
                .name(request.getName())
                .imageUrl(request.getImageUrl())
                .notifyCaregiver(request.getNotifyCaregiver())
                .preNotify(request.getPreNotify())
                .build();

        // 2. Alarm 생성
        createAlarms(medication, request);

        // 3. 저장
        Medication savedMedication = medicationRepository.save(medication);

        // 4. response 반환
        return MedicationResponse.builder()
                .id(savedMedication.getId())
                .name(savedMedication.getName())
                .imageUrl(savedMedication.getImageUrl())
                .notifyCaregiver(savedMedication.getNotifyCaregiver())
                .preNotify(savedMedication.getPreNotify())
                .doseTimes(request.getDoseTimes())
                .doseDays(request.getDoseDays())
                .createdAt(savedMedication.getCreatedAt())
                .updatedAt(savedMedication.getUpdatedAt())
                .build();
    }

    private void createAlarms(Medication medication, MedicationCreateRequest request) {
        List<MedicationAlarm> alarms = new ArrayList<>();

        if (request.getDoseDays().contains(DayOfWeek.DAILY)) {
            //매일 복용 -> 월~일 전체 요일 생성
            DayOfWeek[] allDays = {
                    DayOfWeek.MON, DayOfWeek.TUE, DayOfWeek.WED,
                    DayOfWeek.THU, DayOfWeek.FRI, DayOfWeek.SAT, DayOfWeek.SUN
            };

            for (DayOfWeek day : allDays) {
                for (LocalTime time : request.getDoseTimes()) {
                    alarms.add(createAlarm(medication, time, day));
                }
            }
        } else {
            // 특정 요일에만 복용
            for (DayOfWeek day : request.getDoseDays()) {
                for (LocalTime time : request.getDoseTimes()) {
                    alarms.add(createAlarm(medication, time, day));
                }
            }
        }

        medication.getAlarms().addAll(alarms);
    }

    private MedicationAlarm createAlarm(Medication medication, LocalTime time, DayOfWeek day) {
        return MedicationAlarm.builder()
                .doseTime(time)
                .dayOfWeek(day)
                .medication(medication)
                .build();
    }
}
