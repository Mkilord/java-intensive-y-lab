package autoservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.mkilord.app.EnableAuditLogging;

@SpringBootApplication(scanBasePackages = {"ru.mkilord","autoservice"})
@EnableAuditLogging
public class CarServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CarServiceApplication.class, args);
    }
}
