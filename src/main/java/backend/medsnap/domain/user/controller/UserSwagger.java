package backend.medsnap.domain.user.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

import backend.medsnap.domain.auth.dto.token.CustomUserDetails;
import backend.medsnap.domain.user.dto.request.MyPageUpdateRequest;
import backend.medsnap.domain.user.dto.response.MyPageResponse;
import backend.medsnap.domain.user.dto.response.UserInfoResponse;
import backend.medsnap.global.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "users", description = "사용자 API")
public interface UserSwagger {

    @Operation(
            summary = "사용자 정보 조회",
            description = "현재 로그인한 사용자의 기본 정보를 조회합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "200",
                        description = "사용자 정보 조회 성공",
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
                                              "httpStatus": 200,
                                              "message": "요청이 성공적으로 처리되었습니다.",
                                              "data": {
                                                "id": 1,
                                                "role": "PATIENT",
                                                "name": "홍길동",
                                                "provider": "KAKAO"
                                              },
                                              "error": null
                                            }
                                            """))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "401",
                        description = "인증이 필요합니다",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ApiResponse.class),
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
                                            {
                                              "code": "A008",
                                              "httpStatus": 401,
                                              "message": "인증이 필요합니다.",
                                              "data": null,
                                              "error": null
                                            }
                                            """))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "404",
                        description = "사용자를 찾을 수 없습니다",
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
                                              "data": null,
                                              "error": null
                                            }
                                            """)))
            })
    ResponseEntity<ApiResponse<UserInfoResponse>> getUserInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails);

    @Operation(
            summary = "마이페이지 수정",
            description = "사용자의 개인정보를 수정합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "200",
                        description = "마이페이지 수정 성공",
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
                                              "httpStatus": 200,
                                              "message": "요청이 성공적으로 처리되었습니다.",
                                              "data": {
                                                "id": 1,
                                                "name": "홍길동",
                                                "birthday": "1990-01-01",
                                                "phone": "010-1234-5678",
                                                "caregiverPhone": "010-9876-5432",
                                                "isPushConsent": true
                                              },
                                              "error": null
                                            }
                                            """))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "401",
                        description = "인증이 필요합니다",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ApiResponse.class),
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
                                            {
                                              "code": "A008",
                                              "httpStatus": 401,
                                              "message": "인증이 필요합니다.",
                                              "data": null,
                                              "error": null
                                            }
                                            """))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "404",
                        description = "사용자를 찾을 수 없습니다",
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
                                              "data": null,
                                              "error": null
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
                                              "data": null,
                                              "error": null
                                            }
                                            """)))
            })
    ResponseEntity<ApiResponse<MyPageResponse>> updateMyPage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody MyPageUpdateRequest request);
}
