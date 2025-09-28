package backend.medsnap.domain.pushToken.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

import backend.medsnap.domain.auth.dto.token.CustomUserDetails;
import backend.medsnap.domain.pushToken.dto.request.UpsertPushTokenRequest;
import backend.medsnap.domain.pushToken.dto.response.PushTokenResponse;
import backend.medsnap.global.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "push-tokens", description = "푸시토큰 API")
public interface PushTokenSwagger {

    @Operation(summary = "푸시토큰 등록/수정", description = "사용자의 푸시토큰을 등록하거나 수정합니다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "푸시토큰 등록/수정 요청",
            content =
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UpsertPushTokenRequest.class),
                            examples =
                                    @ExampleObject(
                                            value =
                                                    """
                            {
                              "token": "ExponentPushToken[xxxxxxxxxxxxxxxxxxxxxx]",
                              "platform": "IOS"
                            }
                            """)))
    @ApiResponses(
            value = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "201",
                        description = "푸시토큰 등록/수정 성공",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ApiResponse.class),
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
                                        {
                                          "code": "SUCCESS",
                                          "httpStatus": 201,
                                          "message": "요청이 성공적으로 처리되었습니다.",
                                          "data": {
                                            "token": "ExponentPushToken[xxxxxxxxxxxxxxxxxxxxxx]",
                                            "platform": "IOS"
                                          }
                                        }
                                        """))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "400",
                        description = "입력값 검증 실패",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ApiResponse.class),
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
                                        schema = @Schema(implementation = ApiResponse.class),
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
                                        schema = @Schema(implementation = ApiResponse.class),
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
                        responseCode = "400",
                        description = "잘못된 플랫폼 정보",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ApiResponse.class),
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
                                        {
                                          "code": "PT001",
                                          "httpStatus": 400,
                                          "message": "유효하지 않은 플랫폼 정보입니다.",
                                          "data": null
                                        }
                                        """))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "500",
                        description = "서버 오류",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ApiResponse.class),
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
    ResponseEntity<ApiResponse<PushTokenResponse>> upsertPushToken(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "푸시토큰 등록/수정 요청", required = true) @RequestBody
                    UpsertPushTokenRequest request);
}
