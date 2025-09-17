package backend.medsnap.domain.faq.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import backend.medsnap.domain.faq.dto.request.FaqRequest;
import backend.medsnap.domain.faq.dto.response.FaqResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "faqs", description = "FAQ API")
public interface FaqSwagger {

    @Operation(summary = "FAQ 등록", description = "새로운 FAQ를 등록합니다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "FAQ 등록 요청",
            content = @Content(mediaType = "application/json"))
    @ApiResponses(
            value = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "201",
                        description = "FAQ 등록 성공",
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
                            "question": "약을 복용하는 시간을 변경할 수 있나요?",
                            "answer": "네, 언제든지 약 복용 시간을 변경할 수 있습니다. 앱에서 알람 설정을 수정하시면 됩니다.",
                            "category": "MEDICATION_STATUS",
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
    ResponseEntity<backend.medsnap.global.dto.ApiResponse<FaqResponse>> createFaq(
            @io.swagger.v3.oas.annotations.Parameter(description = "FAQ 등록 요청 데이터", required = true)
                    @RequestBody
                    @Valid
                    FaqRequest request);

    @Operation(summary = "FAQ 수정", description = "기존 FAQ를 수정합니다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "FAQ 수정 요청",
            content = @Content(mediaType = "application/json"))
    @ApiResponses(
            value = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "200",
                        description = "FAQ 수정 성공",
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
                            "question": "수정된 질문입니다.",
                            "answer": "수정된 답변입니다.",
                            "category": "MEDICATION_STATUS",
                            "createdAt": "2024-01-01T10:00:00",
                            "updatedAt": "2024-01-01T11:00:00"
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
                        responseCode = "404",
                        description = "FAQ를 찾을 수 없음",
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
                          "code": "F001",
                          "httpStatus": 404,
                          "message": "FAQ를 찾을 수 없습니다.",
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
    ResponseEntity<backend.medsnap.global.dto.ApiResponse<FaqResponse>> updateFaq(
            @io.swagger.v3.oas.annotations.Parameter(
                            description = "수정할 FAQ의 ID",
                            required = true,
                            example = "1")
                    @PathVariable("faqId")
                    Long faqId,
            @io.swagger.v3.oas.annotations.Parameter(description = "FAQ 수정 요청 데이터", required = true)
                    @RequestBody
                    @Valid
                    FaqRequest request);

    @Operation(summary = "FAQ 삭제", description = "기존 FAQ를 삭제합니다.")
    @ApiResponses(
            value = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "204",
                        description = "FAQ 삭제 성공"),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "404",
                        description = "FAQ를 찾을 수 없음",
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
                          "code": "F001",
                          "httpStatus": 404,
                          "message": "FAQ를 찾을 수 없습니다.",
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
    ResponseEntity<Void> deleteFaq(
            @io.swagger.v3.oas.annotations.Parameter(
                            description = "삭제할 FAQ의 ID",
                            required = true,
                            example = "1")
                    @PathVariable("faqId")
                    Long faqId);
}
