package backend.medsnap.global.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

    @Value("${https.server.url}")
    private String httpsServerUrl;

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info().title("MEDSNAP API Docs").version("1.0").description("API 명세서");

        String jwtSchemeName = "Bearer Authentication";

        // Security 요구사항 정의
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);

        // Security Scheme 정의 (JWT)
        Components components =
                new Components()
                        .addSecuritySchemes(
                                jwtSchemeName,
                                new SecurityScheme()
                                        .name(jwtSchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("access_token"));

        // 서버 URL 설정
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8000");
        localServer.setDescription("MEDSNAP 로컬 서버");

        if (httpsServerUrl != null
                && !httpsServerUrl.isEmpty()
                && !httpsServerUrl.equals("${HTTPS_SERVER_URL}")
                && httpsServerUrl.startsWith("https://")) {
            Server httpsServer = new Server();
            httpsServer.setUrl(httpsServerUrl);
            httpsServer.setDescription("MEDSNAP Production 서버 (HTTPS)");

            return new OpenAPI()
                    .info(info)
                    .servers(List.of(httpsServer, localServer))
                    .addSecurityItem(securityRequirement)
                    .components(components);
        } else {
            return new OpenAPI()
                    .info(info)
                    .servers(List.of(localServer))
                    .addSecurityItem(securityRequirement)
                    .components(components);
        }
    }
}
