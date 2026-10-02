package com.proyectogrado.persona_mayor_service.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * Cliente de messaging-service para enviar las alertas de emergencia por SMS.
 */
@Component
public class MessagingClient {

    private final RestClient restClient;

    public MessagingClient(RestClient messagingRestClient) {
        this.restClient = messagingRestClient;
    }

    /** Devuelve true si messaging-service confirmó el envío; ante cualquier error, false. */
    public boolean enviarMensaje(String celular, String mensaje) {

        try {
            Map<?, ?> respuesta = restClient.post()
                    .uri("/api/mensajes/enviar")
                    .body(Map.of("celular", celular, "mensaje", mensaje))
                    .retrieve()
                    .body(Map.class);

            return respuesta != null && Boolean.TRUE.equals(respuesta.get("success"));

        } catch (Exception e) {
            return false;
        }
    }
}
