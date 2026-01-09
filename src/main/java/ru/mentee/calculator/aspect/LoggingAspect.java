package ru.mentee.calculator.aspect;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

  @Before("@annotation(ru.mentee.calculator.annotation.Loggable)")
  public void logBefore(final JoinPoint joinPoint) {
    String methodName = joinPoint.getSignature().getName();
    Object[] args = joinPoint.getArgs();
    log.info("Вызов метода: {} с аргументами: {}", methodName, java.util.Arrays.toString(args));
  }

  @AfterReturning(pointcut = "@annotation(ru.mentee.calculator.annotation.Loggable)", returning = "result")
  public void logAfterReturning(final JoinPoint joinPoint, final Object result) {
    String methodName = joinPoint.getSignature().getName();
    log.info("Метод {} вернул: {}", methodName, result);

  }
}
