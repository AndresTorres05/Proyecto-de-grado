package com.proyectogrado.persona_mayor_service.controller;

import com.proyectogrado.persona_mayor_service.dto.AcompananteResponse;
import com.proyectogrado.persona_mayor_service.dto.AgregarAcompananteRequest;
import com.proyectogrado.persona_mayor_service.model.AcompananteLookup;
import com.proyectogrado.persona_mayor_service.model.PersonaMayorAcompanante;
import com.proyectogrado.persona_mayor_service.model.PersonaMayorAcompananteId;
import com.proyectogrado.persona_mayor_service.model.UsuarioLookup;
import com.proyectogrado.persona_mayor_service.repository.AcompananteLookupRepository;
import com.proyectogrado.persona_mayor_service.repository.PersonaMayorAcompananteRepository;
import com.proyectogrado.persona_mayor_service.repository.UsuarioLookupRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        if (request.celular() == null || request.celular().isBlank()) {
            return ResponseEntity.badRequest().body("El celular es obligatorio");
        }

        UsuarioLookup usuario = usuarioLookupRepository
                .findByCelular(request.celular())
                .orElse(null);

        if (usuario == null) {
            return ResponseEntity.badRequest()
                    .body("No existe un usuario registrado con ese celular");
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

        acompanante.setRelacion(request.relacion());
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

    @DeleteMapping("/{idAcompanante}")
    public ResponseEntity<String> cancelarAsociacion(
            @RequestHeader("X-User-Id") Integer idPersonaMayor,
            @PathVariable Integer idAcompanante
    ) {
        PersonaMayorAcompananteId idRelacion =
                new PersonaMayorAcompananteId(idPersonaMayor, idAcompanante);

        PersonaMayorAcompanante relacion =
                relacionRepository.findById(idRelacion).orElse(null);

        if (relacion == null) {
            return ResponseEntity.status(404).body("No existe una asociación registrada");
        }

        relacionRepository.delete(relacion);
        relacionRepository.flush();

        return ResponseEntity.ok("Asociación cancelada correctamente");
    }

    private AcompananteResponse construirRespuesta(Integer idAcompanante) {

        UsuarioLookup usuario = usuarioLookupRepository.findById(idAcompanante).orElse(null);

        String relacion = acompananteLookupRepository.findById(idAcompanante)
                .map(AcompananteLookup::getRelacion)
                .orElse(null);

        return new AcompananteResponse(
                idAcompanante,
                usuario != null ? usuario.getNombreUsuario() : null,
                usuario != null ? usuario.getCelular() : null,
                relacion
        );
    }
}
