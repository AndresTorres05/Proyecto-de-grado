package com.proyectogrado.auth_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Endpoint de prueba para comprobar que auth-service está en funcionamiento.
 */
@RestController
@RequestMapping("/api/auth")
public class HealthController {

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
                "servicio", "auth-backend",
                "estado", "activo"
        );
    }
}