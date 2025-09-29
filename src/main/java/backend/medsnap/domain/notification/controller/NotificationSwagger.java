package backend.medsnap.domain.notification.controller;

import backend.medsnap.domain.auth.dto.token.CustomUserDetails;
import backend.medsnap.domain.notification.dto.request.NotificationCreateRequest;
import backend.medsnap.domain.notification.dto.response.NotificationCreateResponse;
import backend.medsnap.global.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "notifications", description = "알림 API")
public interface NotificationSwagger {

    @Operation(summary = "알림 생성", description = "새로운 알림을 생성하고 전송합니다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "알림 생성 요청",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = NotificationCreateRequest.class),
                    examples = @ExampleObject(
                            value = """
                    {
                      "userId": 1,
                      "title": "약 복용 알림",
                      "body": "타이레놀 복용 시간입니다.",
                      "data": {
                        "medicationId": 1,
                        "medicationName": "타이레놀",
                        "doseTime": "09:00"
                      },
                      "scheduledAt": "2024-01-01T09:00:00"
                    }
                    """)))
    @ApiResponses(
            value = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "201",
                        description = "알림 생성 성공",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation = ApiResponse.class),
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
                        {
                          "code": "SUCCESS",
                          "httpStatus": 201,
                          "message": "요청이 성공적으로 처리되었습니다.",
                          "data": {
                            "id": 1
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
                                                        implementation = ApiResponse.class),
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
                        responseCode = "400",
                        description = "유효하지 않은 알림 요청",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation = ApiResponse.class),
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
                        {
                          "code": "N003",
                          "httpStatus": 400,
                          "message": "유효하지 않은 알림 요청입니다.",
                          "data": null
                        }
                        """))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "400",
                        description = "활성화된 푸시 토큰이 없음",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation = ApiResponse.class),
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
                        {
                          "code": "N004",
                          "httpStatus": 400,
                          "message": "활성화된 푸시 토큰이 없어 알림을 보낼 수 없습니다.",
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
                                                        implementation = ApiResponse.class),
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
                                                        implementation = ApiResponse.class),
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
                        description = "알림 전송 실패",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation = ApiResponse.class),
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
                        {
                          "code": "N002",
                          "httpStatus": 500,
                          "message": "알림 전송에 실패했습니다.",
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
                                                        implementation = ApiResponse.class),
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
    ResponseEntity<ApiResponse<NotificationCreateResponse>> create(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "알림 생성 요청 데이터", required = true) @RequestBody @Valid NotificationCreateRequest request);
}
