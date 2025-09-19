package backend.medsnap.infra.oauth.verifier;

import backend.medsnap.infra.oauth.discovery.OidcDiscoveryClient;
import backend.medsnap.infra.oauth.discovery.OidcDiscoveryProperties;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component("kakaoOidcVerifier")
public class KakaoOidcVerifier extends AbstractOidcVerifier {

    private final String[] issuers;
    private final String[] allowedAlgs;
    private final JwkProvider jwkProvider;

    public KakaoOidcVerifier(
            @Value("${kakao.client-id}") String clientId,
            OidcDiscoveryClient discoveryClient
    ) {
        super(clientId);

        String discoveryUrl = "https://kauth.kakao.com/.well-known/openid-configuration";
        OidcDiscoveryProperties props = discoveryClient.fetch(discoveryUrl);

        this.issuers = new String[]{ props.getIssuer() };

        List<String> algs = props.getId_token_signing_alg_values_supported();
        this.allowedAlgs = (algs == null || algs.isEmpty())
                ? new String[] { "RS256" }
                : algs.toArray(new String[0]);

        this.jwkProvider = new JwkProviderBuilder(props.getJwks_uri())
                .cached(10, 1, TimeUnit.HOURS) // 1시간 캐싱
                .rateLimited(10, 1, TimeUnit.MINUTES) // 분당 10회 요청 제한
                .build();
    }

    @Override
    protected String[] getIssuers() {
        return issuers;
    }

    @Override
    protected JwkProvider getJwkProvider() {
        return jwkProvider;
    }

    @Override
    protected String[] getAllowedAlgs() {
        return allowedAlgs;
    }

}
