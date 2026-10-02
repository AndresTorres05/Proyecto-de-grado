package com.proyectogrado.actividad_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Cliente HTTP hacia messaging-service. La URL sale de messaging.service.url.
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
