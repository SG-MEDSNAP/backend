package backend.medsnap.domain.faq.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import backend.medsnap.domain.faq.entity.FaqCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FaqRequest {

    @NotBlank(message = "질문은 필수입니다.")
    private String question;

    @NotBlank(message = "답변은 필수입니다.")
    private String answer;

    @NotNull(message = "카테고리는 필수입니다.")
    private FaqCategory category;
}
