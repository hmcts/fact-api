package uk.gov.hmcts.dts.fact.config;

import feign.RequestTemplate;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@WebMvcTest(FeignConfiguration.class)
class FeignConfigurationTest {

    @Autowired
    ApplicationContext applicationContext;

    @Test
    void shouldAddFeignConfigurationBeanToApplicationContext() {
        assertThat(applicationContext.containsBean("feignConfiguration"));
    }

    @Test
    void shouldSupplyRequestInterceptor() {
        final FeignConfiguration feignConfiguration = (FeignConfiguration) (applicationContext.getBean(
            "feignConfiguration"));
        assertThat(feignConfiguration.requestInterceptor() != null);
    }


    @Test
    @Disabled
    void shouldSetRequestHeaderWithMapitKey() {
        final FeignConfiguration feignConfiguration = (FeignConfiguration) (applicationContext.getBean(
            "feignConfiguration"));

        final RequestTemplate requestTemplate = new RequestTemplate();
        feignConfiguration.requestInterceptor().apply(requestTemplate);
        assertEquals(requestTemplate.headers().size(), 1);
        assertThat(requestTemplate.headers().get("X-Api-Key").contains("TODO"));
    }
}
