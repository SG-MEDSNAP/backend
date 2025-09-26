package backend.medsnap.domain.auth.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import jakarta.validation.constraints.Past;
import org.springframework.format.annotation.DateTimeFormat;

import backend.medsnap.domain.user.entity.Provider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

    @NotBlank(message = "Id Token은 필수입니다.")
    private String idToken;

    @NotNull(message = "provider는 필수입니다.")
    private Provider provider;

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotNull(message = "생일은 필수입니다.")
    @Past(message = "생일은 현재 날짜보다 이전이어야 합니다.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthday;

    @NotBlank(message = "전화번호는 필수입니다.")
    private String phone;

    private String caregiverPhone;

    @NotNull(message = "푸시 동의 여부는 필수입니다.")
    private Boolean isPushConsent;
}
