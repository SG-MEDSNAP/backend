package backend.medsnap.global.config;

import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Bean
    public Algorithm jwtAlgorithm(@Value("${jwt.secret}") String secretKey) {
        return Algorithm.HMAC256(secretKey);
    }

    @Bean
    public String jwtIssuer(@Value("${jwt.issuer}") String issuer) {
        return issuer;
    }
}
