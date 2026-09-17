package com.proyectogrado.acompanante_service.controller;

import com.proyectogrado.acompanante_service.dto.PersonaMayorResponse;
import com.proyectogrado.acompanante_service.model.PersonaMayorAcompanante;
import com.proyectogrado.acompanante_service.model.UsuarioLookup;
import com.proyectogrado.acompanante_service.repository.PersonaMayorAcompananteRepository;
import com.proyectogrado.acompanante_service.repository.UsuarioLookupRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Lado "acompanante" de la relacion con personas mayores:
 * ver sus personas mayores, ver solicitudes pendientes, aceptar/rechazar.
 *
 * El id del usuario autenticado llega en el header X-User-Id, puesto
 * por el api-gateway despues de validar el JWT. Este servicio no valida
 * tokens.
 */
@RestController
@RequestMapping("/api/acompanante/personas-mayores")
public class AcompananteRelacionController {

    private final PersonaMayorAcompananteRepository relacionRepository;
    private final UsuarioLookupRepository usuarioLookupRepository;

    public AcompananteRelacionController(
            PersonaMayorAcompananteRepository relacionRepository,
            UsuarioLookupRepository usuarioLookupRepository
    ) {
        this.relacionRepository = relacionRepository;
        this.usuarioLookupRepository = usuarioLookupRepository;
    }

    @GetMapping
    public ResponseEntity<List<PersonaMayorResponse>> obtenerPersonasMayores(
            @RequestHeader("X-User-Id") Integer idAcompanante
    ) {
        return ResponseEntity.ok(listarPorEstado(idAcompanante, "ACEPTADA"));
    }

    @GetMapping("/solicitudes")
    public ResponseEntity<List<PersonaMayorResponse>> obtenerSolicitudesPendientes(
            @RequestHeader("X-User-Id") Integer idAcompanante
    ) {
        return ResponseEntity.ok(listarPorEstado(idAcompanante, "PENDIENTE"));
    }

    @PutMapping("/solicitudes/{idPersonaMayor}/aceptar")
    public ResponseEntity<String> aceptarSolicitud(
            @RequestHeader("X-User-Id") Integer idAcompanante,
            @PathVariable Integer idPersonaMayor
    ) {
        return cambiarEstado(idAcompanante, idPersonaMayor, "ACEPTADA", "aceptada");
    }

    @PutMapping("/solicitudes/{idPersonaMayor}/rechazar")
    public ResponseEntity<String> rechazarSolicitud(
            @RequestHeader("X-User-Id") Integer idAcompanante,
            @PathVariable Integer idPersonaMayor
    ) {
        return cambiarEstado(idAcompanante, idPersonaMayor, "RECHAZADA", "rechazada");
    }

    private ResponseEntity<String> cambiarEstado(
            Integer idAcompanante,
            Integer idPersonaMayor,
            String nuevoEstado,
            String participioParaMensaje
    ) {
        PersonaMayorAcompanante relacion = relacionRepository
                .findById_IdAcompanante(idAcompanante).stream()
                .filter(r -> r.getId().getIdPersonaMayor().equals(idPersonaMayor))
                .findFirst()
                .orElse(null);

        if (relacion == null) {
            return ResponseEntity.notFound().build();
        }

        if (!"PENDIENTE".equals(relacion.getEstado())) {
            return ResponseEntity.badRequest().body("Esta solicitud ya fue procesada");
        }

        relacion.setEstado(nuevoEstado);
        relacionRepository.save(relacion);

        return ResponseEntity.ok("Solicitud de acompañamiento " + participioParaMensaje);
    }

    private List<PersonaMayorResponse> listarPorEstado(Integer idAcompanante, String estado) {

        List<PersonaMayorAcompanante> relaciones =
                relacionRepository.findById_IdAcompananteAndEstado(idAcompanante, estado);

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
