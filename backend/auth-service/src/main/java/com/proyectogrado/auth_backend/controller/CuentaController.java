package com.proyectogrado.auth_backend.controller;

import com.proyectogrado.auth_backend.security.JwtService;
import com.proyectogrado.auth_backend.service.CuentaService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Eliminación de la cuenta del usuario autenticado, para cualquier rol.
 * El id sale del token, así que cada usuario solo puede borrar la suya.
 */
@RestController
@RequestMapping("/api/auth/cuenta")
public class CuentaController {

    private final CuentaService cuentaService;
    private final JwtService jwtService;

    public CuentaController(CuentaService cuentaService, JwtService jwtService) {
        this.cuentaService = cuentaService;
        this.jwtService = jwtService;
    }

    /** Borra la cuenta y todo lo relacionado con ella (ver CuentaService). */
    @DeleteMapping
    public ResponseEntity<String> eliminarCuenta(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        String token = authorizationHeader.substring(7);
        Integer idUsuario = jwtService.extraerIdUsuario(token);

        try {
            cuentaService.eliminarCuenta(idUsuario);
        } catch (RuntimeException e) {
            System.err.println("Error al eliminar la cuenta " + idUsuario + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("No se pudo eliminar la cuenta");
        }

        return ResponseEntity.ok("Cuenta eliminada correctamente");
    }
}
