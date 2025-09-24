package backend.medsnap.infra.oauth.verifier;

import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;

import backend.medsnap.infra.oauth.discovery.OidcDiscoveryClient;
import backend.medsnap.infra.oauth.discovery.OidcDiscoveryProperties;
import backend.medsnap.infra.oauth.exception.JwkProviderInitializationException;

@Component("googleOidcVerifier")
public class GoogleOidcVerifier extends AbstractOidcVerifier {

    private final String[] issuers;
    private final String[] allowedAlgs;
    private final JwkProvider jwkProvider;

    public GoogleOidcVerifier(
            @Value("${google.ios.client-id}") String iosClientId,
            @Value("${google.android.client-id}") String androidClientId,
            @Value("${google.client-id}") String clientId,
            OidcDiscoveryClient discoveryClient) {
        super(new String[] {iosClientId, androidClientId, clientId});
        if (iosClientId == null || iosClientId.isBlank()) {
            throw new IllegalStateException("google.ios.client-id가 설정되지 않았습니다.");
        }
        if (androidClientId == null || androidClientId.isBlank()) {
            throw new IllegalStateException("google.android.client-id가 설정되지 않았습니다.");
        }
        if (clientId == null || clientId.isBlank()) {
            throw new IllegalStateException("google.client-id가 설정되지 않았습니다.");
        }

        // 디스커버리 조회
        String discoveryUrl = "https://accounts.google.com/.well-known/openid-configuration";
        OidcDiscoveryProperties props = discoveryClient.fetch(discoveryUrl);

        // issuer
        this.issuers = new String[] {props.getIssuer(), "accounts.google.com"};

        // 허용 알고리즘 목록
        List<String> algs = props.getId_token_signing_alg_values_supported();
        this.allowedAlgs =
                (algs == null || algs.isEmpty())
                        ? new String[] {"RS256"}
                        : algs.toArray(new String[0]);

        // JWK Provider 초기화
        try {
            this.jwkProvider =
                    new JwkProviderBuilder(new URL(props.getJwks_uri()))
                            .cached(10, 1, TimeUnit.HOURS)
                            .rateLimited(10, 1, TimeUnit.MINUTES)
                            .build();
        } catch (Exception e) {
            throw new JwkProviderInitializationException(e);
        }
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
