package autoservice.adapter.config;

import autoservice.model.Role;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import java.util.Arrays;

@Aspect
public class AuditAspect {

    @Pointcut("execution(* autoservice.adapter.service..*(..)) && !execution(* autoservice.adapter.service.impl..*(..))")
    public void serviceMethods() {
    }

    @Before("serviceMethods() && args(role, ..)")
    public void beforeMethod(JoinPoint joinPoint, Role role) {
        String methodName = joinPoint.getSignature().getName();
        String userRole = role != null ? role.toString() : "UNKNOWN";
        System.out.println("User Role: " + userRole + " - Invoking method: " + methodName + " with args: " + Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "serviceMethods()", returning = "result")
    public void afterMethod(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        String message = result != null ? "Success: " + result : "Success";
        System.out.println("Method: " + methodName + " - " + message);
    }
}
