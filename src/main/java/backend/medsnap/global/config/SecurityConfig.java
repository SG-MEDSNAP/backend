package backend.medsnap.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${swagger.username}")
    private String swaggerUsername;

    @Value("${swagger.password}")
    private String swaggerPassword;

    @Value("${swagger.auth.enabled}")
    private boolean swaggerAuthEnabled;

    @Value("${security.require-ssl}")
    private boolean requireSsl;

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
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http
                .formLogin((login) -> login.disable());

        // HTTPS 강제 설정
        if (requireSsl) {
            http
                    .requiresChannel(channel -> channel.anyRequest().requiresSecure())
                    .headers(
                            headers ->
                                    headers.httpStrictTransportSecurity(
                                                    hstsConfig ->
                                                            hstsConfig
                                                                    .maxAgeInSeconds(31536000)
                                                                    .includeSubDomains(true))
                                            .referrerPolicy(
                                                    referrer ->
                                                            referrer.policy(
                                                                    ReferrerPolicyHeaderWriter
                                                                            .ReferrerPolicy
                                                                            .STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                                            .frameOptions(frame -> frame.deny())
                                            .contentTypeOptions(contentType -> {}));
        }

        http
                .oauth2Login(Customizer.withDefaults());

        // 권한 설정
        http
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
                                        .requestMatchers(
                                                "/login**",
                                                "/api/v1/login**",
                                                "/oauth2/**",
                                                "/error**")
                                        .permitAll()
                                        .anyRequest()
                                        .authenticated();
                            } else {
                                // 로컬 환경: Swagger 인증 없이 접근 허용
                                authz.requestMatchers(
                                                "/api/v1/docs/**",
                                                "/api/v1/api-docs/**",
                                                "/swagger-ui/**",
                                                "/swagger-ui.html",
                                                "/login**",
                                                "/api/v1/login**",
                                                "/oauth2/**",
                                                "/error**")
                                        .permitAll()
                                        .anyRequest()
                                        .permitAll();
                            }
                        })
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
