package ru.mentee.calculator.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ErrorHandlingAspect {
  @AfterThrowing(
      pointcut = "execution(* ru.mentee.calculator.service.*.*(..))",
      throwing = "exception"
  )
  public void afterThrowing(JoinPoint joinPoint, Exception exception) {
    String methodName = joinPoint.getSignature().getName();
    log.error("Ошибка в методе {}: {}", methodName, exception.getMessage());
  }
}
