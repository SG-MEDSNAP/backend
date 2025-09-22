package backend.medsnap.infra.oauth.verifier;

import backend.medsnap.infra.oauth.exception.OidcVerificationException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Objects;

public abstract class AbstractOidcVerifier {

    protected abstract String[] getIssuers();
    protected abstract JwkProvider getJwkProvider();

    protected String[] getAllowedAlgs() {
        return new String[]{"RS256"};
    }

    protected final String[] clientId;

    public AbstractOidcVerifier(String[] clientId) {
        this.clientId = clientId;
    }

    public DecodedJWT verify(String idToken) {
        try {
            DecodedJWT jwt = JWT.decode(idToken);

            // 🔎 디버그 로그 (임시)
            System.out.println("[OIDC] alg=" + jwt.getAlgorithm()
                    + " kid=" + jwt.getKeyId()
                    + " iss=" + jwt.getIssuer()
                    + " aud=" + jwt.getAudience()
                    + " azp=" + jwt.getClaim("azp").asString());

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

            // JWK 공개키 조회
            PublicKey pk = getJwkProvider().get(kid).getPublicKey();
            if (!(pk instanceof RSAPublicKey)) {
                throw new OidcVerificationException("지원하지 않는 공개키 타입: " + pk.getAlgorithm(), null);
            }

            // 1차 검증: 서명 + issuer
            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) pk, null);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(getIssuers())
                    .acceptLeeway(60) //  서버 간 시계 오차 허용 (초)
                    .build();

            DecodedJWT verified = verifier.verify(idToken);

            // 2차 커스텀 체크: audience 허용 목록 중 하나라도 일치
            var audList = verified.getAudience(); // List<String>
            boolean audienceOk = false;
            for (String aud : audList) {
                for (String allowedClient : clientId) {
                    if (allowedClient != null && !allowedClient.isBlank() && allowedClient.equals(aud)) {
                        audienceOk = true; break;
                    }
                }
                if (audienceOk) break;
            }
            if (!audienceOk) {
                throw new OidcVerificationException("aud 불일치", null);
            }

            // azp 존재 시 허용 clientId와 일치
            String azp = verified.getClaim("azp").asString();
            if (azp != null && !azp.isBlank()) {
                boolean azpOk = false;
                for (String allowedClient : clientId) {
                    if (allowedClient != null && !allowedClient.isBlank() && allowedClient.equals(azp)) {
                        azpOk = true; break;
                    }
                }
                if (!azpOk) {
                    throw new OidcVerificationException("azp 불일치", null);
                }
            }

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
