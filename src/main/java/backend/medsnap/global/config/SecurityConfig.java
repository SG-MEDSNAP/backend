package backend.medsnap.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

import backend.medsnap.domain.auth.jwt.filter.JwtAuthenticationFilter;
import backend.medsnap.domain.auth.jwt.handler.JwtAccessDeniedHandler;
import backend.medsnap.domain.auth.jwt.handler.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${swagger.username}")
    private String swaggerUsername;

    @Value("${swagger.password}")
    private String swaggerPassword;

    @Value("${swagger.auth.enabled}")
    private boolean swaggerAuthEnabled;

    @Value("${security.require-ssl}")
    private boolean requireSsl;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Swagger 접근용 사용자 생성
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        return new InMemoryUserDetailsManager(
                User.withUsername(swaggerUsername)
                        .password(encoder.encode(swaggerPassword))
                        .roles("DOCS")
                        .build());
    }

    // Swagger 전용 필터 체인 (JWT 인증 없이 Basic 인증만)
    @Bean
    @Order(1)
    public SecurityFilterChain swaggerSecurityFilterChain(HttpSecurity http) throws Exception {
        HttpSecurity swaggerHttp =
                http.securityMatcher(
                        "/api/v1/docs/**",
                        "/api/v1/api-docs/**",
                        "/api/v1/swagger-ui/**",
                        "/api/v1/swagger-ui.html");

        if (swaggerAuthEnabled) {
            swaggerHttp
                    .authorizeHttpRequests(auth -> auth.anyRequest().hasRole("DOCS"))
                    .httpBasic(httpBasic -> httpBasic.realmName("Swagger API Documentation"));
        } else {
            swaggerHttp.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        }

        swaggerHttp
                .csrf(csrf -> csrf.disable())
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // HTTPS 설정
        if (requireSsl) {
            swaggerHttp
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

        return swaggerHttp.build();
    }

    // API 전용 필터 체인 (JWT 인증)
    @Bean
    @Order(2)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers("/api/v1/auth/**", "/error", "/error/**")
                                        .permitAll()
                                        .anyRequest()
                                        .authenticated())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(login -> login.disable())
                .addFilterBefore(
                        jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(
                        exception ->
                                exception
                                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                                        .accessDeniedHandler(jwtAccessDeniedHandler));

        // HTTPS 설정
        if (requireSsl) {
            http.requiresChannel(channel -> channel.anyRequest().requiresSecure())
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

        return http.build();
    }
}
