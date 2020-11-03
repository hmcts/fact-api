package uk.gov.hmcts.dts.fact.config.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest()
@ContextConfiguration(classes = SecurityConfiguration.class)
class SecurityConfigurationTest {

    @Autowired
    ApplicationContext applicationContext;

    @Test
    void shouldAddRolesProviderBeanToApplicationContext() {
        assertThat(applicationContext.containsBean("rolesProvider"));
    }

    @Test
    void shouldReturnRolesProvider() {
        final RolesProvider rolesProvider = (RolesProvider) (applicationContext.getBean("rolesProvider"));
        assertThat(rolesProvider.getRoles() != null);
    }
}
