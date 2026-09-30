package com.proyectogrado.messaging_backend.controller;

import com.proyectogrado.messaging_backend.security.TextBeeOtpService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/otp")
//@CrossOrigin(origins = "http://localhost:4200")
public class OtpController {

    private final TextBeeOtpService textBeeOtpService;

    public OtpController(TextBeeOtpService textBeeOtpService) {
        this.textBeeOtpService = textBeeOtpService;
    }

    /**
     * Envía un código OTP al número de celular indicado.
     *
     * Ejemplo de petición:
     *
     * POST /api/otp/send
     *
     * Body:
     * {
     *   "phoneNumber": "+573001234567"
     * }
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendOtp(
            @RequestBody Map<String, String> request
    ) {
        try {
            String phoneNumber = request.get("phoneNumber");

            if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body(Map.of(
                                "success", false,
                                "message", "El número de celular es obligatorio"
                        ));
            }

            textBeeOtpService.enviarCodigo(phoneNumber);

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message", "Código OTP enviado correctamente"
                    )
            );

        } catch (IllegalStateException exception) {
            return ResponseEntity
                    .status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of(
                            "success", false,
                            "message", exception.getMessage()
                    ));

        } catch (Exception exception) {
            System.out.println("ERROR ENVIANDO OTP: " + exception.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "No fue posible enviar el código OTP"
                    ));
        }
    }

    /**
     * Verifica un código OTP. Solo lo llama auth-backend directamente
     * (localhost:8082); el api-gateway NO lo expone al público.
     *
     * Ejemplo de petición:
     *
     * POST /api/otp/verify
     *
     * Body:
     * {
     *   "phoneNumber": "+573001234567",
     *   "code": "123456"
     * }
     */
    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(
            @RequestBody Map<String, String> request
    ) {
        try {
            String phoneNumber = request.get("phoneNumber");
            String code = request.get("code");

            if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body(Map.of(
                                "success", false,
                                "message", "El número de celular es obligatorio"
                        ));
            }

            if (code == null || code.trim().isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body(Map.of(
                                "success", false,
                                "message", "El código OTP es obligatorio"
                        ));
            }

            boolean verified = textBeeOtpService.verificarCodigo(
                    phoneNumber,
                    code
            );

            if (verified) {
                return ResponseEntity.ok(
                        Map.of(
                                "success", true,
                                "message", "Código OTP verificado correctamente"
                        )
                );
            }

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "success", false,
                            "message", "El código OTP es incorrecto o ha expirado"
                    ));

        } catch (Exception exception) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "No fue posible verificar el código OTP"
                    ));
        }
    }
}