package com.proyectogrado.auth_backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
public class EmailValidationService {

    private static final Logger log = LoggerFactory.getLogger(EmailValidationService.class);

    private final String apiKey;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Constructor injection: si algo intenta "new EmailValidationService()"
    // sin pasar la clave, ya no compila. Y si Spring no resuelve la
    // propiedad, falla al arrancar la app en vez de fallar en silencio.
    public EmailValidationService(
            @Value("${hunter.api-key:}") String apiKey
    ) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "La propiedad 'hunter.api-key' no se resolvió. "
                            + "Revisa application.properties y el perfil activo."
            );
        }
        this.apiKey = apiKey;
        log.info("Hunter API key cargada, longitud={}, prefijo={}",
                apiKey.length(), apiKey.substring(0, Math.min(4, apiKey.length())));
    }

    // Misma regla que usa el registro: solo se acepta si Hunter
    // responde status "valid" (el correo existe y puede recibir).
    public boolean puedeRecibirCorreos(String correo) {
        return "valid".equalsIgnoreCase(
                validarCorreo(correo).path("data").path("status").asText()
        );
    }

    public JsonNode validarCorreo(String correo) {

        try {
            String url = "https://api.hunter.io/v2/email-verifier"
                    + "?email=" + java.net.URLEncoder.encode(
                            correo,
                            java.nio.charset.StandardCharsets.UTF_8
                    )
                    + "&api_key=" + apiKey;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(15))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            log.debug("HUNTER STATUS: {}", response.statusCode());
            log.debug("HUNTER RESPONSE: {}", response.body());

            if (response.statusCode() >= 300) {
                throw new RuntimeException(
                        "Hunter respondió " + response.statusCode() + ": " + response.body()
                );
            }

            return objectMapper.readTree(response.body());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("La validación del correo fue interrumpida", e);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo validar el correo con Hunter: " + e.getMessage(), e);
        }
    }
}