package uk.gov.hmcts.dts.fact.config.security;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RoleAspect.class)
class RoleAspectTest {

    @MockBean
    RolesProvider rolesProvider;
    @MockBean
    Role role;
    @MockBean
    ProceedingJoinPoint join;

    @Autowired
    RoleAspect roleAspect;

    @Test
    public void testValidRole() throws Throwable {
        when(role.value()).thenReturn(new String[]{"fact-admin"});
        when(rolesProvider.getRoles()).thenReturn(singletonList("fact-admin"));

        roleAspect.ensureRole(join, role);
        verify(join, atLeastOnce()).proceed();
    }

    @Test
    public void testInvalidRole() throws Throwable {
        when(role.value()).thenReturn(new String[]{"fact-admin"});
        when(rolesProvider.getRoles()).thenReturn(new ArrayList<>());

        roleAspect.ensureRole(join, role);
        verify(join, never()).proceed();
    }
}
