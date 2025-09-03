package backend.medsnap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MedsnapApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedsnapApplication.class, args);
    }

}
