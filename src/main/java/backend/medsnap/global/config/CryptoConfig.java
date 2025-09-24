package backend.medsnap.global.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import backend.medsnap.global.crypto.CryptoProperties;

@Configuration
@EnableConfigurationProperties(CryptoProperties.class)
public class CryptoConfig {}
