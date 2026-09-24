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


@RestController
@RequestMapping("/api/auth")
//@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
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
}