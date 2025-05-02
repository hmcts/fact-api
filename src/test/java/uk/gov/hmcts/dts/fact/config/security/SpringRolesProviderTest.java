package uk.gov.hmcts.dts.fact.config.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringRolesProvider.class)
class SpringRolesProviderTest {

    @Autowired
    SpringRolesProvider springRolesProvider;
    @MockitoBean
    SecurityContext securityContext;
    @MockitoBean
    Authentication authentication;
    @MockitoBean
    Jwt jwt;

    List<String> roles;

    @BeforeEach
    void setUp() {
        roles = asList("role1", "role2");
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(jwt);
        when(jwt.getClaimAsStringList(any())).thenReturn(roles);
    }

    @Test
    void shouldRetunEmptyListWhenAuthenticationNull() {
        when(securityContext.getAuthentication()).thenReturn(null);
        List<String> roles = springRolesProvider.getRoles();
        assertThat(roles).isEmpty();
    }

    @Test
    void shouldRetunEmptyListWhenCredentialsNull() {
        when(authentication.getCredentials()).thenReturn(null);
        List<String> roles = springRolesProvider.getRoles();
        assertThat(roles).isEmpty();
    }

    @Test
    void shouldRetunEmptyListWhenRolesNull() {
        when(jwt.getClaimAsStringList(any())).thenReturn(null);
        List<String> roles = springRolesProvider.getRoles();
        assertThat(roles).isEmpty();
    }
}
