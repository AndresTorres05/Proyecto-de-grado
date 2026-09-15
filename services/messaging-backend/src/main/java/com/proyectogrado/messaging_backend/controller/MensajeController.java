package com.proyectogrado.messaging_backend.controller;

import com.proyectogrado.messaging_backend.security.TextBeeOtpService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Endpoint generico de envio de SMS libre (no OTP).
 *
 * Lo usan otros servicios (personas-backend hoy, salud-backend a futuro
 * para recordatorios) que ya saben A QUIEN avisar y QUE decir, pero no
 * tienen ni deben tener credenciales de TextBee. messaging-backend sigue
 * siendo el UNICO que sabe enviar SMS.
 *
 * No lleva JwtAuth en el gateway: se llama servicio-a-servicio, nunca
 * directo desde el frontend.
 */
@RestController
@RequestMapping("/api/mensajes")
public class MensajeController {

    private final TextBeeOtpService textBeeOtpService;

    public MensajeController(TextBeeOtpService textBeeOtpService) {
        this.textBeeOtpService = textBeeOtpService;
    }

    @PostMapping("/enviar")
    public ResponseEntity<?> enviar(@RequestBody Map<String, String> request) {

        String telefono = request.get("telefono");
        String mensaje = request.get("mensaje");

        try {
            textBeeOtpService.enviarMensaje(telefono, mensaje);
            return ResponseEntity.ok(Map.of("success", true));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "No se pudo enviar el mensaje"));
        }
    }
}
