package backend.medsnap.domain.pushToken.dto.response;

import backend.medsnap.domain.pushToken.entity.Platform;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PushTokenResponse {
    private String token;
    private Platform platform;
}
