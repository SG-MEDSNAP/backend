package backend.medsnap.infra.oauth.verifier;

import backend.medsnap.infra.oauth.exception.JwkProviderInitializationException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.concurrent.TimeUnit;

@Component("naverOidcVerifier")
public class NaverOidcVerifier extends AbstractOidcVerifier {

    private static final String[] ISSUERS = { "https://nid.naver.com" };
    private static final String JWKS_URL = "https://nid.naver.com/oauth2/jwks";

    private final JwkProvider jwkProvider;

    public NaverOidcVerifier(@Value("${naver.client-id}") String clientId) {
        super(new String[]{clientId});
        try {
            this.jwkProvider = new JwkProviderBuilder(new URL(JWKS_URL))
                    .cached(10, 1, TimeUnit.HOURS)
                    .rateLimited(10, 1, TimeUnit.MINUTES)
                    .build();
        } catch (Exception e) {
            throw new JwkProviderInitializationException(e);
        }
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
