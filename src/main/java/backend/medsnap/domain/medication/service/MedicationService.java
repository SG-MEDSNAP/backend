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
import backend.medsnap.domain.medication.dto.request.MedicationUpdateRequest;
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
        return toResponse(savedMedication);
    }

    @Transactional
    public MedicationResponse updateMedication(
            Long medicationId, MedicationUpdateRequest request, MultipartFile image) {
        log.info("약 수정 시작 - ID: {}", medicationId);

        // 약 존재 여부 확인
        Medication medication =
                medicationRepository
                        .findById(medicationId)
                        .orElseThrow(() -> new MedicationNotFoundException(medicationId));

        // 약 이름 중복 검증
        validateDuplicateNameForUpdate(medicationId, request.getName());

        String newImageUrl = null;
        String oldImageUrl = medication.getImageUrl();
        Medication updatedMedication;

        try {
            if (image != null && !image.isEmpty()) {
                // 새 이미지 업로드
                newImageUrl = s3Service.uploadFile(image, "medications");
                log.info("새 이미지 업로드 완료 - URL: {}", newImageUrl);
            }

            // 약 엔티티 정보 업데이트
            medication.updateMedicationDetails(
                    request.getName().trim(),
                    (newImageUrl != null) ? newImageUrl : oldImageUrl,
                    request.getNotifyCaregiver(),
                    request.getPreNotify());

            // 알람 정보 수정
            alarmService.deleteAllAlarmsByMedicationId(medicationId);
            medication.getAlarms().clear();
            alarmService.createAlarms(medication, request.getDoseTimes(), request.getDoseDays());

            // DB 저장
            updatedMedication = medicationRepository.save(medication);

        } catch (DataIntegrityViolationException e) {
            cleanupNewImage(newImageUrl);
            throw InvalidMedicationDataException.duplicateName(request.getName().trim());
        } catch (RuntimeException e) {
            cleanupNewImage(newImageUrl);
            throw e;
        }

        // DB 저장 성공 시 기존 이미지 정리
        if (newImageUrl != null && oldImageUrl != null && !oldImageUrl.equals(newImageUrl)) {
            try {
                s3Service.deleteFile(oldImageUrl);
                log.info("이전 이미지 삭제 완료 - {}", oldImageUrl);
            } catch (Exception ex) {
                log.warn("이전 이미지 삭제 실패 - {}, 오류: {}", oldImageUrl, ex.getMessage());
            }
        }

        return toResponse(updatedMedication);
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

        // 약 삭제
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
        int remainingAlarmCount = alarmService.getRemainingAlarmCount(medicationId);

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

    /** 약 이름 중복 검증 (수정용) */
    private void validateDuplicateNameForUpdate(Long medicationId, String name) {
        String trimmedName = name.trim();
        if (medicationRepository.existsByNameAndIdNot(trimmedName, medicationId)) {
            throw InvalidMedicationDataException.duplicateName(trimmedName);
        }
    }

    /** 새로운 이미지 업로드 후 롤백이 필요한 경우 호출 */
    private void cleanupNewImage(String newImageUrl) {
        if (newImageUrl == null) {
            return;
        }
        try {
            s3Service.deleteFile(newImageUrl);
            log.info("롤백: 새 이미지 삭제 완료 - {}", newImageUrl);
        } catch (Exception ex) {
            log.warn("롤백: 새 이미지 삭제 실패 - {}, 오류: {}", newImageUrl, ex.getMessage());
        }
    }

    /** 알람 삭제 요청 검증 */
    private void validateAlarmDeleteRequest(List<Long> alarmIds) {
        if (alarmIds == null || alarmIds.isEmpty()) {
            throw new BusinessException(
                    ErrorCode.COMMON_VALIDATION_ERROR, "삭제할 알람 ID 목록은 비어있을 수 없습니다.");
        }

        // 중복 ID 검증
        if (alarmIds.size() != alarmIds.stream().distinct().count()) {
            throw new BusinessException(ErrorCode.COMMON_VALIDATION_ERROR, "중복된 알람 ID가 포함되어 있습니다.");
        }

        // null 값 검증
        if (alarmIds.stream().anyMatch(java.util.Objects::isNull)) {
            throw new BusinessException(
                    ErrorCode.COMMON_VALIDATION_ERROR, "알람 ID에 null 값이 포함되어 있습니다.");
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

    /** 엔티티 -> Response DTO 변환 */
    private MedicationResponse toResponse(Medication medication) {
        return MedicationResponse.builder()
                .id(medication.getId())
                .name(medication.getName())
                .imageUrl(medication.getImageUrl())
                .notifyCaregiver(medication.getNotifyCaregiver())
                .preNotify(medication.getPreNotify())
                .doseTimes(
                        medication.getAlarms().stream()
                                .map(Alarm::getDoseTime)
                                .distinct()
                                .sorted()
                                .toList())
                .doseDays(
                        medication.getAlarms().stream()
                                .map(Alarm::getDayOfWeek)
                                .distinct()
                                .sorted()
                                .toList())
                .createdAt(medication.getCreatedAt())
                .updatedAt(medication.getUpdatedAt())
                .build();
    }
}
