package backend.medsnap.domain.auth.dto.token;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@Builder
public class CustomUserDetails implements UserDetails {

    @NonNull
    private final Long id;
    @Builder.Default
    @NonNull
    private final Collection<? extends GrantedAuthority> authorities = Collections.emptyList(); // 권한 정보

    @Override
    public String getPassword() {
        return null; // JWT 인증이므로 패스워드 불필요
    }

    @Override
    public String getUsername() {
        return String.valueOf(id); // 사용자명 -> 사용자 ID
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.unmodifiableCollection(authorities);
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
