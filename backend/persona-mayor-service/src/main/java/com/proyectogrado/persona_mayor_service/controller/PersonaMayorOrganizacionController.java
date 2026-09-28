package com.proyectogrado.persona_mayor_service.controller;

import com.proyectogrado.persona_mayor_service.dto.AsociarPersonaMayorRequest;
import com.proyectogrado.persona_mayor_service.dto.OrganizacionSolicitudResponse;
import com.proyectogrado.persona_mayor_service.dto.PersonaMayorResponse;
import com.proyectogrado.persona_mayor_service.model.PersonaMayorOrganizacion;
import com.proyectogrado.persona_mayor_service.model.PersonaMayorOrganizacionId;
import com.proyectogrado.persona_mayor_service.model.UsuarioLookup;
import com.proyectogrado.persona_mayor_service.repository.PersonaMayorOrganizacionRepository;
import com.proyectogrado.persona_mayor_service.repository.UsuarioLookupRepository;
import com.proyectogrado.persona_mayor_service.repository.AcompananteLookupRepository;
import com.proyectogrado.persona_mayor_service.dto.AcompananteResponse;
import com.proyectogrado.persona_mayor_service.repository.PersonaMayorAcompananteRepository;
import com.proyectogrado.persona_mayor_service.model.AcompananteLookup;
import com.proyectogrado.persona_mayor_service.model.PersonaMayorAcompanante;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PersonaMayorOrganizacionController {

    private final PersonaMayorOrganizacionRepository relacionRepository;
    private final UsuarioLookupRepository usuarioLookupRepository;
    private final AcompananteLookupRepository acompananteLookupRepository;
    private final PersonaMayorAcompananteRepository personaMayorAcompananteRepository;
    

public PersonaMayorOrganizacionController(
        PersonaMayorOrganizacionRepository relacionRepository,
        UsuarioLookupRepository usuarioLookupRepository,
        AcompananteLookupRepository acompananteLookupRepository,
        PersonaMayorAcompananteRepository personaMayorAcompananteRepository
) {
    this.relacionRepository = relacionRepository;
    this.usuarioLookupRepository = usuarioLookupRepository;
    this.acompananteLookupRepository = acompananteLookupRepository;
    this.personaMayorAcompananteRepository = personaMayorAcompananteRepository;
}

    // =========================================================
    // ORGANIZACION: personas mayores asociadas
    // =========================================================

    @GetMapping("/api/organizacion/personas-mayores")
    public ResponseEntity<?> obtenerPersonasMayoresOrganizacion(
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

    // =========================================================
// ORGANIZACION: acompanantes de una persona mayor
// =========================================================

@GetMapping("/api/organizacion/personas-mayores/{idPersonaMayor}/acompanantes")
public ResponseEntity<?> obtenerAcompanantesPersonaMayor(
        @RequestHeader("X-User-Id") Integer idUsuarioOrganizacion,
        @PathVariable Integer idPersonaMayor
) {

    Integer idOrganizacion = obtenerIdOrganizacion(idUsuarioOrganizacion);

    if (idOrganizacion == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("El usuario no tiene una organización asociada");
    }

    // Verificar que la persona mayor pertenece a esta organización
    PersonaMayorOrganizacionId idRelacion =
            new PersonaMayorOrganizacionId(
                    idPersonaMayor,
                    idOrganizacion
            );

    PersonaMayorOrganizacion relacion =
            relacionRepository.findById(idRelacion).orElse(null);

    if (relacion == null || !"ACEPTADA".equals(relacion.getEstado())) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("La persona mayor no está asociada a esta organización");
    }

    // Buscar acompanantes aceptados
    List<PersonaMayorAcompanante> relaciones =
            personaMayorAcompananteRepository
                    .findById_IdPersonaMayorAndEstado(
                            idPersonaMayor,
                            "ACEPTADA"
                    );

    List<AcompananteResponse> acompanantes = relaciones.stream()
            .map(relacionAcompanante -> {

                Integer idAcompanante =
                        relacionAcompanante.getId().getIdAcompanante();

                UsuarioLookup usuario =
                        usuarioLookupRepository
                                .findById(idAcompanante)
                                .orElse(null);

                AcompananteLookup acompanante =
                        acompananteLookupRepository
                                .findById(idAcompanante)
                                .orElse(null);

                if (usuario == null) {
                    return null;
                }

                return new AcompananteResponse(
                        idAcompanante,
                        usuario.getNombreUsuario(),
                        usuario.getCelular(),
                        acompanante != null
                                ? acompanante.getRelacion()
                                : null
                );
            })
            .filter(java.util.Objects::nonNull)
            .toList();

    return ResponseEntity.ok(acompanantes);
}

    @PostMapping("/api/organizacion/personas-mayores")
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
                .findByCelular(request.celular())
                .orElse(null);

        if (usuarioPersonaMayor == null) {
            return ResponseEntity.badRequest()
                    .body("No existe un usuario registrado con ese celular");
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

    @DeleteMapping("/api/organizacion/personas-mayores/{idPersonaMayor}")
    public ResponseEntity<?> cancelarAsociacionDesdeOrganizacion(
            @RequestHeader("X-User-Id") Integer idUsuarioOrganizacion,
            @PathVariable Integer idPersonaMayor
    ) {
        Integer idOrganizacion = obtenerIdOrganizacion(idUsuarioOrganizacion);

        if (idOrganizacion == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El usuario no tiene una organización asociada");
        }

        return eliminarRelacion(new PersonaMayorOrganizacionId(idPersonaMayor, idOrganizacion));
    }

    // =========================================================
    // PERSONA MAYOR: organizaciones
    // =========================================================

    @GetMapping("/api/persona-mayor/organizaciones")
    public ResponseEntity<List<OrganizacionSolicitudResponse>> obtenerOrganizaciones(
            @RequestHeader("X-User-Id") Integer idPersonaMayor
    ) {
        return ResponseEntity.ok(listarOrganizacionesPorEstado(idPersonaMayor, "ACEPTADA"));
    }

    @GetMapping("/api/persona-mayor/organizaciones/solicitudes")
    public ResponseEntity<List<OrganizacionSolicitudResponse>> obtenerSolicitudes(
            @RequestHeader("X-User-Id") Integer idPersonaMayor
    ) {
        return ResponseEntity.ok(listarOrganizacionesPorEstado(idPersonaMayor, "PENDIENTE"));
    }

    @PutMapping("/api/persona-mayor/organizaciones/solicitudes/{idOrganizacion}/aceptar")
    public ResponseEntity<String> aceptarSolicitudOrganizacion(
            @RequestHeader("X-User-Id") Integer idPersonaMayor,
            @PathVariable Integer idOrganizacion
    ) {
        return cambiarEstadoOrganizacion(idPersonaMayor, idOrganizacion, "ACEPTADA", "aceptada");
    }

    @PutMapping("/api/persona-mayor/organizaciones/solicitudes/{idOrganizacion}/rechazar")
    public ResponseEntity<String> rechazarSolicitudOrganizacion(
            @RequestHeader("X-User-Id") Integer idPersonaMayor,
            @PathVariable Integer idOrganizacion
    ) {
        return cambiarEstadoOrganizacion(idPersonaMayor, idOrganizacion, "RECHAZADA", "rechazada");
    }

    @DeleteMapping("/api/persona-mayor/organizaciones/{idOrganizacion}")
    public ResponseEntity<?> cancelarAsociacionDesdePersonaMayor(
            @RequestHeader("X-User-Id") Integer idPersonaMayor,
            @PathVariable Integer idOrganizacion
    ) {
        return eliminarRelacion(new PersonaMayorOrganizacionId(idPersonaMayor, idOrganizacion));
    }

    // =========================================================
    // Helpers
    // =========================================================

    private Integer obtenerIdOrganizacion(Integer idUsuario) {
        return usuarioLookupRepository.findById(idUsuario)
                .map(UsuarioLookup::getIdOrganizacion)
                .orElse(null);
    }

    private ResponseEntity<String> cambiarEstadoOrganizacion(
            Integer idPersonaMayor,
            Integer idOrganizacion,
            String nuevoEstado,
            String participio
    ) {
        PersonaMayorOrganizacion relacion = relacionRepository
                .findById(new PersonaMayorOrganizacionId(idPersonaMayor, idOrganizacion))
                .orElse(null);

        if (relacion == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró la solicitud de esta organización");
        }

        if (!"PENDIENTE".equals(relacion.getEstado())) {
            return ResponseEntity.badRequest().body("Esta solicitud ya fue procesada");
        }

        relacion.setEstado(nuevoEstado);
        relacionRepository.saveAndFlush(relacion);

        return ResponseEntity.ok("Solicitud " + participio + " correctamente");
    }

    private ResponseEntity<?> eliminarRelacion(PersonaMayorOrganizacionId idRelacion) {

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

    private List<OrganizacionSolicitudResponse> listarOrganizacionesPorEstado(
            Integer idPersonaMayor,
            String estado
    ) {
        List<PersonaMayorOrganizacion> relaciones =
                relacionRepository.findById_IdPersonaMayorAndEstado(idPersonaMayor, estado);

        return relaciones.stream()
                .map(relacion -> {
                    Integer idOrganizacion = relacion.getId().getIdOrganizacion();

                    List<UsuarioLookup> usuariosOrganizacion =
                            usuarioLookupRepository.findByIdOrganizacion(idOrganizacion);

                    UsuarioLookup usuarioOrganizacion =
                            usuariosOrganizacion.isEmpty() ? null : usuariosOrganizacion.get(0);

                    return new OrganizacionSolicitudResponse(
                            idOrganizacion,
                            usuarioOrganizacion != null ? usuarioOrganizacion.getNombreUsuario() : "Organización",
                            usuarioOrganizacion != null ? usuarioOrganizacion.getCelular() : null,
                            usuarioOrganizacion != null ? usuarioOrganizacion.getCorreo() : null
                    );
                })
                .toList();
    }

    private List<PersonaMayorResponse> mapearAPersonaMayor(List<PersonaMayorOrganizacion> relaciones) {
        return relaciones.stream()
                .map(relacion -> {
                    Integer idPersonaMayor = relacion.getId().getIdPersonaMayor();
                    UsuarioLookup usuario = usuarioLookupRepository.findById(idPersonaMayor).orElse(null);

                    return new PersonaMayorResponse(
                            idPersonaMayor,
                            usuario != null ? usuario.getNombreUsuario() : null,
                            usuario != null ? usuario.getCelular() : null,
                            usuario != null ? usuario.getCorreo() : null
                    );
                })
                .toList();
    }
}
