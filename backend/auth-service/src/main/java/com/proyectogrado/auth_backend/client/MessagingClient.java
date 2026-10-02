package com.proyectogrado.auth_backend.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * Cliente de messaging-service. auth-service emite los tokens, pero no
 * envía ni verifica códigos OTP: le pregunta a messaging-service y solo
 * interpreta la respuesta.
 */
@Component
public class MessagingClient {

    private final RestClient restClient;

    public MessagingClient(RestClient messagingRestClient) {
        this.restClient = messagingRestClient;
    }

    /**
     * Pregunta a messaging-service si el código es válido para ese celular.
     * Si lo es, allá queda usado y no sirve una segunda vez. Ante cualquier
     * error de comunicación devuelve false.
     */
    public boolean verificarOtp(String celular, String codigo) {

        try {
            Map<?, ?> respuesta = restClient.post()
                    .uri("/api/otp/verify")
                    .body(Map.of(
                            "phoneNumber", celular,
                            "code", codigo
                    ))
                    .retrieve()
                    .body(Map.class);

            return respuesta != null
                    && Boolean.TRUE.equals(respuesta.get("success"));

     } catch (Exception e) {
    System.out.println("ERROR VERIFICANDO OTP: " + e.getMessage());
    e.printStackTrace();
    return false;
}
    }
}
