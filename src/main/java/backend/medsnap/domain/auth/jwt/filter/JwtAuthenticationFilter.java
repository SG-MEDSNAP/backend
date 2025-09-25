package backend.medsnap.domain.auth.jwt.filter;

import backend.medsnap.domain.auth.jwt.JwtTokenValidator;
import backend.medsnap.domain.auth.service.CustomUserDetailsService;
import backend.medsnap.global.exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenValidator jwtTokenValidator;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            // JWT 토큰 추출
            String jwt = extractTokenFromRequest(request);

            if (StringUtils.hasText(jwt)) {
                // 토큰에서 사용자 ID 추출
                Long userId = jwtTokenValidator.getUserIdFromToken(jwt);

                // 아직 인증되지 않은 요청인 경우에만 처리
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    // 사용자 정보 조회
                    UserDetails userDetails = customUserDetailsService.loadUserById(userId);

                    // Spring Security 인증 객체 생성
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // SecurityContext에 인증 정보 저장
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    log.debug("사용자 인증 성공: userId={}", userId);
                }
            }
        } catch (Exception e) {
            log.error("사용자 인증 처리 중 오류 발생: {}", e.getMessage());
            // 예외 발생 시 SecurityContext 초기화
            SecurityContextHolder.clearContext();
            // JWT 토큰 관련 에러 코드를 request attribute에 설정
            request.setAttribute("exception", ErrorCode.AUTH_INVALID_JWT_TOKEN);
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    // Authorization 헤더에서 Bearer 토큰 추출
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {

            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
