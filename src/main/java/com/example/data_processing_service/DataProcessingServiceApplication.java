package com.example.data_processing_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
@Configuration
public class DataProcessingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataProcessingServiceApplication.class, args);
    }

}
