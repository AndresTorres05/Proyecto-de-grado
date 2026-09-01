package com.proyectogrado.backend.controller;

import com.proyectogrado.backend.model.PersonaMayorAcompanante;
import com.proyectogrado.backend.repository.PersonaMayorAcompananteRepository;
import com.proyectogrado.backend.repository.UsuarioRepository;
import com.proyectogrado.backend.security.TextBeeOtpService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import com.proyectogrado.backend.model.PersonaMayorOrganizacion;
import com.proyectogrado.backend.repository.PersonaMayorOrganizacionRepository;

import java.util.List;

@RestController
@RequestMapping("/api/persona-mayor/emergencia")
@CrossOrigin(origins = "http://localhost:4200")
public class EmergenciaController {

    private final PersonaMayorAcompananteRepository relacionRepository;
    private final PersonaMayorOrganizacionRepository organizacionRelacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final TextBeeOtpService textBeeOtpService;
    

        public EmergenciaController(
                PersonaMayorAcompananteRepository relacionRepository,
                PersonaMayorOrganizacionRepository organizacionRelacionRepository,
                UsuarioRepository usuarioRepository,
                TextBeeOtpService textBeeOtpService
        ) {
                this.relacionRepository = relacionRepository;
                this.organizacionRelacionRepository = organizacionRelacionRepository;
                this.usuarioRepository = usuarioRepository;
                this.textBeeOtpService = textBeeOtpService;
        }

    @PostMapping
        public ResponseEntity<String> enviarEmergencia(
                Authentication authentication
        ) {

                String username = authentication.getName();

                Integer idPersonaMayor = usuarioRepository
                        .findByCorreo(username)
                        .or(() -> usuarioRepository.findByTelefono(username))
                        .map(usuario -> usuario.getIdUsuario())
                        .orElse(null);

                if (idPersonaMayor == null) {
                        return ResponseEntity.badRequest().body(
                                "No se pudo identificar al usuario autenticado"
                        );
                }

                List<PersonaMayorAcompanante> relaciones =
                        relacionRepository.findById_IdPersonaMayor(
                                idPersonaMayor
                        );

                String nombrePersonaMayor = usuarioRepository
                        .findById(idPersonaMayor)
                        .map(usuario -> usuario.getNombreUsuario())
                        .orElse("Una persona mayor");

                String mensaje =
                        "🚨 ALERTA DE EMERGENCIA: "
                        + nombrePersonaMayor
                        + " ha activado una alerta desde VITA+. "
                        + "Por favor, verifica que se encuentre bien.";

                int enviadosAcompanantes = 0;
                int enviadosOrganizaciones = 0;

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



                        textBeeOtpService.enviarMensaje(
                                telefono,
                                mensaje
                        );

                        enviadosAcompanantes++;
                }

                List<PersonaMayorOrganizacion> organizaciones =
                        organizacionRelacionRepository
                                .findById_IdPersonaMayor(idPersonaMayor);

                for (PersonaMayorOrganizacion relacion : organizaciones) {

                        String telefono = relacion.getOrganizacion().getTelefono();

                        if (telefono == null || telefono.isBlank()) {
                                continue;
                        }

                        textBeeOtpService.enviarMensaje(
                                telefono,
                                mensaje
                        );

                        enviadosOrganizaciones++;
                }

                int totalEnviados =
                        enviadosAcompanantes + enviadosOrganizaciones;

                if (totalEnviados == 0) {
                return ResponseEntity.badRequest().body(
                        "No se pudo enviar la alerta a ningún acompañante u organización"
                );
                }

                return ResponseEntity.ok(
                        "Alerta de emergencia enviada a "
                        + enviadosAcompanantes
                        + " acompañante(s) y "
                        + enviadosOrganizaciones
                        + " organización(es)"
                );
        }
}