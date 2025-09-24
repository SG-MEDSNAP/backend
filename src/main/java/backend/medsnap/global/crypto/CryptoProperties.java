package backend.medsnap.global.crypto;

import backend.medsnap.domain.auth.exception.CryptoKeyInvalidException;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.crypto")
public class CryptoProperties {

    private String aesSecretKey;

    public String getAesSecretKey() {
        return aesSecretKey;
    }

    public CryptoProperties(String aesSecretKey) {
        if (aesSecretKey == null || aesSecretKey.length() != 32) {
            throw new CryptoKeyInvalidException();
        }
        this.aesSecretKey = aesSecretKey;
    }
}
