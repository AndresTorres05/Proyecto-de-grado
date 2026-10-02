package com.proyectogrado.analitica_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Servicio de analítica: reportes de la organización (actividades, salud y
 * perfil de la población) y su descarga en PDF. Solo lee la base de datos
 * compartida.
 */
@SpringBootApplication
public class AnaliticaServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnaliticaServiceApplication.class, args);
    }
}
