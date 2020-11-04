package uk.gov.hmcts.dts.fact.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

public class SpringRolesProvider implements RolesProvider {

    private static final String ROLES = "roles";

    @Override
    public List<String> getRoles() {
        return Optional
            .of(SecurityContextHolder.getContext())
            .map(SecurityContext::getAuthentication)
            .map(Authentication::getCredentials)
            .map(obj -> (Jwt) obj)
            .map(jwt -> jwt.getClaimAsStringList(ROLES))
            .orElse(emptyList());
    }

}
