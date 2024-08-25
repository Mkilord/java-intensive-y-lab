package autoservice.config;

import autoservice.domen.model.enums.Role;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class AuditAspect {

    private static final Logger logger = LoggerFactory.getLogger(AuditAspect.class);

    @Pointcut("execution(* autoservice.adapter.service..*(..)) && !execution(* autoservice.adapter.service.impl..*(..))")
    public void serviceMethods() {
    }

    @Before("serviceMethods() && args(role, ..)")
    public void beforeMethod(JoinPoint joinPoint, Role role) {
        String methodName = joinPoint.getSignature().getName();
        String userRole = role != null ? role.toString() : "UNKNOWN";
        logger.info("User Role: {} - Invoking method: {} with args: {}", userRole, methodName, Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "serviceMethods()", returning = "result")
    public void afterMethod(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        String message = result != null ? "Success: " + result : "Success";
        logger.info("Method: {} - {}", methodName, message);
    }
}
