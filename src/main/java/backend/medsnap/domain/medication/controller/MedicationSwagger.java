package backend.medsnap.domain.medication.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import backend.medsnap.domain.alarm.dto.request.AlarmDeleteRequest;
import backend.medsnap.domain.auth.dto.token.CustomUserDetails;
import backend.medsnap.domain.medication.dto.request.MedicationCreateRequest;
import backend.medsnap.domain.medication.dto.request.MedicationUpdateRequest;
import backend.medsnap.domain.medication.dto.response.MedicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "medications", description = "약 API")
public interface MedicationSwagger {

    @Operation(summary = "약 등록", description = "새로운 약을 등록합니다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "약 등록 요청 (multipart/form-data)",
            content = @Content(mediaType = "multipart/form-data"))
    @ApiResponses(
            value = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "201",
                        description = "약 등록 성공",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                backend.medsnap.global.dto
                                                                        .ApiResponse.class),
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
            {
              "code": "SUCCESS",
              "httpStatus": 201,
              "message": "요청이 성공적으로 처리되었습니다.",
              "data": {
                "id": 1,
                "name": "타이레놀",
                "imageUrl": "https://s3.amazonaws.com/bucket/medications/uuid_timestamp.jpg",
                "notifyCaregiver": true,
                "preNotify": true,
                "doseTimes": ["09:00", "21:00"],
                "doseDays": ["MON", "TUE", "WED"],
                "createdAt": "2024-01-01T10:00:00",
                "updatedAt": "2024-01-01T10:00:00"
              }
            }
            """))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "400",
                        description = "입력값 검증 실패",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                backend.medsnap.global.dto
                                                                        .ApiResponse.class),
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
            {
              "code": "C002",
              "httpStatus": 400,
              "message": "입력값 검증에 실패했습니다.",
              "data": null
            }
            """))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "401",
                        description = "인증 실패",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                backend.medsnap.global.dto
                                                                        .ApiResponse.class),
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
            {
              "code": "A007",
              "httpStatus": 401,
              "message": "유효하지 않은 JWT 토큰입니다.",
              "data": null
            }
            """))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "404",
                        description = "사용자를 찾을 수 없음",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                backend.medsnap.global.dto
                                                                        .ApiResponse.class),
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
            {
              "code": "U001",
              "httpStatus": 404,
              "message": "사용자를 찾을 수 없습니다.",
              "data": null
            }
            """))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "409",
                        description = "중복된 약 이름",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                backend.medsnap.global.dto
                                                                        .ApiResponse.class),
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
            {
              "code": "M002",
              "httpStatus": 409,
              "message": "이미 등록된 약 이름입니다.",
              "data": null
            }
            """))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "500",
                        description = "서버 오류",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                backend.medsnap.global.dto
                                                                        .ApiResponse.class),
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
            {
              "code": "C001",
              "httpStatus": 500,
              "message": "내부 서버 오류가 발생했습니다.",
              "data": null
            }
            """)))
            })
    ResponseEntity<backend.medsnap.global.dto.ApiResponse<MedicationResponse>> createMedication(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "약 등록 JSON 데이터", required = true)
                    @RequestPart("request")
                    @Valid
                    MedicationCreateRequest request,
            @Parameter(description = "약 이미지 파일", required = true) @RequestPart("image")
                    MultipartFile image);

    @Operation(summary = "약 정보 수정", description = "기존 약의 정보를 수정합니다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "약 수정 요청 (multipart/form-data)",
            content = @Content(mediaType = "multipart/form-data"))
    @ApiResponses(
            value = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "200",
                        description = "약 수정 성공",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                backend.medsnap.global.dto
                                                                        .ApiResponse.class),
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
            {
              "code": "SUCCESS",
              "httpStatus": 200,
              "message": "요청이 성공적으로 처리되었습니다.",
              "data": {
                "id": 1,
                "name": "수정된 타이레놀",
                "imageUrl": "https://s3.amazonaws.com/bucket/medications/new_uuid_timestamp.jpg",
                "notifyCaregiver": false,
                "preNotify": true,
                "doseTimes": ["08:00", "20:00"],
                "doseDays": ["MON", "TUE", "WED", "THU", "FRI"],
                "createdAt": "2024-01-01T10:00:00",
                "updatedAt": "2024-01-02T15:30:00"
              }
            }
            """))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "400",
                        description = "입력값 검증 실패",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                backend.medsnap.global.dto
                                                                        .ApiResponse.class),
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
            {
              "code": "C002",
              "httpStatus": 400,
              "message": "입력값 검증에 실패했습니다.",
              "data": null
            }
            """))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "401",
                        description = "인증 실패",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                backend.medsnap.global.dto
                                                                        .ApiResponse.class),
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
            {
              "code": "A007",
              "httpStatus": 401,
              "message": "유효하지 않은 JWT 토큰입니다.",
              "data": null
            }
            """))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "404",
                        description = "약을 찾을 수 없음 (존재하지 않거나 다른 사용자의 약)",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                backend.medsnap.global.dto
                                                                        .ApiResponse.class),
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
            {
              "code": "M001",
              "httpStatus": 404,
              "message": "약 정보를 찾을 수 없습니다.",
              "data": null
            }
            """))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "409",
                        description = "중복된 약 이름 (사용자 내에서)",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                backend.medsnap.global.dto
                                                                        .ApiResponse.class),
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
            {
              "code": "M002",
              "httpStatus": 409,
              "message": "이미 등록된 약 이름입니다.",
              "data": null
            }
            """))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "500",
                        description = "서버 오류",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                backend.medsnap.global.dto
                                                                        .ApiResponse.class),
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
            {
              "code": "C001",
              "httpStatus": 500,
              "message": "내부 서버 오류가 발생했습니다.",
              "data": null
            }
            """)))
            })
    ResponseEntity<backend.medsnap.global.dto.ApiResponse<MedicationResponse>> updateMedication(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "수정할 약의 ID", required = true, example = "1")
                    @PathVariable("medicationId")
                    Long medicationId,
            @Parameter(description = "약 수정 JSON 데이터", required = true)
                    @RequestPart("request")
                    @Valid
                    MedicationUpdateRequest request,
            @Parameter(description = "약 이미지 파일 (선택사항)")
                    @RequestPart(value = "image", required = false)
                    MultipartFile image);

    @Operation(summary = "약 삭제", description = "등록된 약과 관련된 모든 알람을 삭제합니다.")
    @ApiResponses(
            value = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "204",
                        description = "약 삭제 성공"),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "401",
                        description = "인증 실패",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                backend.medsnap.global.dto
                                                                        .ApiResponse.class),
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
{
  "code": "A007",
  "httpStatus": 401,
  "message": "유효하지 않은 JWT 토큰입니다.",
  "data": null
}
"""))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "404",
                        description = "약을 찾을 수 없음 (존재하지 않거나 다른 사용자의 약)",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                backend.medsnap.global.dto
                                                                        .ApiResponse.class),
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
{
  "code": "M001",
  "httpStatus": 404,
  "message": "약 정보를 찾을 수 없습니다.",
  "data": null
}
"""))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "500",
                        description = "서버 오류",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                backend.medsnap.global.dto
                                                                        .ApiResponse.class),
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
{
  "code": "C001",
  "httpStatus": 500,
  "message": "내부 서버 오류가 발생했습니다.",
  "data": null
}
""")))
            })
    ResponseEntity<Void> deleteMedication(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "삭제할 약의 ID", required = true, example = "1")
                    @PathVariable("medicationId")
                    Long medicationId);

    @Operation(summary = "선택된 알람 삭제", description = "특정 약의 선택된 알람들을 삭제합니다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "삭제할 알람 ID 목록",
            content =
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AlarmDeleteRequest.class),
                            examples =
                                    @ExampleObject(
                                            value =
                                                    """
    {
      "alarmIds": [1, 2, 3]
    }
    """)))
    @ApiResponses(
            value = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "204",
                        description = "선택된 알람 삭제 성공"),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "400",
                        description = "잘못된 요청 (빈 알람 ID 목록, 중복 ID 등)",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                backend.medsnap.global.dto
                                                                        .ApiResponse.class),
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
{
  "code": "C002",
  "httpStatus": 400,
  "message": "삭제할 알람 ID 목록은 비어있을 수 없습니다.",
  "data": null
}
"""))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "401",
                        description = "인증 실패",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                backend.medsnap.global.dto
                                                                        .ApiResponse.class),
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
{
  "code": "A007",
  "httpStatus": 401,
  "message": "유효하지 않은 JWT 토큰입니다.",
  "data": null
}
"""))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "404",
                        description = "약을 찾을 수 없음 (존재하지 않거나 다른 사용자의 약)",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                backend.medsnap.global.dto
                                                                        .ApiResponse.class),
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
{
  "code": "M001",
  "httpStatus": 404,
  "message": "약 정보를 찾을 수 없습니다.",
  "data": null
}
"""))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "500",
                        description = "서버 오류",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                backend.medsnap.global.dto
                                                                        .ApiResponse.class),
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
{
  "code": "C001",
  "httpStatus": 500,
  "message": "내부 서버 오류가 발생했습니다.",
  "data": null
}
""")))
            })
    ResponseEntity<Void> deleteAlarms(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "약의 ID", required = true, example = "1")
                    @PathVariable("medicationId")
                    Long medicationId,
            @Parameter(description = "삭제할 알람 정보", required = true) @RequestBody @Valid
                    AlarmDeleteRequest request);
}
