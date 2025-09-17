package backend.medsnap.domain.faq.dto.response;

import backend.medsnap.domain.faq.entity.FaqCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FaqResponse {

    @Schema(description = "FAQ ID")
    private Long id;

    @Schema(description = "질문")
    private String question;

    @Schema(description = "답변")
    private String answer;

    @Schema(description = "FAQ 카테고리")
    private FaqCategory category;

    @Schema(description = "생성 시간")
    private final LocalDateTime createdAt;

    @Schema(description = "수정 시간")
    private final LocalDateTime updatedAt;
}
