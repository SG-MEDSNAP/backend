package backend.medsnap.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class RefreshRequest {

    @NotBlank(message = "리프레시 토큰은 비어있을 수 없습니다.")
    private String refreshToken;
}
