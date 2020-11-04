package uk.gov.hmcts.dts.fact.config.security;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

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
        List<String> roles = Arrays.asList(annotation.value());

        if (rolesProvider.getRoles().stream().anyMatch(role -> roles.contains(role))) {
            return joinPoint.proceed();
        } else {
            return status(HttpStatus.FORBIDDEN).body("Missing required roles: " + roles);
        }
    }

}
