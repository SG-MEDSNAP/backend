package backend.medsnap.infra.oauth.verifier;

import backend.medsnap.infra.oauth.exception.OidcVerificationException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import lombok.RequiredArgsConstructor;

import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Objects;

@RequiredArgsConstructor
public abstract class AbstractOidcVerifier {

    protected abstract String[] getIssuers();
    protected abstract JwkProvider getJwkProvider();

    protected String[] getAllowedAlgs() {
        return new String[]{"RS256"};
    }

    private final String clientId;

    public DecodedJWT verify(String idToken) {
        try {
            DecodedJWT jwt = JWT.decode(idToken);

            // alg 방어
            String alg = jwt.getAlgorithm();
            boolean algAllowed = false;
            for (String allowed : getAllowedAlgs()) {
                if (Objects.equals(allowed, alg)) {
                    algAllowed = true;
                    break;
                }
            }

            if (!algAllowed) {
                throw new OidcVerificationException("지원하지 않는 서명 알고리즘: " + alg, null);
            }

            // kid 방어
            String kid = jwt.getKeyId();
            if (kid == null || kid.isBlank()) {
                throw new OidcVerificationException("ID 토큰 헤더에 kid가 없습니다.", null);
            }

            // JWK로 공개키 가져오기
            PublicKey pk = getJwkProvider().get(kid).getPublicKey();
            if (!(pk instanceof RSAPublicKey)) {
                throw new OidcVerificationException("지원하지 않는 공개키 타입: " + pk.getAlgorithm(), null);
            }

            // 검증기 구성
            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) pk, null);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(getIssuers())
                    .withAudience(clientId)
                    .acceptLeeway(60) //  서버 간 시계 오차 허용 (초)
                    .build();

            DecodedJWT verified = verifier.verify(idToken);

            return verified;

        } catch (JWTVerificationException e) {
            throw new OidcVerificationException("ID 토큰 검증에 실패했습니다.", e);
        } catch (OidcVerificationException e) {
            throw e;
        } catch (Exception e) {
            throw new OidcVerificationException("ID 토큰 검증 중 알 수 없는 오류가 발생했습니다.", e);
        }
    }
}
