package ru.mkilord.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.mkilord.app.aspect.ControllerLogAspect;

@Configuration
public class AutoControllerLogConfig {
    @Bean
    public ControllerLogAspect controllerLogAspect() {
        return new ControllerLogAspect();
    }
}
