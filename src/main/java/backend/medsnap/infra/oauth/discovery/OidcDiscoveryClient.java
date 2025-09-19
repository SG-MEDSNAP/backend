package backend.medsnap.infra.oauth.discovery;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OidcDiscoveryClient {

    public OidcDiscoveryProperties fetch(String discoveryUrl) {

        // 타임아웃
        SimpleClientHttpRequestFactory f = new SimpleClientHttpRequestFactory();
        f.setConnectTimeout(3000); // 연결 대기 3초
        f.setReadTimeout(3000);    // 응답 대기 3초

        RestTemplate rt = new RestTemplate(f);
        return rt.getForObject(discoveryUrl, OidcDiscoveryProperties.class);
    }
}
