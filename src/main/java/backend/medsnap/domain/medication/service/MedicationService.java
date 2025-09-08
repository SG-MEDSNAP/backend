package backend.medsnap.domain.medication.service;

import backend.medsnap.domain.medication.dto.request.MedicationCreateRequest;
import backend.medsnap.domain.medication.dto.response.MedicationResponse;
import backend.medsnap.domain.medication.entity.DayOfWeek;
import backend.medsnap.domain.medication.entity.Medication;
import backend.medsnap.domain.medication.entity.MedicationAlarm;
import backend.medsnap.domain.medication.exception.InvalidMedicationDataException;
import backend.medsnap.domain.medication.repository.MedicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MedicationService {

    private final MedicationRepository medicationRepository;

    @Transactional
    public MedicationResponse createMedication(MedicationCreateRequest request) {

        // 1. 약 이름 중복 검증
        validateDuplicateName(request.getName());
        log.info("약 등록 시작 - 이름: {}", request.getName());

        // 2. Medication 생성
        Medication medication = Medication.builder()
                .name(request.getName().trim())
                .imageUrl(request.getImageUrl().trim())
                .notifyCaregiver(request.getNotifyCaregiver())
                .preNotify(request.getPreNotify())
                .build();

        // 3. Alarm 생성
        createAlarms(medication, request);

        // 4. 저장
        Medication savedMedication;
        try {
            savedMedication = medicationRepository.save(medication);
        } catch (DataIntegrityViolationException e) {
            // DB 레벨에서 중복 제약 위반 시 처리
            throw InvalidMedicationDataException.duplicateName(request.getName().trim());
        }

        // 5. response 반환
        return getMedicationResponse(savedMedication, request);
    }

    /**
     * 약 이름 중복 검증
     */
    private void validateDuplicateName(String name) {
        String trimmedName = name.trim();
        if (medicationRepository.existsByName(trimmedName)) {
            throw InvalidMedicationDataException.duplicateName(trimmedName);
        }
    }

    /**
     * 알람 생성 및 연관관계 설정
     */
    private void createAlarms(Medication medication, MedicationCreateRequest request) {
        List<DayOfWeek> expandedDays = DayOfWeek.expandDays(request.getDoseDays());

        List<MedicationAlarm> alarms = expandedDays.stream()
                        .flatMap(day -> request.getDoseTimes().stream()
                                .map(time -> createAlarm(medication, time, day)))
                        .toList();

        medication.getAlarms().addAll(alarms);
    }

    /**
     * 단일 알람 생성
     */
    private MedicationAlarm createAlarm(Medication medication, LocalTime time, DayOfWeek day) {
        return MedicationAlarm.builder()
                .doseTime(time)
                .dayOfWeek(day)
                .medication(medication)
                .build();
    }

    /**
     * Medication 엔티티 생성
     */
    private Medication createMedicationEntity(MedicationCreateRequest request) {
        return Medication.builder()
                .name(request.getName().trim())
                .imageUrl(request.getImageUrl().trim())
                .notifyCaregiver(request.getNotifyCaregiver())
                .preNotify(request.getPreNotify())
                .build();
    }

    /**
     * Response 객체 생성
     */
    private MedicationResponse getMedicationResponse(Medication savedMedication, MedicationCreateRequest request) {
        return MedicationResponse.builder()
                .id(savedMedication.getId())
                .name(savedMedication.getName())
                .imageUrl(savedMedication.getImageUrl())
                .notifyCaregiver(savedMedication.getNotifyCaregiver())
                .preNotify(savedMedication.getPreNotify())
                .doseTimes(savedMedication.getAlarms().stream()
                        .map(MedicationAlarm::getDoseTime)
                        .distinct()
                        .sorted()
                        .toList())
                .doseDays(savedMedication.getAlarms().stream()
                        .map(MedicationAlarm::getDayOfWeek)
                        .distinct()
                        .sorted()
                        .toList())
                .createdAt(savedMedication.getCreatedAt())
                .updatedAt(savedMedication.getUpdatedAt())
                .build();
    }
}
