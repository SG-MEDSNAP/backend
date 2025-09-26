package backend.medsnap.domain.user.dto.response;

import backend.medsnap.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class MyPageResponse {

    private Long id;
    private String name;
    private LocalDate birthday;
    private String phone;
    private String caregiverPhone;
    private Boolean isPushConsent;

    public static MyPageResponse from(User u) {
        return new MyPageResponse(u.getId(), u.getName(), u.getBirthday(), u.getPhone(), u.getCaregiverPhone(), u.getIsPushConsent());
    }
}
