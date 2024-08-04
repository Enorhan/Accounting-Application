package com.cydeo.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @AfterThrowing(pointcut = "execution(* com.cydeo..*(..))", throwing = "ex")
    public void logRuntimeException(JoinPoint joinPoint, RuntimeException ex) {
        String methodName = joinPoint.getSignature().getName();
        String exceptionName = ex.getClass().getSimpleName();
        String exceptionMessage = ex.getMessage();

        log.error("Exception in method '{}': {} - {}", methodName, exceptionName, exceptionMessage);
    }
}
