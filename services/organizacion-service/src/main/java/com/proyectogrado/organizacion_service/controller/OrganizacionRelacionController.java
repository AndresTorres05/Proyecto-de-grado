package com.proyectogrado.organizacion_service.controller;

import com.proyectogrado.organizacion_service.dto.AsociarPersonaMayorRequest;
import com.proyectogrado.organizacion_service.dto.PersonaMayorResponse;
import com.proyectogrado.organizacion_service.model.PersonaMayorOrganizacion;
import com.proyectogrado.organizacion_service.model.PersonaMayorOrganizacionId;
import com.proyectogrado.organizacion_service.model.UsuarioLookup;
import com.proyectogrado.organizacion_service.repository.PersonaMayorOrganizacionRepository;
import com.proyectogrado.organizacion_service.repository.UsuarioLookupRepository;

import org.springframework.http.HttpStatus;
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
 * Lado "organizacion" de la relacion con personas mayores:
 * verlas, asociar una nueva por teléfono, cancelar la asociación.
 *
 * El id del usuario autenticado llega en el header X-User-Id, puesto
 * por el api-gateway despues de validar el JWT. Este servicio no valida
 * tokens.
 *
 * OJO: X-User-Id es el id del USUARIO de la organización (quien inició
 * sesión), no el id_organizacion. Por eso el primer paso de cada
 * endpoint es resolver uno a partir del otro con obtenerIdOrganizacion().
 */
@RestController
@RequestMapping("/api/organizacion/personas-mayores")
public class OrganizacionRelacionController {

    private final PersonaMayorOrganizacionRepository relacionRepository;
    private final UsuarioLookupRepository usuarioLookupRepository;

    public OrganizacionRelacionController(
            PersonaMayorOrganizacionRepository relacionRepository,
            UsuarioLookupRepository usuarioLookupRepository
    ) {
        this.relacionRepository = relacionRepository;
        this.usuarioLookupRepository = usuarioLookupRepository;
    }

    @GetMapping
    public ResponseEntity<?> obtenerPersonasMayores(
            @RequestHeader("X-User-Id") Integer idUsuarioOrganizacion
    ) {
        Integer idOrganizacion = obtenerIdOrganizacion(idUsuarioOrganizacion);

        if (idOrganizacion == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El usuario no tiene una organización asociada");
        }

        List<PersonaMayorOrganizacion> relaciones =
                relacionRepository.findById_IdOrganizacionAndEstado(idOrganizacion, "ACEPTADA");

        return ResponseEntity.ok(mapearAPersonaMayor(relaciones));
    }

    @PostMapping
    public ResponseEntity<?> asociarPersonaMayor(
            @RequestHeader("X-User-Id") Integer idUsuarioOrganizacion,
            @RequestBody AsociarPersonaMayorRequest request
    ) {
        Integer idOrganizacion = obtenerIdOrganizacion(idUsuarioOrganizacion);

        if (idOrganizacion == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El usuario no tiene una organización asociada");
        }

        UsuarioLookup usuarioPersonaMayor = usuarioLookupRepository
                .findByTelefono(request.telefono())
                .orElse(null);

        if (usuarioPersonaMayor == null) {
            return ResponseEntity.badRequest()
                    .body("No existe un usuario registrado con ese teléfono");
        }

        Integer idPersonaMayor = usuarioPersonaMayor.getIdUsuario();

        PersonaMayorOrganizacionId idRelacion =
                new PersonaMayorOrganizacionId(idPersonaMayor, idOrganizacion);

        PersonaMayorOrganizacion relacionExistente =
                relacionRepository.findById(idRelacion).orElse(null);

        if (relacionExistente != null) {

            if ("ACEPTADA".equals(relacionExistente.getEstado())) {
                return ResponseEntity.badRequest()
                        .body("Esta persona mayor ya está asociada a la organización");
            }

            if ("PENDIENTE".equals(relacionExistente.getEstado())) {
                return ResponseEntity.badRequest()
                        .body("Ya existe una solicitud pendiente para esta persona mayor");
            }

            relacionExistente.setEstado("PENDIENTE");
            relacionRepository.saveAndFlush(relacionExistente);

            return ResponseEntity.ok("Solicitud de asociación enviada correctamente");
        }

        PersonaMayorOrganizacion relacion =
                new PersonaMayorOrganizacion(idPersonaMayor, idOrganizacion);

        relacion.setEstado("PENDIENTE");
        relacionRepository.saveAndFlush(relacion);

        return ResponseEntity.ok("Solicitud de asociación enviada correctamente");
    }

    @DeleteMapping("/{idPersonaMayor}")
    public ResponseEntity<?> cancelarAsociacion(
            @RequestHeader("X-User-Id") Integer idUsuarioOrganizacion,
            @PathVariable Integer idPersonaMayor
    ) {
        Integer idOrganizacion = obtenerIdOrganizacion(idUsuarioOrganizacion);

        if (idOrganizacion == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El usuario no tiene una organización asociada");
        }

        PersonaMayorOrganizacionId idRelacion =
                new PersonaMayorOrganizacionId(idPersonaMayor, idOrganizacion);

        PersonaMayorOrganizacion relacion =
                relacionRepository.findById(idRelacion).orElse(null);

        if (relacion == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No existe una asociación registrada");
        }

        relacionRepository.delete(relacion);
        relacionRepository.flush();

        return ResponseEntity.ok("Asociación cancelada correctamente");
    }

    private Integer obtenerIdOrganizacion(Integer idUsuario) {
        return usuarioLookupRepository.findById(idUsuario)
                .map(UsuarioLookup::getIdOrganizacion)
                .orElse(null);
    }

    private List<PersonaMayorResponse> mapearAPersonaMayor(List<PersonaMayorOrganizacion> relaciones) {
        return relaciones.stream()
                .map(relacion -> {
                    Integer idPersonaMayor = relacion.getId().getIdPersonaMayor();
                    UsuarioLookup usuario = usuarioLookupRepository.findById(idPersonaMayor).orElse(null);

                    return new PersonaMayorResponse(
                            idPersonaMayor,
                            usuario != null ? usuario.getNombreUsuario() : null,
                            usuario != null ? usuario.getTelefono() : null,
                            usuario != null ? usuario.getCorreo() : null
                    );
                })
                .toList();
    }
}
