package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "com.example.demo",
        "com.safewords.api"
})
@EnableJpaRepositories(basePackages = {
        "com.safewords.api.modelo.repositorio"
})
@EntityScan(basePackages = {
        "com.safewords.api.modelo"
})
public class SafewordsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SafewordsApplication.class, args);
    }
}
