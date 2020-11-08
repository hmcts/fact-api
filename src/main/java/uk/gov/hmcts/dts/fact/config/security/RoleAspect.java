package uk.gov.hmcts.dts.fact.config.security;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Arrays.asList;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.ResponseEntity.status;

@Aspect
@Component
public class RoleAspect {

    @Autowired
    private RolesProvider rolesProvider;

    public RoleAspect() {
        // stop pmd complaining
    }

    public RoleAspect(RolesProvider rolesProvider) {
        this.rolesProvider = rolesProvider;
    }

    @Around("@annotation(annotation)")
    public Object ensureRole(ProceedingJoinPoint joinPoint, Role annotation) throws Throwable {
        List<String> roles = asList(annotation.value());

        if (rolesProvider.getRoles().stream().anyMatch(roles::contains)) {
            return joinPoint.proceed();
        } else {
            return status(FORBIDDEN).build();
        }
    }

}
