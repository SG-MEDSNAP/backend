package backend.medsnap.global.crypto;

import backend.medsnap.domain.auth.exception.CryptoKeyInvalidException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = "app.crypto")
public class CryptoProperties {

    private String aesSecretKey;

    public String getAesSecretKey() {
        return aesSecretKey;
    }

    @ConstructorBinding
    public CryptoProperties(String aesSecretKey) {
        if (aesSecretKey == null || aesSecretKey.length() != 32) {
            throw new CryptoKeyInvalidException();
        }
        this.aesSecretKey = aesSecretKey;
    }
}
