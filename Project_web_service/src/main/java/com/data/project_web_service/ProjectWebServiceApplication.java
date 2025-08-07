package com.data.project_web_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ProjectWebServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProjectWebServiceApplication.class, args);
    }
}

