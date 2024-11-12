package com.trufanov;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.trufanov.entity")
public class BankingBackendApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(BankingBackendApiApplication.class);
    }
}
