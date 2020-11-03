package uk.gov.hmcts.dts.fact.config.security;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.*;

class RoleAspectTest {

    @Test
    public void testValidRole() throws Throwable {
        ProceedingJoinPoint join = mock(ProceedingJoinPoint.class);
        RolesProvider rolesProvider = mock(RolesProvider.class);
        Role role = mock(Role.class);

        when(role.value()).thenReturn("fact-admin");
        when(rolesProvider.getRoles()).thenReturn(singletonList("fact-admin"));

        RoleAspect roleAspect = new RoleAspect(rolesProvider);

        roleAspect.ensureRole(join, role);
        verify(join, atLeastOnce()).proceed();
    }
}
