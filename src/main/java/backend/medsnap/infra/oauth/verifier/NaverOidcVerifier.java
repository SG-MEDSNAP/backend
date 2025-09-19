package backend.medsnap.infra.oauth.verifier;

import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component("naverOidcVerifier")
public class NaverOidcVerifier extends AbstractOidcVerifier {

    private static final String[] ISSUERS = { "https://nid.naver.com" }; // 수정 가능성
    private static final String JWKS_URL = "https://nid.naver.com/oauth2/jwks";

    private final JwkProvider jwkProvider;

    public NaverOidcVerifier(@Value("${naver.client-id}") String clientId) {
        super(clientId);
        this.jwkProvider = new JwkProviderBuilder(JWKS_URL)
                .cached(10, 1, TimeUnit.HOURS) // 1시간 캐싱
                .rateLimited(10, 1, TimeUnit.MINUTES) // 분당 10회 요청 제한
                .build();
    }

    @Override
    protected String[] getIssuers() {
        return ISSUERS;
    }

    @Override
    protected JwkProvider getJwkProvider() {
        return jwkProvider;
    }

    @Override
    protected String[] getAllowedAlgs() {
        return new String[] { "RS256" };
    }
}
