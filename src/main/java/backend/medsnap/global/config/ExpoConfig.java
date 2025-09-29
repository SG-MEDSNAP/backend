package backend.medsnap.global.config;

import com.niamedtech.expo.exposerversdk.ExpoPushNotificationClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExpoConfig {

    @Bean
    public CloseableHttpClient expoHttpClient() {
        return HttpClients.createDefault();
    }

    @Bean
    public ExpoPushNotificationClient expoClient(CloseableHttpClient httpClient) {

        return ExpoPushNotificationClient.builder()
                .setHttpClient(httpClient)
                .build();
    }
}
