package backend.medsnap.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class TimeConfig {

    /**
     * 시스템 전체에서 사용할 일관된 시간 소스
     */
    @Bean
    public Clock systemClock() {
        return Clock.system(ZoneId.of("Asia/Seoul"));
    }
}
