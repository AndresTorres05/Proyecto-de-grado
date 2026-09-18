package com.proyectogrado.messaging_backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * messaging-backend no valida JWT ni conoce usuarios: quien decide si una
 * peticion puede llegar aqui es el api-gateway (o, mientras no exista,
 * quien llame directamente a este servicio en desarrollo).
 *
 * Sin esta clase, Spring Security activa su configuracion por defecto y
 * bloquea con 401 todos los endpoints, incluido /api/otp/**.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }
}
