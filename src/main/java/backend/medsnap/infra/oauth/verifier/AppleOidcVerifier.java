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

@Component("appleOidcVerifier")
public class AppleOidcVerifier extends AbstractOidcVerifier {

    private final String[] issuers;
    private final String[] allowedAlgs;
    private final JwkProvider jwkProvider;

    public AppleOidcVerifier(
            @Value("${apple.ios.client-id:}") String iosClientId,
            @Value("${apple.web.client-id:}") String webClientId,
            OidcDiscoveryClient discoveryClient) {

        super(new String[] {iosClientId, webClientId});

        if (iosClientId == null || iosClientId.isBlank()) {
            throw new IllegalStateException("apple.ios.client-id가 설정되지 않았습니다.");
        }

        if (webClientId == null || webClientId.isBlank()) {
            throw new IllegalStateException("apple.web.client-id가 설정되지 않았습니다.");
        }

        String discoveryUrl = "https://appleid.apple.com/.well-known/openid-configuration";
        OidcDiscoveryProperties props = discoveryClient.fetch(discoveryUrl);

        this.issuers = new String[] {props.getIssuer()};

        final List<String> algs = props.getId_token_signing_alg_values_supported();
        this.allowedAlgs =
                (algs == null || algs.isEmpty())
                        ? new String[] {"RS256"}
                        : algs.toArray(new String[0]);

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
