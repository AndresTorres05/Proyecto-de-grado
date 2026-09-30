package com.proyectogrado.messaging_backend.security;

import tools.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Service
public class TextBeeOtpService {

    @Value("${textbee.api-key}")
    private String apiKey;

    @Value("${textbee.device-id}")
    private String deviceId;

    private static final long MINUTOS_EXPIRACION = 10;

    // Tras estos intentos fallidos el código se invalida y hay que pedir
    // otro; sin este límite un código de 6 dígitos se adivina por fuerza bruta.
    private static final int MAX_INTENTOS = 5;

    // Igual al contador de reenvío del login en el frontend.
    private static final long SEGUNDOS_ENTRE_ENVIOS = 30;

    private final SecureRandom random = new SecureRandom();

    // Los códigos nunca se guardan en claro: solo su HMAC-SHA256 con esta
    // clave, que se genera al arrancar y no sale de la memoria. Como los
    // códigos pendientes también viven solo en memoria, al reiniciar el
    // servicio ambos se pierden a la vez.
    private final SecretKeySpec claveHmac = generarClaveHmac();

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
     * Genera y envía un código OTP al celular indicado.
     */
    public void enviarCodigo(String celular) {

        validarCelular(celular);

        String celularNormalizado = normalizarCelular(celular);

        CodigoOtp anterior = codigosPendientes.get(celularNormalizado);

        if (anterior != null
                && Instant.now().isBefore(
                        anterior.enviado().plusSeconds(SEGUNDOS_ENTRE_ENVIOS))) {

            throw new IllegalStateException(
                    "Espera unos segundos antes de pedir otro código"
            );
        }

        String codigo = generarCodigo();

        String mensaje = "Tu código de verificación de Vita+ es: "
                + codigo
                + ". Tiene una vigencia de "
                + MINUTOS_EXPIRACION
                + " minutos.";

        Instant ahora = Instant.now();

        codigosPendientes.put(
                celularNormalizado,
                new CodigoOtp(
                        hmac(celularNormalizado, codigo),
                        ahora,
                        ahora.plusSeconds(MINUTOS_EXPIRACION * 60),
                        0
                )
        );

        enviarSms(celular, mensaje);
    }

    /**
     * Envía un mensaje SMS normal, por ejemplo una alerta de emergencia.
     */
    public void enviarMensaje(String celular, String mensaje) {

        validarCelular(celular);

        if (mensaje == null || mensaje.isBlank()) {
            throw new IllegalArgumentException(
                    "El mensaje no puede estar vacío"
            );
        }

        enviarSms(celular, mensaje);
    }

    /**
     * Verifica el código OTP enviado anteriormente.
     */
    public boolean verificarCodigo(String celular, String codigo) {

        if (celular == null
                || celular.isBlank()
                || codigo == null
                || codigo.isBlank()) {

            return false;
        }

        String celularNormalizado = normalizarCelular(celular);

        CodigoOtp guardado =
                codigosPendientes.get(celularNormalizado);

        if (guardado == null) {
            return false;
        }

        if (Instant.now().isAfter(guardado.expiracion())) {
            codigosPendientes.remove(celularNormalizado);
            return false;
        }

        boolean coincide = MessageDigest.isEqual(
                guardado.hash(),
                hmac(celularNormalizado, codigo.trim())
        );

        if (coincide) {
            codigosPendientes.remove(celularNormalizado);
            return true;
        }

        // Solo se cuenta el intento si nadie cambió el código mientras
        // tanto (p. ej. un reenvío); tras el último fallo se descarta.
        if (guardado.intentosFallidos() + 1 >= MAX_INTENTOS) {
            codigosPendientes.remove(celularNormalizado, guardado);
        } else {
            codigosPendientes.replace(
                    celularNormalizado,
                    guardado,
                    guardado.conIntentoFallido()
            );
        }

        return false;
    }

    /**
     * Realiza el envío físico del SMS mediante TextBee.
     */
    private void enviarSms(String celular, String mensaje) {

        try {

            String cuerpoJson = objectMapper.writeValueAsString(
                    Map.of(
                            "recipients", List.of(celular),
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

            // No se imprime el cuerpo de la respuesta: TextBee puede
            // devolver el texto del SMS, que incluye el código OTP.
            System.out.println(
                    "TEXTBEE STATUS: " + response.statusCode()
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

    private static SecretKeySpec generarClaveHmac() {

        byte[] clave = new byte[32];
        new SecureRandom().nextBytes(clave);

        return new SecretKeySpec(clave, "HmacSHA256");
    }

    /**
     * HMAC del código ligado al celular: el mismo código enviado a otro
     * número produce un hash distinto.
     */
    private byte[] hmac(String celularNormalizado, String codigo) {

        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(claveHmac);

            return mac.doFinal(
                    (celularNormalizado + ":" + codigo)
                            .getBytes(StandardCharsets.UTF_8)
            );

        } catch (Exception e) {
            throw new IllegalStateException("No se pudo calcular el HMAC del código", e);
        }
    }

    /**
     * Valida que el celular tenga un valor válido.
     */
    private void validarCelular(String celular) {

        if (celular == null || celular.isBlank()) {

            throw new IllegalArgumentException(
                    "El celular es obligatorio"
            );
        }
    }

    /**
     * Normaliza el celular para evitar problemas al verificar el OTP.
     */
    private String normalizarCelular(String celular) {

        return celular.trim().replaceAll("\\s+", "");
    }

    private record CodigoOtp(
            byte[] hash,
            Instant enviado,
            Instant expiracion,
            int intentosFallidos
    ) {
        CodigoOtp conIntentoFallido() {
            return new CodigoOtp(hash, enviado, expiracion, intentosFallidos + 1);
        }
    }
}