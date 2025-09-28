package backend.medsnap.domain.user.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import org.springframework.format.annotation.DateTimeFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(
        description = "마이페이지 수정 요청",
        example =
                """
    {
      "name": "홍길동",
      "birthday": "1990-01-01",
      "phone": "010-1234-5678",
      "isPushConsent": true
    }
    """)
public class MyPageUpdateRequest {

    @NotNull(message = "이름은 필수입니다.")
    @Schema(description = "사용자 이름", example = "홍길동")
    private String name;

    @NotNull(message = "생일은 필수입니다.")
    @Past(message = "생일은 현재 날짜보다 이전이어야 합니다.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(description = "생일 (YYYY-MM-DD 형식, 현재 날짜보다 이전이어야 함)", example = "1990-01-01")
    private LocalDate birthday;

    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phone;

    // @Schema(description = "보호자 전화번호", example = "010-9876-5432")
    // private String caregiverPhone;

    @NotNull(message = "앱 알림 동의 여부는 필수입니다.")
    @Schema(description = "앱 알림 동의 여부", example = "true")
    private Boolean isPushConsent;
}
