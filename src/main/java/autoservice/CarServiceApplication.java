package autoservice;

import autoservice.config.DatabaseConfig;
import autoservice.config.SecurityConfiguration;
import autoservice.config.WebConfig;
import autoservice.config.YamlConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class CarServiceApplication {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(
                SecurityConfiguration.class,
                YamlConfig.class,
                WebConfig.class,
                DatabaseConfig.class
        );
    }
}
