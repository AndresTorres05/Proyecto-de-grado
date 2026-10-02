package com.proyectogrado.messaging_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de messaging-service (puerto 8082): envío de SMS con
 * TextBee, códigos OTP y notificaciones del panel.
 */
@SpringBootApplication
public class MessagingBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MessagingBackendApplication.class, args);
	}

}
