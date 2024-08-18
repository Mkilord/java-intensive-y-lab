package autoservice.adapter.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class PerformanceLoggingAspect {
    @Pointcut("execution(void jakarta.servlet.http.HttpServlet.doGet(..)) || execution(void jakarta.servlet.http.HttpServlet.doPost(..))")
    public void servletMethods() {}

    @Around("servletMethods()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        Object proceed = joinPoint.proceed();

        long elapsedTime = System.currentTimeMillis() - startTime;
        String methodName = joinPoint.getSignature().getName();
        System.out.println("Execution time for method " + methodName + " : " + elapsedTime + " ms");

        return proceed;
    }
}
