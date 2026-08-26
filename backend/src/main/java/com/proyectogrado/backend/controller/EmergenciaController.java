package com.proyectogrado.backend.controller;

import com.proyectogrado.backend.model.PersonaMayorAcompanante;
import com.proyectogrado.backend.repository.PersonaMayorAcompananteRepository;
import com.proyectogrado.backend.repository.UsuarioRepository;
import com.proyectogrado.backend.security.TextBeeOtpService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/persona-mayor/emergencia")
@CrossOrigin(origins = "http://localhost:4200")
public class EmergenciaController {

    private final PersonaMayorAcompananteRepository relacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final TextBeeOtpService textBeeOtpService;

    public EmergenciaController(
            PersonaMayorAcompananteRepository relacionRepository,
            UsuarioRepository usuarioRepository,
            TextBeeOtpService textBeeOtpService
    ) {
        this.relacionRepository = relacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.textBeeOtpService = textBeeOtpService;
    }

    @PostMapping("/prueba")
    public ResponseEntity<String> enviarEmergenciaDePrueba(
            @RequestParam Integer idPersonaMayor
    ) {

        List<PersonaMayorAcompanante> relaciones =
                relacionRepository.findById_IdPersonaMayor(
                        idPersonaMayor
                );

        if (relaciones.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    "La persona mayor no tiene acompañantes asociados"
            );
        }

        String nombrePersonaMayor = usuarioRepository
                .findById(idPersonaMayor)
                .map(usuario -> usuario.getNombreUsuario())
                .orElse("Una persona mayor");

        int enviados = 0;

        for (PersonaMayorAcompanante relacion : relaciones) {

            Integer idAcompanante =
                    relacion.getAcompanante().getIdUsuario();

            String telefono = usuarioRepository
                    .findById(idAcompanante)
                    .map(usuario -> usuario.getTelefono())
                    .orElse(null);

            if (telefono == null || telefono.isBlank()) {
                continue;
            }

            String mensaje =
                    "🚨 ALERTA DE EMERGENCIA: "
                    + nombrePersonaMayor
                    + " ha activado una alerta desde Gema. "
                    + "Por favor, verifica que se encuentre bien.";

            textBeeOtpService.enviarMensaje(
                    telefono,
                    mensaje
            );

            enviados++;
        }

        if (enviados == 0) {
            return ResponseEntity.badRequest().body(
                    "No se pudo enviar la alerta a ningún acompañante"
            );
        }

        return ResponseEntity.ok(
                "Alerta de emergencia enviada a "
                + enviados
                + " acompañante(s)"
        );
    }
}