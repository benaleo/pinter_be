package com.kopibery.pos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication(scanBasePackages = "com.kopibery.pos")
@EnableWebMvc
@EntityScan(basePackages = "com.kopibery.pos.entity")
@EnableJpaRepositories(basePackages = "com.kopibery.pos.repository")
public class KopiberyBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(KopiberyBeApplication.class, args);
    }

}
