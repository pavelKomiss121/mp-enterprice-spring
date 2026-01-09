/* @MENTEE_POWER (C)2025 */
package ru.mentee.banking.aspect;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import ru.mentee.banking.annotation.RequiresRole;
import ru.mentee.banking.exception.AccessDeniedException;
import ru.mentee.banking.service.SecurityContext;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class SecurityAspect {

    private final SecurityContext securityContext;

    @Before("@annotation(requiresRole)")
    public void checkRole(JoinPoint joinPoint, RequiresRole requiresRole) {
        String[] requiredRoles = requiresRole.value();
        String methodName = joinPoint.getSignature().getName();

        log.debug(
                "Checking roles for method: {}. Required: {}",
                methodName,
                Arrays.toString(requiredRoles));

        boolean hasRole =
                Arrays.stream(requiredRoles).anyMatch(role -> securityContext.hasRole(role));

        if (!hasRole) {
            String message =
                    String.format(
                            "Access denied for method %s. Required roles: %s. Current user: %s",
                            methodName,
                            Arrays.toString(requiredRoles),
                            securityContext.getCurrentUser());
            log.warn(message);
            throw new AccessDeniedException(message);
        }

        log.debug(
                "Access granted for method: {} with roles: {}",
                methodName,
                Arrays.toString(requiredRoles));
    }
}
