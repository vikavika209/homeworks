package medical_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MainMedical {
    public static void main(String[] args) {
        SpringApplication.run(MainMedical.class, args);
    }
}
