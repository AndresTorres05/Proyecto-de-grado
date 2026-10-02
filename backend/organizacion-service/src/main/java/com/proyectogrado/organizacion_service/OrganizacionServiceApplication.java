package com.proyectogrado.organizacion_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de organizacion-service (puerto 8088): información de la
 * organización, sus personas mayores vinculadas y la analítica.
 */
@SpringBootApplication
public class OrganizacionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrganizacionServiceApplication.class, args);
    }
}
