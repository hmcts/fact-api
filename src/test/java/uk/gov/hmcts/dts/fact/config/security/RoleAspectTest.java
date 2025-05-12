package uk.gov.hmcts.dts.fact.config.security;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RoleAspect.class)
class RoleAspectTest {

    @MockitoBean
    RolesProvider rolesProvider;
    @MockitoBean
    Role role;
    @MockitoBean
    ProceedingJoinPoint join;

    @Autowired
    RoleAspect roleAspect;

    @Test
    void testValidRole() throws Throwable {
        when(role.value()).thenReturn(new String[]{"fact-admin"});
        when(rolesProvider.getRoles()).thenReturn(singletonList("fact-admin"));

        roleAspect.ensureRole(join, role);
        verify(join, atLeastOnce()).proceed();
    }

    @Test
    void testInvalidRole() throws Throwable {
        when(role.value()).thenReturn(new String[]{"fact-admin"});
        when(rolesProvider.getRoles()).thenReturn(new ArrayList<>());

        roleAspect.ensureRole(join, role);
        verify(join, never()).proceed();
    }
}
