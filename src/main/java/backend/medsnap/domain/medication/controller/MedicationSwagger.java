package backend.medsnap.domain.medication.controller;

import backend.medsnap.domain.medication.dto.request.MedicationCreateRequest;
import backend.medsnap.domain.medication.dto.response.MedicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Medication", description = "약 API")
public interface MedicationSwagger {

    @Operation(summary = "약 등록", description = "새로운 약을 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "약 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    ResponseEntity<MedicationResponse> createMedication(
            @Parameter(description = "약 등록 요청 정보", required = true)
            @RequestBody MedicationCreateRequest request
    );
}
