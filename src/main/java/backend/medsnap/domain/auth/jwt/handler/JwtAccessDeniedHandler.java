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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {

        log.error("권한 부족: {}", accessDeniedException.getMessage());

        // HTTP 응답 설정 (403 Forbidden)
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        // 에러 응답 객체 생성
        ApiResponse<Void> errorResponse = ApiResponse.error(ErrorCode.AUTH_ACCESS_DENIED);

        // 에러 응답 전송
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
