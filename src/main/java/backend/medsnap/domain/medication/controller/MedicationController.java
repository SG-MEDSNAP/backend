package backend.medsnap.domain.medication.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @PostMapping
    public ResponseEntity<ApiResponse<MedicationResponse>> createMedication(
            @RequestBody @Valid MedicationCreateRequest request) {

        MedicationResponse response = medicationService.createMedication(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, response));
    }
}
