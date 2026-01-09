/* @MENTEE_POWER (C)2025 */
package ru.mentee.banking.aspect;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import ru.mentee.banking.annotation.Auditable;
import ru.mentee.banking.domain.model.AuditEntry;
import ru.mentee.banking.service.AuditService;
import ru.mentee.banking.service.SecurityContext;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditService auditService;
    private final SecurityContext securityContext;

    @Around("@annotation(auditable)")
    public Object audit(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        String action = auditable.action();
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        if (action.isEmpty()) {
            action = className + "." + methodName;
        }

        AuditEntry.AuditEntryBuilder entryBuilder =
                AuditEntry.builder()
                        .id(UUID.randomUUID().toString())
                        .userId(securityContext.getCurrentUser())
                        .operation(action)
                        .timestamp(LocalDateTime.now());

        if (auditable.logArgs()) {
            entryBuilder.parameters(Arrays.toString(joinPoint.getArgs()));
        }

        try {
            Object result = joinPoint.proceed();

            entryBuilder.status("SUCCESS");
            if (auditable.logResult()) {
                entryBuilder.result(String.valueOf(result));
            }

            AuditEntry entry = entryBuilder.build();
            auditService.save(entry);

            log.debug("Audit entry saved: {}", entry.getId());
            return result;

        } catch (Exception e) {
            entryBuilder.status("FAILED").error(e.getMessage());

            AuditEntry entry = entryBuilder.build();
            auditService.save(entry);

            log.error("Audit entry saved for failed operation: {}", entry.getId());
            throw e;
        }
    }
}
