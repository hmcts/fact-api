package uk.gov.hmcts.dts.fact.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureMapsFeignConfiguration {

    @Value("${azure.maps.client-id}")
    private String clientId;

    @Value("${azure.maps.subscription-key}")
    private String subscriptionKey;

    @Bean
    public RequestInterceptor azureMapsRequestInterceptor() {
        return template -> {
            template.header("x-ms-client-id", clientId);
            template.query("subscription-key", subscriptionKey);
        };
    }
}
