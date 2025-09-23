package backend.medsnap.domain.auth.controller;

import backend.medsnap.domain.auth.dto.request.LoginRequest;
import backend.medsnap.domain.auth.dto.request.SignupRequest;
import backend.medsnap.domain.auth.dto.token.TokenPair;
import backend.medsnap.global.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "auth", description = "Auth API")
public interface AuthSwagger {

    @Operation(summary = "로그인", description = "OIDC ID 토큰을 사용하여 로그인합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "code": "SUCCESS",
                                              "httpStatus": 200,
                                              "message": "요청이 성공적으로 처리되었습니다.",
                                              "data": {
                                                "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                                "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                                              },
                                              "error": null
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "가입되지 않은 소셜 계정 (회원가입 필요)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "code": "A002",
                                              "httpStatus": 404,
                                              "message": "가입되지 않은 소셜 계정입니다.",
                                              "data": null,
                                              "error": null
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
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "code": "C002",
                                              "httpStatus": 400,
                                              "message": "입력값 검증에 실패했습니다.",
                                              "data": null,
                                              "error": null
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<TokenPair>> login(@Valid @RequestBody LoginRequest request);

    @Operation(summary = "회원가입", description = "OIDC ID 토큰과 사용자 정보를 사용하여 회원가입합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "회원가입 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "code": "SUCCESS",
                                              "httpStatus": 200,
                                              "message": "요청이 성공적으로 처리되었습니다.",
                                              "data": {
                                                "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                                "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                                              },
                                              "error": null
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "이미 가입된 소셜 계정",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "code": "A003",
                                              "httpStatus": 409,
                                              "message": "이미 가입된 소셜 계정입니다.",
                                              "data": null,
                                              "error": null
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
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "code": "C002",
                                              "httpStatus": 400,
                                              "message": "입력값 검증에 실패했습니다.",
                                              "data": null,
                                              "error": null
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<TokenPair>> signup(@Valid @RequestBody SignupRequest request);
}
