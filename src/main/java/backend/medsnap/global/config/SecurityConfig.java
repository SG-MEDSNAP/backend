package backend.medsnap.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Value("${swagger.username}")
    private String swaggerUsername;

    @Value("${swagger.password}")
    private String swaggerPassword;

    @Value("${swagger.auth.enabled}")
    private boolean swaggerAuthEnabled;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        return new InMemoryUserDetailsManager(
                User.withUsername(swaggerUsername)
                        .password(encoder.encode(swaggerPassword))
                        .roles("DOCS")
                        .build());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        authz -> {
                            if (swaggerAuthEnabled) {
                                // 배포 환경: Swagger에 인증 필요
                                authz.requestMatchers(
                                                "/api/v1/docs/**",
                                                "/api/v1/api-docs/**",
                                                "/swagger-ui/**",
                                                "/swagger-ui.html")
                                        .hasRole("DOCS")
                                        .anyRequest()
                                        .permitAll();
                            } else {
                                // 로컬 환경: Swagger 인증 없이 접근 허용
                                authz.requestMatchers(
                                                "/api/v1/docs/**",
                                                "/api/v1/api-docs/**",
                                                "/swagger-ui/**",
                                                "/swagger-ui.html")
                                        .permitAll()
                                        .anyRequest()
                                        .permitAll();
                            }
                        })
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
