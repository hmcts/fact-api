package uk.gov.hmcts.dts.fact.config;

import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.filter.UrlHandlerFilter;

@Configuration
public class TrailingSlashConfiguration {

    @Bean
    public FilterRegistrationBean<Filter> trailingSlashHandlerFilter() {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(
            // Spring Boot 4  use strict path matching, so
            // "/courts" and "/courts/" are treated as different request paths.
            // During migration this caused endpoint and security matcher drift
            // (for example public search vs admin-protected routes) depending on
            // whether clients sent a trailing slash. Wrapping the request here
            // normalizes paths early so controller mappings and auth rules behave
            // consistently for both forms.
            UrlHandlerFilter
                .trailingSlashHandler("/**")
                .wrapRequest()
                .build()
        );
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }
}
