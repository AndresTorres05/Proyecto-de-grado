package com.proyectogrado.actividad_service.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * Cliente de messaging-service para enviar los recordatorios de actividades por SMS.
 */
@Component
public class MessagingClient {

    private final RestClient restClient;

    public MessagingClient(RestClient messagingRestClient) {
        this.restClient = messagingRestClient;
    }

    /** Devuelve true si el SMS salió; ante cualquier error devuelve false sin lanzar excepción. */
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
