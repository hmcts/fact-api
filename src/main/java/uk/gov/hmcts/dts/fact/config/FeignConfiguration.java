package uk.gov.hmcts.dts.fact.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfiguration {

    @Value("${mapit.key}")
    private String key;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> template.header("X-Api-Key", key);
    }
}
