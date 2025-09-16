package backend.medsnap.domain.medication.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import backend.medsnap.domain.alarm.entity.Alarm;
import backend.medsnap.domain.alarm.repository.AlarmRepository;
import backend.medsnap.domain.alarm.service.AlarmService;
import backend.medsnap.domain.medication.dto.request.MedicationCreateRequest;
import backend.medsnap.domain.medication.dto.response.MedicationResponse;
import backend.medsnap.domain.medication.entity.Medication;
import backend.medsnap.domain.medication.exception.InvalidMedicationDataException;
import backend.medsnap.domain.medication.exception.MedicationNotFoundException;
import backend.medsnap.domain.medication.repository.MedicationRepository;
import backend.medsnap.global.exception.BusinessException;
import backend.medsnap.global.exception.ErrorCode;
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
    private final AlarmRepository alarmRepository;

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

    /** 약 삭제 */
    @Transactional
    public void deleteMedication(Long medicationId) {
        log.info("약 삭제 시작 - ID: {}", medicationId);

        // 약 존재 여부 확인
        Medication medication =
                medicationRepository
                        .findById(medicationId)
                        .orElseThrow(() -> new MedicationNotFoundException(medicationId));

        // 알람 개수 확인
        int alarmCount = medication.getAlarms().size();
        log.info("약 ID: {}에 연결된 알람 개수: {}", medicationId, alarmCount);

        // 약 삭제 (cascade로 알람도 함께 삭제됨)
        medicationRepository.delete(medication);
        log.info("약 ID: {} 및 관련 알람 {}개가 삭제되었습니다.", medicationId, alarmCount);

        // S3 이미지 삭제
        deleteMedicationImage(medication);
    }

    /** 선택된 알람들 삭제 */
    @Transactional
    public void deleteSelectedAlarms(Long medicationId, List<Long> alarmIds) {
        log.info("선택된 알람 삭제 시작 - 약 ID: {}, 알람 IDs: {}", medicationId, alarmIds);

        // 알람 삭제 요청 검증
        validateAlarmDeleteRequest(alarmIds);

        // 약 존재 여부 확인
        Medication medication =
                medicationRepository
                        .findById(medicationId)
                        .orElseThrow(() -> new MedicationNotFoundException(medicationId));

        // 알람 삭제 수행
        alarmService.deleteAlarm(medication, alarmIds);

        // 남은 알람 개수 확인
        int remainingAlarmCount = alarmRepository.countByMedicationId(medicationId);

        if (remainingAlarmCount == 0) {
            log.info("약 ID: {}의 모든 알람이 삭제되어 약도 함께 삭제합니다.", medicationId);

            // 약 삭제
            medicationRepository.delete(medication);
            log.info("약 ID: {} 삭제 완료", medication.getId());

            deleteMedicationImage(medication);
        }
    }

    /** 약 이름 중복 검증 */
    private void validateDuplicateName(String name) {
        String trimmedName = name.trim();
        if (medicationRepository.existsByName(trimmedName)) {
            throw InvalidMedicationDataException.duplicateName(trimmedName);
        }
    }

    /** 알람 삭제 요청 검증 */
    private void validateAlarmDeleteRequest(List<Long> alarmIds) {
        if (alarmIds == null || alarmIds.isEmpty()) {
            throw new BusinessException(ErrorCode.COMMON_VALIDATION_ERROR, "삭제할 알람 ID 목록은 비어있을 수 없습니다.");
        }

        // 중복 ID 검증
        if (alarmIds.size() != alarmIds.stream().distinct().count()) {
            throw new BusinessException(ErrorCode.COMMON_VALIDATION_ERROR, "중복된 알람 ID가 포함되어 있습니다.");
        }

        // null 값 검증
        if (alarmIds.stream().anyMatch(java.util.Objects::isNull)) {
            throw new BusinessException(ErrorCode.COMMON_VALIDATION_ERROR, "알람 ID에 null 값이 포함되어 있습니다.");
        }
    }

    /** 약 이미지 S3에서 삭제 */
    private void deleteMedicationImage(Medication medication) {
        if (medication.getImageUrl() == null || medication.getImageUrl().isEmpty()) {
            log.debug("약 ID: {}에 삭제할 이미지가 없습니다.", medication.getId());
            return;
        }

        try {
            s3Service.deleteFile(medication.getImageUrl());
            log.info(
                    "S3 이미지 삭제 완료 - 약 ID: {}, URL: {}",
                    medication.getId(),
                    medication.getImageUrl());
        } catch (Exception e) {
            log.warn(
                    "S3 이미지 삭제 실패 - 약 ID: {}, URL: {}, 오류: {}",
                    medication.getId(),
                    medication.getImageUrl(),
                    e.getMessage());
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
