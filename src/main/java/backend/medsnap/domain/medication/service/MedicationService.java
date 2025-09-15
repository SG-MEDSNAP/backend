package backend.medsnap.domain.medication.service;


import backend.medsnap.domain.alarm.entity.Alarm;
import backend.medsnap.domain.alarm.service.AlarmService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import backend.medsnap.domain.medication.dto.request.MedicationCreateRequest;
import backend.medsnap.domain.medication.dto.response.MedicationResponse;
import backend.medsnap.domain.medication.entity.Medication;
import backend.medsnap.domain.medication.exception.InvalidMedicationDataException;
import backend.medsnap.domain.medication.repository.MedicationRepository;
import backend.medsnap.infra.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MedicationService {

    private final MedicationRepository medicationRepository;
    private final S3Service s3Service;
    private final AlarmService alarmService;

    @Transactional
    public MedicationResponse createMedication(
            MedicationCreateRequest request, MultipartFile image) {

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
        alarmService.createAlarms(medication, request.getDoseTimes(), request.getDoseDays());

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
                                .map(Alarm::getDoseTime)
                                .distinct()
                                .sorted()
                                .toList())
                .doseDays(
                        savedMedication.getAlarms().stream()
                                .map(Alarm::getDayOfWeek)
                                .distinct()
                                .sorted()
                                .toList())
                .createdAt(savedMedication.getCreatedAt())
                .updatedAt(savedMedication.getUpdatedAt())
                .build();
    }
}
