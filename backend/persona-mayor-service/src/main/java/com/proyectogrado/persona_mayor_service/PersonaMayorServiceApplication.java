package com.proyectogrado.persona_mayor_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PersonaMayorServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersonaMayorServiceApplication.class, args);
    }
}
