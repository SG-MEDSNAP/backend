package backend.medsnap.domain.auth.jwt;

import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import backend.medsnap.domain.auth.exception.InvalidJwtTokenException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenValidator {

    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    public JwtTokenValidator(Algorithm algorithm, String issuer) {
        this.algorithm = algorithm;
        this.verifier =
                JWT.require(algorithm)
                        .withIssuer(issuer)
                        .acceptLeeway(5) // 시계 오차 허용 (5초)
                        .build();
    }

    // JWT 토큰 검증 및 디코딩
    public DecodedJWT validateToken(String token) {
        try {
            return verifier.verify(token);
        } catch (JWTVerificationException e) {
            log.error("JWT 검증 실패: {}", e.getMessage());
            throw new InvalidJwtTokenException();
        }
    }

    // 토큰에서 사용자 ID 추출
    public Long getUserIdFromToken(String token) {
        DecodedJWT decodedJWT = validateToken(token);
        String sub = decodedJWT.getSubject();
        try {
            return Long.parseLong(sub);
        } catch (RuntimeException ex) {
            log.warn("JWT subject 파싱 실패. subject={}", sub);
            throw new InvalidJwtTokenException();
        }
    }
}
