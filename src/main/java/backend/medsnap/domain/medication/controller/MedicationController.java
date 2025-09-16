package backend.medsnap.domain.medication.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import backend.medsnap.domain.alarm.dto.request.AlarmDeleteRequest;
import backend.medsnap.domain.medication.dto.request.MedicationCreateRequest;
import backend.medsnap.domain.medication.dto.response.MedicationResponse;
import backend.medsnap.domain.medication.service.MedicationService;
import backend.medsnap.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/medications")
@RequiredArgsConstructor
public class MedicationController implements MedicationSwagger {

    private final MedicationService medicationService;

    @Override
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<MedicationResponse>> createMedication(
            @RequestPart("request") @Valid MedicationCreateRequest request,
            @RequestPart("image") MultipartFile image) {

        MedicationResponse response = medicationService.createMedication(request, image);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, response));
    }

    @Override
    @DeleteMapping("/{medicationId}")
    public ResponseEntity<Void> deleteMedication(
            @PathVariable Long medicationId) {
        medicationService.deleteMedication(medicationId);

        return ResponseEntity.noContent().build();
    }

    @Override
    @DeleteMapping("/{medicationId}/alarms")
    public ResponseEntity<Void> deleteAlarms(
            @PathVariable Long medicationId, @RequestBody @Valid AlarmDeleteRequest request) {

        medicationService.deleteSelectedAlarms(medicationId, request.getAlarmIds());

        return ResponseEntity.noContent().build();
    }
}
