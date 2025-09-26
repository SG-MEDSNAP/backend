package backend.medsnap.domain.user.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class MyPageUpdateRequest {

    @NotNull(message = "이름은 필수입니다.")
    private String name;

    @NotNull(message = "생일은 필수입니다.")
    @Past(message = "생일은 현재 날짜보다 이전이어야 합니다.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthday;

    private String phone;

    private String caregiverPhone;

    @NotNull(message = "앱 알림 동의 여부는 필수입니다.")
    private Boolean isPushConsent;
}
