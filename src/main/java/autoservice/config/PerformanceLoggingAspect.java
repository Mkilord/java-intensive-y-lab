package autoservice.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PerformanceLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceLoggingAspect.class);

    @Pointcut("within(autoservice.adapter.controller..*)")
    public void controllerMethods() {}

    @Around("controllerMethods()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        Object proceed = joinPoint.proceed();

        long elapsedTime = System.currentTimeMillis() - startTime;
        String methodName = joinPoint.getSignature().toShortString();
        logger.info("Execution time for method {}: {} ms", methodName, elapsedTime);

        return proceed;
    }
}
