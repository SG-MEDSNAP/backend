package backend.medsnap.domain.auth.dto.request;

import backend.medsnap.domain.user.entity.Provider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class LoginRequest {

    @NotBlank(message = "idToken은 필수입니다.")
    private String idToken;

    @NotNull(message = "provider는 필수입니다.")
    private Provider provider;
}
