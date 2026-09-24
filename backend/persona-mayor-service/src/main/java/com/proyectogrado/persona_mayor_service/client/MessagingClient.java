package com.proyectogrado.persona_mayor_service.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class MessagingClient {

    private final RestClient restClient;

    public MessagingClient(RestClient messagingRestClient) {
        this.restClient = messagingRestClient;
    }

    /**
     * @return true si messaging-backend confirmo el envio.
     */
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
