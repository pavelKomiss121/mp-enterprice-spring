/* @MENTEE_POWER (C)2026 */
package ru.mentee.calculator.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class TimingAspect {
    @Around("@annotation(ru.mentee.calculator.annotation.Timed)")
    public Object measureTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;

            String methodName = joinPoint.getSignature().getName();
            log.info("{} took {} ms", methodName, elapsedTime);

            return result;
        } catch (Throwable throwable) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            String methodName = joinPoint.getSignature().getName();
            log.info("{} упал с ошибкой за {} ms", methodName, elapsedTime);
            throw throwable;
        }
    }
}
