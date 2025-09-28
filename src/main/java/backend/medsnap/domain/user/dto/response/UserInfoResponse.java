package backend.medsnap.domain.user.dto.response;

import backend.medsnap.domain.user.entity.Provider;
import backend.medsnap.domain.user.entity.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoResponse {

    private Long id;
    private Role role;
    private String name;
    private Provider provider;
}
