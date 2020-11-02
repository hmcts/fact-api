package uk.gov.hmcts.dts.fact.config.security;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.springframework.http.ResponseEntity.status;

@Aspect
@Component
public class RoleAspect {

    @Autowired
    private RolesProvider rolesProvider;

    @Around("@annotation(annotation)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint, Role annotation) throws Throwable {
        String role = annotation.value();

        if (rolesProvider.getRoles().contains(role)) {
            return joinPoint.proceed();
        } else {
            return status(401).body("Missing required role: " + role);
        }
    }

}
