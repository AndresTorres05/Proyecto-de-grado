package com.proyectogrado.acompanante_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de acompanante-service (puerto 8086): personas mayores
 * a cargo del acompañante y su seguimiento.
 */
@SpringBootApplication
public class AcompananteServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AcompananteServiceApplication.class, args);
    }
}
