package com.proyectogrado.auth_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Cliente HTTP hacia messaging-backend.
 *
 * auth-backend NUNCA envia ni valida OTP por su cuenta: siempre le
 * pregunta a messaging-backend, que es el unico dueno de esa logica.
 */
@Configuration
public class MessagingClientConfig {

    @Bean
    public RestClient messagingRestClient(
            @Value("${messaging.service.url}") String messagingServiceUrl
    ) {
        return RestClient.builder()
                .baseUrl(messagingServiceUrl)
                .build();
    }
}
