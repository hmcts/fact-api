package uk.gov.hmcts.dts.fact;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.openfeign.EnableFeignClients;
import uk.gov.hmcts.dts.fact.mapit.MapitClient;

@SpringBootApplication
@EnableCircuitBreaker
@EnableFeignClients(basePackageClasses = MapitClient.class)
@SuppressWarnings("HideUtilityClassConstructor") // Spring needs a constructor, its not a utility class
public class Application {

    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
