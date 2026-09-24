package com.proyectogrado.auth_backend.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * Encapsula la comunicacion con messaging-backend.
 *
 * auth-backend es el unico que emite JWT, pero no sabe nada de SMS/OTP:
 * le delega esa verificacion a messaging-backend y solo interpreta la
 * respuesta.
 */
@Component
public class MessagingClient {

    private final RestClient restClient;

    public MessagingClient(RestClient messagingRestClient) {
        this.restClient = messagingRestClient;
    }

    /**
     * Verifica un codigo OTP contra messaging-backend.
     *
     * @return true si el codigo es valido (y ya fue consumido alla).
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
            // 401 de messaging-backend (codigo invalido/expirado) tambien
            // cae aqui porque RestClient lanza excepcion en 4xx/5xx.
            return false;
        }
    }
}
