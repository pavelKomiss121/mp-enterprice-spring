/* @MENTEE_POWER (C)2025 */
package ru.mentee.banking.aspect;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * ValidationAspect - дополнительная валидация через AOP.
 *
 * Примечание: Spring автоматически валидирует @RequestBody с @Valid,
 * но этот аспект может использоваться для дополнительной валидации
 * или логирования валидации.
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ValidationAspect {

    private final Validator validator;

    /**
     * Дополнительная валидация для методов контроллеров.
     * Spring уже валидирует @RequestBody с @Valid, но можно добавить
     * дополнительную логику валидации здесь.
     */
    @Before("@within(org.springframework.web.bind.annotation.RestController)")
    public void validateControllerMethod(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        for (Object arg : args) {
            if (arg != null) {
                Set<ConstraintViolation<Object>> violations = validator.validate(arg);

                if (!violations.isEmpty()) {
                    String errors =
                            violations.stream()
                                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                                    .collect(Collectors.joining(", "));

                    log.debug(
                            "Additional validation for {}: {}",
                            joinPoint.getSignature().getName(),
                            errors);
                    // Не выбрасываем исключение, т.к. Spring уже валидирует через @Valid
                }
            }
        }
    }
}
