package com.proyectogrado.messaging_backend.security;

import tools.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.SecureRandom;
import java.time.Duration;
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

    private final Map<String, CodigoOtp> codigosPendientes =
            new ConcurrentHashMap<>();

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String obtenerUrlTextBee() {
        return "https://api.textbee.dev/api/v1/gateway/devices/"
                + deviceId
                + "/send-sms";
    }

    /**
     * Genera y envía un código OTP al teléfono indicado.
     */
    public void enviarCodigo(String telefono) {

        validarTelefono(telefono);

        String codigo = generarCodigo();

        String mensaje = "Tu código de verificación de Vita+ es: "
                + codigo
                + ". Tiene una vigencia de "
                + MINUTOS_EXPIRACION
                + " minutos.";

        enviarSms(telefono, mensaje);

        Instant expiracion = Instant.now()
                .plusSeconds(MINUTOS_EXPIRACION * 60);

        codigosPendientes.put(
                normalizarTelefono(telefono),
                new CodigoOtp(codigo, expiracion)
        );
    }

    /**
     * Envía un mensaje SMS normal, por ejemplo una alerta de emergencia.
     */
    public void enviarMensaje(String telefono, String mensaje) {

        validarTelefono(telefono);

        if (mensaje == null || mensaje.isBlank()) {
            throw new IllegalArgumentException(
                    "El mensaje no puede estar vacío"
            );
        }

        enviarSms(telefono, mensaje);
    }

    /**
     * Verifica el código OTP enviado anteriormente.
     */
    public boolean verificarCodigo(String telefono, String codigo) {

        if (telefono == null
                || telefono.isBlank()
                || codigo == null
                || codigo.isBlank()) {

            return false;
        }

        String telefonoNormalizado = normalizarTelefono(telefono);

        CodigoOtp guardado =
                codigosPendientes.get(telefonoNormalizado);

        if (guardado == null) {
            return false;
        }

        if (Instant.now().isAfter(guardado.expiracion())) {
            codigosPendientes.remove(telefonoNormalizado);
            return false;
        }

        boolean coincide =
                guardado.codigo().equals(codigo.trim());

        if (coincide) {
            codigosPendientes.remove(telefonoNormalizado);
        }

        return coincide;
    }

    /**
     * Realiza el envío físico del SMS mediante TextBee.
     */
    private void enviarSms(String telefono, String mensaje) {

        try {

            String cuerpoJson = objectMapper.writeValueAsString(
                    Map.of(
                            "recipients", List.of(telefono),
                            "message", mensaje
                    )
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(obtenerUrlTextBee()))
                    .header("Content-Type", "application/json")
                    .header("x-api-key", apiKey)
                    .timeout(Duration.ofSeconds(20))
                    .POST(
                            HttpRequest.BodyPublishers.ofString(cuerpoJson)
                    )
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            System.out.println(
                    "TEXTBEE STATUS: " + response.statusCode()
            );

            System.out.println(
                    "TEXTBEE RESPONSE: " + response.body()
            );

            if (response.statusCode() >= 300) {

                throw new RuntimeException(
                        "TextBee respondió "
                                + response.statusCode()
                                + ": "
                                + response.body()
                );
            }

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new RuntimeException(
                    "El envío del SMS fue interrumpido",
                    e
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "No se pudo enviar el SMS mediante TextBee: "
                            + e.getMessage(),
                    e
            );
        }
    }

    /**
     * Genera un código numérico de 6 dígitos.
     */
    private String generarCodigo() {

        int numero = 100000 + random.nextInt(900000);

        return String.valueOf(numero);
    }

    /**
     * Valida que el teléfono tenga un valor válido.
     */
    private void validarTelefono(String telefono) {

        if (telefono == null || telefono.isBlank()) {

            throw new IllegalArgumentException(
                    "El teléfono es obligatorio"
            );
        }
    }

    /**
     * Normaliza el teléfono para evitar problemas al verificar el OTP.
     */
    private String normalizarTelefono(String telefono) {

        return telefono.trim().replaceAll("\\s+", "");
    }

    private record CodigoOtp(
            String codigo,
            Instant expiracion
    ) {
    }
}