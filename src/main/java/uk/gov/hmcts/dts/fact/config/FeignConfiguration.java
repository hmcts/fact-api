package uk.gov.hmcts.dts.fact.config;

import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class FeignConfiguration {

    @Value("${mapit.key}")
    private String key;

    @Bean
    public RequestInterceptor requestInterceptor() {
        log.info("Api key is: " + key);
        return template -> template.header("X-Api-Key", key);
    }
}
