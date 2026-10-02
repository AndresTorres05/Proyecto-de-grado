package com.proyectogrado.auth_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de auth-service (puerto 8081): registro, inicio de
 * sesión, emisión de tokens y datos de la cuenta.
 */
@SpringBootApplication
public class AuthBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthBackendApplication.class, args);
	}

}
