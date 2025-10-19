package backend.medsnap.global.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
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

    /** Swagger 접근용 사용자 정의 */
    @Bean(name = "swaggerUserDetailsService")
    public UserDetailsService swaggerUserDetailsService(PasswordEncoder encoder) {
        return new InMemoryUserDetailsManager(
                User.withUsername(swaggerUsername)
                        .password(encoder.encode(swaggerPassword))
                        .roles("DOCS")
                        .build());
    }

    /** Swagger 전용 필터 체인 (Basic Auth) */
    @Bean
    @Order(1)
    public SecurityFilterChain swaggerSecurityFilterChain(
            HttpSecurity http,
            PasswordEncoder encoder,
            @Qualifier("swaggerUserDetailsService") UserDetailsService swaggerUserDetailsService)
            throws Exception {
        
        // HttpSecurity 객체에 직접 securityMatcher 적용
        http.securityMatcher(
                        "/api/v1/docs/**",
                        "/api/v1/api-docs/**",
                        "/api/v1/swagger-ui/**",
                        "/api/v1/swagger-ui.html",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**");

        if (swaggerAuthEnabled) {
            // Swagger 전용 AuthenticationManager 구성
            AuthenticationManagerBuilder authBuilder =
                    http.getSharedObject(AuthenticationManagerBuilder.class);
            authBuilder.userDetailsService(swaggerUserDetailsService).passwordEncoder(encoder);
            AuthenticationManager authManager = authBuilder.build();

            http
                    .authenticationManager(authManager)
                    .authorizeHttpRequests(auth -> auth.anyRequest().hasRole("DOCS"))
                    .httpBasic(httpBasic -> httpBasic.realmName("Swagger API Documentation"));
        } else {
            http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        }

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // HTTPS 설정
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

        return http.build();
    }

    /** API 전용 필터 체인 (JWT) */
    @Bean
    @Order(2)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers("/", "/api/v1/auth/**", "/error", "/error/**")
                                        .permitAll()
                                        // FAQ 조회는 인증된 모든 사용자 가능
                                        .requestMatchers(HttpMethod.GET, "/api/v1/faqs/**")
                                        .authenticated()
                                        // FAQ 등록, 수정, 삭제는 ADMIN만 가능
                                        .requestMatchers(HttpMethod.POST, "/api/v1/faqs/**")
                                        .hasRole("ADMIN")
                                        .requestMatchers(HttpMethod.PUT, "/api/v1/faqs/**")
                                        .hasRole("ADMIN")
                                        .requestMatchers(HttpMethod.DELETE, "/api/v1/faqs/**")
                                        .hasRole("ADMIN")
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
