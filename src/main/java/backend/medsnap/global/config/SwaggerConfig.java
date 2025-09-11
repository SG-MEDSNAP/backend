package backend.medsnap.global.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

    @Value("${https.server.url}")
    private String httpsServerUrl;

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info().title("MEDSNAP API Docs").version("1.0").description("API 명세서");

        // 서버 URL 설정
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8080");
        localServer.setDescription("MEDSNAP 로컬 서버");

        Server httpsServer = new Server();
        httpsServer.setUrl(httpsServerUrl);
        httpsServer.setDescription("MEDSNAP https 서버");

        return new OpenAPI().info(info).servers(List.of(localServer, httpsServer));
    }
}
