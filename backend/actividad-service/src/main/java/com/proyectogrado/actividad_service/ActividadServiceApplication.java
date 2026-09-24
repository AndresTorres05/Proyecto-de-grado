package com.proyectogrado.actividad_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ActividadServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ActividadServiceApplication.class, args);
    }
}
