package com.proyectogrado.salud_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Punto de entrada de salud-service (puerto 8084): medicamentos, signos
 * vitales, citas médicas y recordatorios. EnableScheduling activa las
 * tareas que revisan cada minuto los recordatorios de medicamentos y de
 * citas médicas.
 */
@SpringBootApplication
@EnableScheduling
public class SaludBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SaludBackendApplication.class, args);
    }
}
