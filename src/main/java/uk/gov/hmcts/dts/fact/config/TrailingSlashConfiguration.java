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
            UrlHandlerFilter
                .trailingSlashHandler("/**")
                .wrapRequest()
                .build()
        );
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }
}

