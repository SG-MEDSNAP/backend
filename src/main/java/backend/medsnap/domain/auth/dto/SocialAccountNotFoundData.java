package backend.medsnap.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SocialAccountNotFoundData {
    private final String nameHint;
}
