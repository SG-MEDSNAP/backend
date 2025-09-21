package backend.medsnap.domain.auth.jwt;

import backend.medsnap.domain.auth.dto.token.TokenPair;
import backend.medsnap.domain.user.entity.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final Algorithm algorithm;
    private final String issuer;
    private final long accessTokenValidityHours;
    private final long refreshTokenValidityDays;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.issuer}") String issuer,
            @Value("${jwt.access-token-validity-hours}") long accessTokenValidityHours,
            @Value("${jwt.refresh-token-validity-days}") long refreshTokenValidityDays
    ) {
        this.algorithm = Algorithm.HMAC256(secretKey);
        this.issuer = issuer;
        this.accessTokenValidityHours = accessTokenValidityHours;
        this.refreshTokenValidityDays = refreshTokenValidityDays;
    }

    public TokenPair createTokenPair(User user) {
        String accessToken = createAccessToken(user.getId());
        String refreshToken = createRefreshToken(user.getId());
        return new TokenPair(accessToken, refreshToken);
    }

    private String createAccessToken(Long userId) {
        Date expiresAt = Date.from(Instant.now().plus(accessTokenValidityHours, ChronoUnit.HOURS));

        return JWT.create()
                .withIssuer(issuer)
                .withSubject(String.valueOf(userId))
                .withExpiresAt(expiresAt)
                .sign(algorithm);
    }

    private String createRefreshToken(Long userId) {
        Date expiresAt = Date.from(Instant.now().plus(refreshTokenValidityDays, ChronoUnit.DAYS));

        return JWT.create()
                .withIssuer(issuer)
                .withSubject(String.valueOf(userId))
                .withExpiresAt(expiresAt)
                .sign(algorithm);
    }
}
