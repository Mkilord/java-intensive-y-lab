package ru.mkilord.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import ru.mkilord.app.aspect.AuditLoggingAspect;

@Configuration
@EnableAspectJAutoProxy
public class AuditLoggingConfig {
    @Bean
    public AuditLoggingAspect auditLoggingAspect() {
        return new AuditLoggingAspect();
    }
}
