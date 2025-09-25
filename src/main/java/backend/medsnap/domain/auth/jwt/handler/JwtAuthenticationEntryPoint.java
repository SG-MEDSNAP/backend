package backend.medsnap.domain.auth.jwt.handler;

import backend.medsnap.global.dto.ApiResponse;
import backend.medsnap.global.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        log.error("인증 실패: {}", authException.getMessage());

        // 4. HTTP 응답 설정 (401 Unauthorized)
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // 에러 응답 객체 생성
        ApiResponse<Void> errorResponse = ApiResponse.error(ErrorCode.AUTH_INVALID_JWT_TOKEN);

        // JSON 형태로 에러 응답 전송
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
