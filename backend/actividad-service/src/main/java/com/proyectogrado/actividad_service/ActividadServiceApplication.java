package com.proyectogrado.actividad_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Punto de entrada de actividad-service (puerto 8089): actividades de las
 * organizaciones e inscripciones. EnableScheduling activa los recordatorios
 * por SMS una hora antes de cada actividad.
 */
@SpringBootApplication
@EnableScheduling
public class ActividadServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ActividadServiceApplication.class, args);
    }
}
