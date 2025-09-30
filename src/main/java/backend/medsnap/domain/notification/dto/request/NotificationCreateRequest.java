package backend.medsnap.domain.notification.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationCreateRequest {

    @NotBlank(message = "title은 필수입니다.")
    private String title;

    @NotBlank(message = "body는 필수입니다.")
    private String body;

    private Map<String, Object> data;

    @NotNull(message = "scheduledAt은 필수입니다.")
    private LocalDateTime scheduledAt;
}
