package uk.gov.hmcts.dts.fact.config.security;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RoleAspect.class)
class RoleAspectTest {

    @MockBean
    RolesProvider rolesProvider;

    @Autowired
    RoleAspect roleAspect;

    @Test
    public void testValidRole() throws Throwable {
        ProceedingJoinPoint join = mock(ProceedingJoinPoint.class);
        Role role = mock(Role.class);

        when(role.value()).thenReturn("fact-admin");
        when(rolesProvider.getRoles()).thenReturn(singletonList("fact-admin"));


        roleAspect.ensureRole(join, role);
        verify(join, atLeastOnce()).proceed();
    }
}
