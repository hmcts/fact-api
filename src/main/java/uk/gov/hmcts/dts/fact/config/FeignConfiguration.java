package uk.gov.hmcts.dts.fact.config;

import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class FeignConfiguration {

    @Value("${mapit.key}")
    private String key;

    @Bean
    public RequestInterceptor requestInterceptor() {

        log.info("*******key is " + key);
        if (StringUtils.isNotBlank(key) && key.length() == 39) {
            log.info("******mapit key present!!!!!********");
        }
        return template -> template.header("X-Api-Key", key);
    }
}
