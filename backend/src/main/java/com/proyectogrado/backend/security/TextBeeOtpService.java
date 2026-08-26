package com.proyectogrado.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TextBeeOtpService {

    @Value("${textbee.api-key}")
    private String apiKey;

    @Value("${textbee.device-id}")
    private String deviceId;

    private static final long MINUTOS_EXPIRACION = 10;

    private final SecureRandom random = new SecureRandom();
    private final Map<String, CodigoOtp> codigosPendientes = new ConcurrentHashMap<>();
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void enviarCodigo(String telefono) {
        String codigo = generarCodigo();

        try {
            String cuerpoJson = objectMapper.writeValueAsString(
                    Map.of(
                            "recipients", List.of(telefono),
                            "message", "Tu código de verificación es: " + codigo
                    )
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.textbee.dev/api/v1/gateway/devices/" + deviceId + "/send-sms"))
                    .header("Content-Type", "application/json")
                    .header("x-api-key", apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(cuerpoJson))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 300) {
                throw new RuntimeException("TextBee respondió " + response.statusCode() + ": " + response.body());
            }

            codigosPendientes.put(telefono, new CodigoOtp(codigo, Instant.now().plusSeconds(MINUTOS_EXPIRACION * 60)));

        } catch (Exception e) {
            throw new RuntimeException("No se pudo enviar el SMS: " + e.getMessage(), e);
        }
    }

    public void enviarMensaje(String telefono, String mensaje) {

        try {
            String cuerpoJson = objectMapper.writeValueAsString(
                    Map.of(
                            "recipients", List.of(telefono),
                            "message", mensaje
                    )
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(
                            "https://api.textbee.dev/api/v1/gateway/devices/"
                                    + deviceId
                                    + "/send-sms"
                    ))
                    .header("Content-Type", "application/json")
                    .header("x-api-key", apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(cuerpoJson))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() >= 300) {
                throw new RuntimeException(
                        "TextBee respondió "
                                + response.statusCode()
                                + ": "
                                + response.body()
                );
            }

        } catch (Exception e) {
            throw new RuntimeException(
                    "No se pudo enviar el mensaje: "
                            + e.getMessage(),
                    e
            );
        }
    }

    public boolean verificarCodigo(String telefono, String codigo) {
        CodigoOtp guardado = codigosPendientes.get(telefono);

        if (guardado == null) {
            return false;
        }

        if (Instant.now().isAfter(guardado.expiracion())) {
            codigosPendientes.remove(telefono);
            return false;
        }

        boolean coincide = guardado.codigo().equals(codigo);
        if (coincide) {
            codigosPendientes.remove(telefono);
        }

        return coincide;
    }

    private String generarCodigo() {
        int numero = 100000 + random.nextInt(900000);
        return String.valueOf(numero);
    }

    private record CodigoOtp(String codigo, Instant expiracion) {
    }
}