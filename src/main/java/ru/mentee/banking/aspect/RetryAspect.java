/* @MENTEE_POWER (C)2025 */
package ru.mentee.banking.aspect;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import ru.mentee.banking.annotation.Retryable;

@Slf4j
@Aspect
@Component
public class RetryAspect {

    @Around("@annotation(retryable)")
    public Object retry(ProceedingJoinPoint joinPoint, Retryable retryable) throws Throwable {
        int maxAttempts = retryable.maxAttempts();
        long delay = retryable.delay();
        Class<? extends Exception>[] retryOn = retryable.retryOn();

        String methodName = joinPoint.getSignature().getName();
        Exception lastException = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                log.debug("Attempt {} of {} for method {}", attempt, maxAttempts, methodName);

                Object result = joinPoint.proceed();
                if (attempt > 1) {
                    log.info("Method {} succeeded on attempt {}", methodName, attempt);
                }
                return result;

            } catch (Exception e) {
                lastException = e;

                boolean shouldRetry =
                        Arrays.stream(retryOn)
                                .anyMatch(exceptionClass -> exceptionClass.isInstance(e));

                if (!shouldRetry) {
                    log.warn(
                            "Exception {} is not retryable for method {}",
                            e.getClass().getSimpleName(),
                            methodName);
                    throw e;
                }

                if (attempt == maxAttempts) {
                    log.error("Method {} failed after {} attempts", methodName, maxAttempts);
                    throw e;
                }

                log.warn(
                        "Attempt {} failed for method {}, retrying in {} ms. Error: {}",
                        attempt,
                        methodName,
                        delay,
                        e.getMessage());

                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry interrupted", ie);
                }
            }
        }

        throw lastException;
    }
}
