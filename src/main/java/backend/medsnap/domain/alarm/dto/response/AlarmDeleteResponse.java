package backend.medsnap.domain.alarm.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AlarmDeleteResponse {

    private String message;

    public static AlarmDeleteResponse of(String message) {
        return AlarmDeleteResponse.builder()
                .message(message)
                .build();
    }
}
