package backend.medsnap.infra.oauth.discovery;

import lombok.Getter;

import java.util.List;

@Getter
public class OidcDiscoveryProperties {
    private String issuer;
    private String jwks_uri;
    private List<String> id_token_signing_alg_values_supported;
}
