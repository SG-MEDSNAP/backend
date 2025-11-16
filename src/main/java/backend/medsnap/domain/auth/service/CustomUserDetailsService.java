package backend.medsnap.domain.auth.service;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.medsnap.domain.auth.dto.token.CustomUserDetails;
import backend.medsnap.domain.user.entity.User;
import backend.medsnap.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service("customUserDetailsService")
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // username으로 사용자 조회
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            // username을 userId로 해석하여 처리
            Long userId = Long.parseLong(username);
            return loadUserById(userId);
        } catch (NumberFormatException ex) {
            throw new UsernameNotFoundException("유효하지 않은 사용자 식별자: " + username);
        }
    }

    // 사용자 ID로 직접 사용자 정보 조회
    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long userId) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(
                                () -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId));

        return CustomUserDetails.builder()
                .id(user.getId())
                .authorities(
                        Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())))
                .build();
    }
}
