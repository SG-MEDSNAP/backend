package backend.medsnap.domain.pushToken.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpsertPushTokenRequest {

    @NotBlank(message = "푸시 토큰은 필수입니다.")
    private String token;

    @NotBlank(message = "플랫폼 정보는 필수입니다.")
    private String platform;
}
