package backend.medsnap.domain.medication.controller;

import backend.medsnap.domain.medication.dto.request.MedicationCreateRequest;
import backend.medsnap.domain.medication.dto.response.MedicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "medications", description = "약 API")
public interface MedicationSwagger {

    @Operation(
            summary = "약 등록",
            description = "새로운 약을 등록합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "약 등록 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = backend.medsnap.global.dto.ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "code": "SUCCESS",
                                      "httpStatus": 201,
                                      "message": "요청이 성공적으로 처리되었습니다.",
                                      "data": {
                                        "id": 1,
                                        "name": "타이레놀",
                                        "imageUrl": "https://example.com/image.jpg",
                                        "notifyCaregiver": true,
                                        "preNotify": true,
                                        "doseTimes": ["09:00", "21:00"],
                                        "doseDays": ["MON", "TUE", "WED"],
                                        "createdAt": "2024-01-01T10:00:00",
                                        "updatedAt": "2024-01-01T10:00:00"
                                      }
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "입력값 검증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = backend.medsnap.global.dto.ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "code": "C005",
                                      "httpStatus": 400,
                                      "message": "입력값 검증에 실패했습니다.",
                                      "data": null
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "중복된 약 이름",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = backend.medsnap.global.dto.ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "code": "M006",
                                      "httpStatus": 409,
                                      "message": "이미 등록된 약 이름입니다.",
                                      "data": null
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = backend.medsnap.global.dto.ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "code": "C003",
                                      "httpStatus": 500,
                                      "message": "내부 서버 오류가 발생했습니다.",
                                      "data": null
                                    }
                                    """
                            )
                    )
            )
    })
    ResponseEntity<backend.medsnap.global.dto.ApiResponse<MedicationResponse>> createMedication(
            @Parameter(description = "약 등록 요청 정보", required = true)
            @RequestBody @Valid MedicationCreateRequest request
    );
}
