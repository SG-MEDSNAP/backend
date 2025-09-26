package backend.medsnap.domain.auth.jwt;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import backend.medsnap.domain.auth.dto.token.TokenPair;
import backend.medsnap.domain.user.entity.User;

@Component
public class JwtTokenProvider {

    private final Algorithm algorithm;
    private final String issuer;
    private final long accessTokenValidityHours;
    private final long refreshTokenValidityDays;

    public JwtTokenProvider(
            Algorithm algorithm,
            String issuer,
            @Value("${jwt.access-token-validity-hours}") long accessTokenValidityHours,
            @Value("${jwt.refresh-token-validity-days}") long refreshTokenValidityDays) {
        this.algorithm = algorithm;
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
                .withClaim("typ", "access")
                .withExpiresAt(expiresAt)
                .sign(algorithm);
    }

    private String createRefreshToken(Long userId) {
        Date expiresAt = Date.from(Instant.now().plus(refreshTokenValidityDays, ChronoUnit.DAYS));

        return JWT.create()
                .withIssuer(issuer)
                .withSubject(String.valueOf(userId))
                .withClaim("typ", "refresh")
                .withExpiresAt(expiresAt)
                .sign(algorithm);
    }

    private JWTVerifier baseVerifier() {
        return JWT.require(algorithm).withIssuer(issuer).build();
    }

    /** refresh 전용 검증 */
    public DecodedJWT verifyRefreshToken(String token) {
        DecodedJWT jwt = baseVerifier().verify(token);
        String typ = jwt.getClaim("typ").asString();

        if (!"refresh".equals(typ)) {
            throw new JWTVerificationException("Not a refresh token");
        }
        return jwt;
    }
}
