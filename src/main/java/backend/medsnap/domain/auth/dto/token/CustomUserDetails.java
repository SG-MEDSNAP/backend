package backend.medsnap.domain.auth.dto.token;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@Builder
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final Collection<? extends GrantedAuthority> authorities; // 권한 정보

    @Override
    public String getPassword() {
        return null; // JWT 인증이므로 패스워드 불필요
    }

    @Override
    public String getUsername() {
        return String.valueOf(id); // 사용자명 -> 사용자 ID
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
