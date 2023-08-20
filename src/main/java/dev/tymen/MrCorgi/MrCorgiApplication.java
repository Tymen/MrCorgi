package dev.tymen.MrCorgi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MrCorgiApplication {
    public static void main(String[] args) {
        SpringApplication.run(MrCorgiApplication.class, args);
    }
}
