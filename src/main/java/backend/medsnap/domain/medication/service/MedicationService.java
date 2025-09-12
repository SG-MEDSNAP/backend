package backend.medsnap.domain.medication.service;

import java.time.LocalTime;
import java.util.List;

import backend.medsnap.infra.s3.S3Service;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.medsnap.domain.medication.dto.request.MedicationCreateRequest;
import backend.medsnap.domain.medication.dto.response.MedicationResponse;
import backend.medsnap.domain.medication.entity.DayOfWeek;
import backend.medsnap.domain.medication.entity.Medication;
import backend.medsnap.domain.medication.entity.MedicationAlarm;
import backend.medsnap.domain.medication.exception.InvalidMedicationDataException;
import backend.medsnap.domain.medication.repository.MedicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class MedicationService {

    private final MedicationRepository medicationRepository;
    private final S3Service s3Service;

    @Transactional
    public MedicationResponse createMedication(MedicationCreateRequest request, MultipartFile image) {

        // 약 이름 중복 검증
        validateDuplicateName(request.getName());
        log.info("약 등록 시작 - 이름: {}", request.getName());

        // 이미지 업로드
        String imageUrl = s3Service.uploadFile(image, "medications");
        log.info("이미지 업로드 완료 - URL: {}", imageUrl);

        // Medication 생성
        Medication medication =
                Medication.builder()
                        .name(request.getName().trim())
                        .imageUrl(imageUrl)
                        .notifyCaregiver(request.getNotifyCaregiver())
                        .preNotify(request.getPreNotify())
                        .build();

        // Alarm 생성
        createAlarms(medication, request);

        // 저장
        Medication savedMedication;
        try {
            savedMedication = medicationRepository.save(medication);
        } catch (DataIntegrityViolationException e) {
            // DB 레벨에서 중복 제약 위반 시 처리
            throw InvalidMedicationDataException.duplicateName(request.getName().trim());
        }

        // response 반환
        return getMedicationResponse(savedMedication, request);
    }

    /** 약 이름 중복 검증 */
    private void validateDuplicateName(String name) {
        String trimmedName = name.trim();
        if (medicationRepository.existsByName(trimmedName)) {
            throw InvalidMedicationDataException.duplicateName(trimmedName);
        }
    }

    /** 알람 생성 및 연관관계 설정 */
    private void createAlarms(Medication medication, MedicationCreateRequest request) {
        List<DayOfWeek> expandedDays = DayOfWeek.expandDays(request.getDoseDays());

        List<MedicationAlarm> alarms =
                expandedDays.stream()
                        .flatMap(
                                day ->
                                        request.getDoseTimes().stream()
                                                .map(timeStr -> {
                                                    LocalTime time = LocalTime.parse(timeStr);
                                                    return createAlarm(medication, time, day);
                                                }))
                        .toList();

        medication.getAlarms().addAll(alarms);
    }

    /** 단일 알람 생성 */
    private MedicationAlarm createAlarm(Medication medication, LocalTime time, DayOfWeek day) {
        return MedicationAlarm.builder()
                .doseTime(time)
                .dayOfWeek(day)
                .medication(medication)
                .build();
    }

    /** Response 객체 생성 */
    private MedicationResponse getMedicationResponse(
            Medication savedMedication, MedicationCreateRequest request) {
        return MedicationResponse.builder()
                .id(savedMedication.getId())
                .name(savedMedication.getName())
                .imageUrl(savedMedication.getImageUrl())
                .notifyCaregiver(savedMedication.getNotifyCaregiver())
                .preNotify(savedMedication.getPreNotify())
                .doseTimes(
                        savedMedication.getAlarms().stream()
                                .map(MedicationAlarm::getDoseTime)
                                .distinct()
                                .sorted()
                                .toList())
                .doseDays(
                        savedMedication.getAlarms().stream()
                                .map(MedicationAlarm::getDayOfWeek)
                                .distinct()
                                .sorted()
                                .toList())
                .createdAt(savedMedication.getCreatedAt())
                .updatedAt(savedMedication.getUpdatedAt())
                .build();
    }
}
