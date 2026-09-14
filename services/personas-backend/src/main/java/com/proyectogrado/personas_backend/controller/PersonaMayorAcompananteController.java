package com.proyectogrado.personas_backend.controller;

import com.proyectogrado.personas_backend.dto.AcompananteResponse;
import com.proyectogrado.personas_backend.dto.AgregarAcompananteRequest;
import com.proyectogrado.personas_backend.model.AcompananteLookup;
import com.proyectogrado.personas_backend.model.PersonaMayorAcompanante;
import com.proyectogrado.personas_backend.model.PersonaMayorAcompananteId;
import com.proyectogrado.personas_backend.model.UsuarioLookup;
import com.proyectogrado.personas_backend.repository.AcompananteLookupRepository;
import com.proyectogrado.personas_backend.repository.PersonaMayorAcompananteRepository;
import com.proyectogrado.personas_backend.repository.UsuarioLookupRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Lado "persona mayor" de la relacion con acompanantes.
 *
 * El id del usuario autenticado llega en el header X-User-Id, puesto
 * por el api-gateway despues de validar el JWT. Este servicio no valida
 * tokens.
 */
@RestController
@RequestMapping("/api/persona-mayor/acompanantes")
public class PersonaMayorAcompananteController {

    private final PersonaMayorAcompananteRepository relacionRepository;
    private final UsuarioLookupRepository usuarioLookupRepository;
    private final AcompananteLookupRepository acompananteLookupRepository;

    public PersonaMayorAcompananteController(
            PersonaMayorAcompananteRepository relacionRepository,
            UsuarioLookupRepository usuarioLookupRepository,
            AcompananteLookupRepository acompananteLookupRepository
    ) {
        this.relacionRepository = relacionRepository;
        this.usuarioLookupRepository = usuarioLookupRepository;
        this.acompananteLookupRepository = acompananteLookupRepository;
    }

    @GetMapping
    public ResponseEntity<List<AcompananteResponse>> listarAcompanantes(
            @RequestHeader("X-User-Id") Integer idPersonaMayor
    ) {
        List<PersonaMayorAcompanante> relaciones =
                relacionRepository.findById_IdPersonaMayorAndEstado(idPersonaMayor, "ACEPTADA");

        List<AcompananteResponse> respuesta = relaciones.stream()
                .map(relacion -> construirRespuesta(relacion.getId().getIdAcompanante()))
                .toList();

        return ResponseEntity.ok(respuesta);
    }

    @PostMapping
    public ResponseEntity<?> agregarAcompanante(
            @RequestHeader("X-User-Id") Integer idPersonaMayor,
            @RequestBody AgregarAcompananteRequest request
    ) {
        if (request.telefono() == null || request.telefono().isBlank()) {
            return ResponseEntity.badRequest().body("El teléfono es obligatorio");
        }

        UsuarioLookup usuario = usuarioLookupRepository
                .findByTelefono(request.telefono())
                .orElse(null);

        if (usuario == null) {
            return ResponseEntity.badRequest()
                    .body("No existe un usuario registrado con ese teléfono");
        }

        AcompananteLookup acompanante = acompananteLookupRepository
                .findById(usuario.getIdUsuario())
                .orElse(null);

        if (acompanante == null) {
            return ResponseEntity.badRequest()
                    .body("El usuario existe, pero no está registrado como acompañante");
        }

        PersonaMayorAcompananteId idRelacion =
                new PersonaMayorAcompananteId(idPersonaMayor, acompanante.getIdUsuario());

        PersonaMayorAcompanante relacionExistente =
                relacionRepository.findById(idRelacion).orElse(null);

        acompanante.setParentesco(request.parentesco());
        acompananteLookupRepository.saveAndFlush(acompanante);

        if (relacionExistente != null) {

            if ("ACEPTADA".equals(relacionExistente.getEstado())) {
                return ResponseEntity.badRequest().body("Este acompañante ya está registrado");
            }

            if ("PENDIENTE".equals(relacionExistente.getEstado())) {
                return ResponseEntity.badRequest()
                        .body("Ya existe una solicitud pendiente para este acompañante");
            }

            // Estaba RECHAZADA: se permite volver a intentar.
            relacionExistente.setEstado("PENDIENTE");
            relacionRepository.saveAndFlush(relacionExistente);

            return ResponseEntity.ok("Solicitud de acompañamiento enviada correctamente");
        }

        PersonaMayorAcompanante relacion =
                new PersonaMayorAcompanante(idPersonaMayor, acompanante.getIdUsuario());

        relacion.setEstado("PENDIENTE");
        relacionRepository.saveAndFlush(relacion);

        return ResponseEntity.ok("Solicitud de acompañamiento enviada correctamente");
    }

    private AcompananteResponse construirRespuesta(Integer idAcompanante) {

        UsuarioLookup usuario = usuarioLookupRepository.findById(idAcompanante).orElse(null);

        String parentesco = acompananteLookupRepository.findById(idAcompanante)
                .map(AcompananteLookup::getParentesco)
                .orElse(null);

        return new AcompananteResponse(
                idAcompanante,
                usuario != null ? usuario.getNombreUsuario() : null,
                usuario != null ? usuario.getTelefono() : null,
                parentesco
        );
    }
}
