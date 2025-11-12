package backend.medsnap.domain.auth.dto.request;

import backend.medsnap.domain.user.entity.Provider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "idToken은 필수입니다.")
    private String idToken;

    @NotNull(message = "provider는 필수입니다.")
    private Provider provider;

    // apple 최초 로그인 시 포함
    private String appleUserJson;
}
