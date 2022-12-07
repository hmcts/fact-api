package uk.gov.hmcts.dts.fact.util;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

public class MvcSecurityUtil {

    public MockMvc getMockMvcSecurityConfig(String role, WebApplicationContext context, String username) {
        SecurityContextHolder.getContext()
            .setAuthentication(new UsernamePasswordAuthenticationToken(
                username,
                "kupocatword",
                Collections.singletonList(
                    new SimpleGrantedAuthority(
                        role))
            ));
        return webAppContextSetup(context).apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }
}
