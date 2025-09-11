package backend.medsnap.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                        authz ->
                                authz
                                        // Swagger UI 관련 경로 허용
                                        .requestMatchers("/api/v1/docs/**")
                                        .authenticated()
                                        .requestMatchers("/api/v1/api-docs/**")
                                        .authenticated()
                                        .requestMatchers("/swagger-ui/**")
                                        .authenticated()
                                        // 기타 API 경로 설정
                                        .anyRequest()
                                        .authenticated())
                .httpBasic(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
