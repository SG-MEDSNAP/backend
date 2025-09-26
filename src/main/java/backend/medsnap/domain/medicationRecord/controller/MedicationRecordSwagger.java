package backend.medsnap.domain.medicationRecord.controller;

import backend.medsnap.domain.auth.dto.token.CustomUserDetails;
import backend.medsnap.domain.medicationRecord.dto.response.DayListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.time.LocalDate;

@Tag(name = "medication-records", description = "복약 현황 API")
public interface MedicationRecordSwagger {

    @Operation(summary = "특정 날짜의 복약 목록 조회", description = "지정된 날짜의 복약 목록과 상태를 조회합니다.")
    @ApiResponses(
            value = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "200",
                        description = "복약 목록 조회 성공",
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
                            "date": "2024-01-15",
                            "items": [
                              {
                                "alarmTime": "09:00",
                                "medicationId": 1,
                                "medicationName": "타이레놀",
                                "status": "TAKEN"
                              },
                              {
                                "alarmTime": "21:00",
                                "medicationId": 1,
                                "medicationName": "타이레놀",
                                "status": "PENDING"
                              },
                              {
                                "alarmTime": "08:00",
                                "medicationId": 2,
                                "medicationName": "비타민",
                                "status": "SKIPPED"
                              }
                            ]
                          }
                        }
                        """))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "400",
                        description = "잘못된 요청 (날짜 형식 오류 등)",
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
    ResponseEntity<backend.medsnap.global.dto.ApiResponse<DayListResponse>> getDayList(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user,
            @Parameter(description = "조회할 날짜 (YYYY-MM-DD 형식)", required = true, example = "2024-01-15")
                    @org.springframework.web.bind.annotation.RequestParam
                    LocalDate date);
}
