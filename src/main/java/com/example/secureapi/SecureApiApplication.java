package com.example.secureapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.example.secureapi.model")
public class SecureApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(SecureApiApplication.class, args);
    }
} 