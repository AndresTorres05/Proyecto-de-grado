package com.proyectogrado.api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del API Gateway (puerto 8080). Es la única puerta de
 * entrada del frontend: valida el token y reenvía cada petición al servicio
 * que le corresponde según las rutas de application.yml.
 */
@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
