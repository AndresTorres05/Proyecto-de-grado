package com.proyectogrado.auth_backend.controller;

import com.proyectogrado.auth_backend.dto.LoginOtpRequest;
import com.proyectogrado.auth_backend.dto.LoginRequest;
import com.proyectogrado.auth_backend.dto.LoginResponse;
import com.proyectogrado.auth_backend.dto.RegistroRequest;
import com.proyectogrado.auth_backend.service.AuthService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.proyectogrado.auth_backend.dto.RestablecerContrasenaRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.proyectogrado.auth_backend.service.EmailValidationService;


@RestController
@RequestMapping("/api/auth")
//@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final AuthService authService;

    private final EmailValidationService emailValidationService;

    public AuthController(AuthService authService, EmailValidationService emailValidationService) {
        this.authService = authService;
        this.emailValidationService = emailValidationService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request
    ) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            LoginResponse response = new LoginResponse(
                    null,
                    null,
                    e.getMessage(),
                    null,
                    null
            );

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }
    }

    @PostMapping("/login-otp")
    public ResponseEntity<LoginResponse> loginOtp(
            @RequestBody LoginOtpRequest request
    ) {
        try {
            LoginResponse response = authService.loginConOtp(request);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            LoginResponse response = new LoginResponse(
                    null,
                    null,
                    e.getMessage(),
                    null,
                    null
            );

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }
    }

    @PostMapping("/registro")
    public ResponseEntity<LoginResponse> registro(
            @RequestBody RegistroRequest request
    ) {
        try {
            LoginResponse response = authService.registrar(request);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);

        } catch (RuntimeException e) {
            LoginResponse response = new LoginResponse(
                    null,
                    null,
                    e.getMessage(),
                    null,
                    null
            );

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }
    }

    @GetMapping("/celular-existe")
public ResponseEntity<Boolean> celularExiste(
        @RequestParam String celular
) {
    return ResponseEntity.ok(
            authService.existeCelular(celular)
    );
}

// =========================================================
// RESTABLECER CONTRASEÑA
// =========================================================

@PostMapping("/restablecer-contrasena")
public ResponseEntity<LoginResponse> restablecerContrasena(
        @RequestBody RestablecerContrasenaRequest request
) {

    try {

        authService.restablecerContrasena(request);

        return ResponseEntity.ok(
                new LoginResponse(
                        null,
                        null,
                        "Contraseña actualizada correctamente",
                        null,
                        null
                )
        );

    } catch (RuntimeException e) {

        return ResponseEntity
                .badRequest()
                .body(
                        new LoginResponse(
                                null,
                                null,
                                e.getMessage(),
                                null,
                                null
                        )
                );
    }
}

@GetMapping("/validar-correo")
public ResponseEntity<?> validarCorreo(
        @RequestParam String correo
) {
    try {
        JsonNode resultado =
                emailValidationService.validarCorreo(correo);

        return ResponseEntity
                .ok()
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(resultado.toString());

    } catch (RuntimeException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(
                        java.util.Map.of(
                                "mensaje",
                                e.getMessage()
                        )
                );
    }
}

}