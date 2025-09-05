package backend.medsnap.domain.medication.controller;

import backend.medsnap.domain.medication.dto.request.MedicationCreateRequest;
import backend.medsnap.domain.medication.dto.response.MedicationResponse;
import backend.medsnap.domain.medication.service.MedicationService;
import backend.medsnap.global.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/medications")
@RequiredArgsConstructor
public class MedicationController implements MedicationSwagger {

    private final MedicationService medicationService;

    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<MedicationResponse>> createMedication(
            @RequestBody @Valid MedicationCreateRequest request) {
        try {
            MedicationResponse response = medicationService.createMedication(request);
            return ResponseEntity.status(201).body(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(ApiResponse.error(e.getMessage()));
        }
    }
}
