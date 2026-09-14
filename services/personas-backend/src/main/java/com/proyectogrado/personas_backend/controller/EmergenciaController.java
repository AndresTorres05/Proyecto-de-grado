package com.proyectogrado.personas_backend.controller;

import com.proyectogrado.personas_backend.client.MessagingClient;
import com.proyectogrado.personas_backend.model.PersonaMayorAcompanante;
import com.proyectogrado.personas_backend.model.PersonaMayorOrganizacion;
import com.proyectogrado.personas_backend.model.UsuarioLookup;
import com.proyectogrado.personas_backend.repository.PersonaMayorAcompananteRepository;
import com.proyectogrado.personas_backend.repository.PersonaMayorOrganizacionRepository;
import com.proyectogrado.personas_backend.repository.UsuarioLookupRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Reconstruida aqui (ya no en messaging-backend): personas-backend es
 * quien conoce las relaciones persona mayor <-> acompanante/organizacion,
 * y le delega a messaging-backend SOLO el envio del SMS.
 */
@RestController
@RequestMapping("/api/persona-mayor/emergencia")
public class EmergenciaController {

    private final PersonaMayorAcompananteRepository relacionAcompananteRepository;
    private final PersonaMayorOrganizacionRepository relacionOrganizacionRepository;
    private final UsuarioLookupRepository usuarioLookupRepository;
    private final MessagingClient messagingClient;

    public EmergenciaController(
            PersonaMayorAcompananteRepository relacionAcompananteRepository,
            PersonaMayorOrganizacionRepository relacionOrganizacionRepository,
            UsuarioLookupRepository usuarioLookupRepository,
            MessagingClient messagingClient
    ) {
        this.relacionAcompananteRepository = relacionAcompananteRepository;
        this.relacionOrganizacionRepository = relacionOrganizacionRepository;
        this.usuarioLookupRepository = usuarioLookupRepository;
        this.messagingClient = messagingClient;
    }

    @PostMapping
    public ResponseEntity<String> enviarEmergencia(
            @RequestHeader("X-User-Id") Integer idPersonaMayor
    ) {
        UsuarioLookup personaMayor = usuarioLookupRepository.findById(idPersonaMayor).orElse(null);

        String nombrePersonaMayor =
                personaMayor != null ? personaMayor.getNombreUsuario() : "Una persona mayor";

        String mensaje = "ALERTA DE EMERGENCIA: " + nombrePersonaMayor
                + " ha activado una alerta desde VITA+. Por favor, verifica que se encuentre bien.";

        int enviadosAcompanantes = 0;
        int enviadosOrganizaciones = 0;

        List<PersonaMayorAcompanante> relacionesAcompanantes =
                relacionAcompananteRepository.findById_IdPersonaMayorAndEstado(idPersonaMayor, "ACEPTADA");

        for (PersonaMayorAcompanante relacion : relacionesAcompanantes) {

            Integer idAcompanante = relacion.getId().getIdAcompanante();

            String telefono = usuarioLookupRepository.findById(idAcompanante)
                    .map(UsuarioLookup::getTelefono)
                    .orElse(null);

            if (telefono == null || telefono.isBlank()) {
                continue;
            }

            if (messagingClient.enviarMensaje(telefono, mensaje)) {
                enviadosAcompanantes++;
            }
        }

        List<PersonaMayorOrganizacion> relacionesOrganizaciones =
                relacionOrganizacionRepository.findById_IdPersonaMayorAndEstado(idPersonaMayor, "ACEPTADA");

        for (PersonaMayorOrganizacion relacion : relacionesOrganizaciones) {

            Integer idOrganizacion = relacion.getId().getIdOrganizacion();

            List<UsuarioLookup> usuariosOrganizacion =
                    usuarioLookupRepository.findByIdOrganizacion(idOrganizacion);

            String telefono = usuariosOrganizacion.isEmpty()
                    ? null
                    : usuariosOrganizacion.get(0).getTelefono();

            if (telefono == null || telefono.isBlank()) {
                continue;
            }

            if (messagingClient.enviarMensaje(telefono, mensaje)) {
                enviadosOrganizaciones++;
            }
        }

        int totalEnviados = enviadosAcompanantes + enviadosOrganizaciones;

        if (totalEnviados == 0) {
            return ResponseEntity.badRequest()
                    .body("No se pudo enviar la alerta a ningún acompañante u organización");
        }

        return ResponseEntity.ok(
                "Alerta de emergencia enviada a " + enviadosAcompanantes
                        + " acompañante(s) y " + enviadosOrganizaciones + " organización(es)"
        );
    }
}
