package com.proyectogrado.voluntario_service.controller;

import com.proyectogrado.voluntario_service.dto.OrganizacionVoluntarioResponse;
import com.proyectogrado.voluntario_service.dto.VoluntarioOrganizacionResponse;
import com.proyectogrado.voluntario_service.model.OrganizacionLookup;
import com.proyectogrado.voluntario_service.model.UsuarioLookup;
import com.proyectogrado.voluntario_service.model.VoluntarioOrganizacion;
import com.proyectogrado.voluntario_service.model.VoluntarioOrganizacionId;
import com.proyectogrado.voluntario_service.repository.OrganizacionLookupRepository;
import com.proyectogrado.voluntario_service.repository.UsuarioLookupRepository;
import com.proyectogrado.voluntario_service.repository.VoluntarioOrganizacionRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Vinculacion de voluntarios con organizaciones:
 * - El voluntario consulta las organizaciones y envia solicitudes.
 * - La organizacion acepta o rechaza las solicitudes y ve sus voluntarios.
 *
 * Igual que PersonaMayorOrganizacionController en personamayor-service,
 * aqui viven los dos lados del vinculo. El api-gateway enruta
 * /api/organizacion/voluntarios/** a este servicio.
 *
 * El id del usuario autenticado llega en el header X-User-Id, puesto
 * por el api-gateway despues de validar el JWT.
 */
@RestController
public class VoluntarioOrganizacionController {

    private final VoluntarioOrganizacionRepository relacionRepository;
    private final OrganizacionLookupRepository organizacionLookupRepository;
    private final UsuarioLookupRepository usuarioLookupRepository;

    public VoluntarioOrganizacionController(
            VoluntarioOrganizacionRepository relacionRepository,
            OrganizacionLookupRepository organizacionLookupRepository,
            UsuarioLookupRepository usuarioLookupRepository
    ) {
        this.relacionRepository = relacionRepository;
        this.organizacionLookupRepository = organizacionLookupRepository;
        this.usuarioLookupRepository = usuarioLookupRepository;
    }

    // =========================================================
    // VOLUNTARIO: organizaciones y solicitudes
    // =========================================================

    /**
     * Todas las organizaciones, cada una con el estado del vinculo del
     * voluntario (null si nunca ha solicitado). Con esto el frontend arma
     * "mis organizaciones", "mis solicitudes" y "disponibles".
     */
    @GetMapping("/api/voluntario/organizaciones")
    public ResponseEntity<?> listarOrganizaciones(
            @RequestHeader("X-User-Id") Integer idVoluntario
    ) {
        if (!esVoluntario(idVoluntario)) {
            return noEsVoluntario();
        }

        Map<Integer, String> estados = relacionRepository.findById_IdVoluntario(idVoluntario)
                .stream()
                .collect(Collectors.toMap(
                        relacion -> relacion.getId().getIdOrganizacion(),
                        VoluntarioOrganizacion::getEstado));

        List<OrganizacionVoluntarioResponse> respuesta = organizacionLookupRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(
                        OrganizacionLookup::getNombre,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .map(organizacion -> aOrganizacion(
                        organizacion,
                        estados.get(organizacion.getIdOrganizacion())))
                .toList();

        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/api/voluntario/organizaciones/{idOrganizacion}/solicitud")
    public ResponseEntity<String> solicitarVinculacion(
            @RequestHeader("X-User-Id") Integer idVoluntario,
            @PathVariable Integer idOrganizacion
    ) {
        if (!esVoluntario(idVoluntario)) {
            return noEsVoluntario();
        }

        if (!organizacionLookupRepository.existsById(idOrganizacion)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("La organización no existe");
        }

        VoluntarioOrganizacion relacion = relacionRepository
                .findById(new VoluntarioOrganizacionId(idVoluntario, idOrganizacion))
                .orElse(null);

        if (relacion != null) {
            if (VoluntarioOrganizacion.ACEPTADA.equals(relacion.getEstado())) {
                return ResponseEntity.badRequest()
                        .body("Ya estás vinculado a esta organización");
            }

            if (VoluntarioOrganizacion.PENDIENTE.equals(relacion.getEstado())) {
                return ResponseEntity.badRequest()
                        .body("Ya tienes una solicitud pendiente con esta organización");
            }

            // Rechazada antes: se puede volver a solicitar
            relacion.setEstado(VoluntarioOrganizacion.PENDIENTE);
        } else {
            relacion = new VoluntarioOrganizacion(idVoluntario, idOrganizacion);
        }

        relacionRepository.saveAndFlush(relacion);

        return ResponseEntity.ok("Solicitud de vinculación enviada correctamente");
    }

    /**
     * Borra el vinculo, sea cual sea su estado: cancela una solicitud
     * pendiente, descarta una rechazada o desvincula al voluntario.
     */
    @DeleteMapping("/api/voluntario/organizaciones/{idOrganizacion}")
    public ResponseEntity<String> eliminarVinculoDesdeVoluntario(
            @RequestHeader("X-User-Id") Integer idVoluntario,
            @PathVariable Integer idOrganizacion
    ) {
        VoluntarioOrganizacion relacion = relacionRepository
                .findById(new VoluntarioOrganizacionId(idVoluntario, idOrganizacion))
                .orElse(null);

        if (relacion == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No tienes ningún vínculo ni solicitud con esta organización");
        }

        String mensaje = switch (relacion.getEstado()) {
            case VoluntarioOrganizacion.PENDIENTE -> "Solicitud cancelada correctamente";
            case VoluntarioOrganizacion.ACEPTADA -> "Te desvinculaste de la organización correctamente";
            default -> "Solicitud eliminada correctamente";
        };

        relacionRepository.delete(relacion);
        relacionRepository.flush();

        return ResponseEntity.ok(mensaje);
    }

    // =========================================================
    // ORGANIZACION: voluntarios y solicitudes
    // =========================================================

    @GetMapping("/api/organizacion/voluntarios")
    public ResponseEntity<?> listarVoluntarios(
            @RequestHeader("X-User-Id") Integer idUsuarioOrganizacion
    ) {
        return listarPorEstado(idUsuarioOrganizacion, VoluntarioOrganizacion.ACEPTADA);
    }

    @GetMapping("/api/organizacion/voluntarios/solicitudes")
    public ResponseEntity<?> listarSolicitudes(
            @RequestHeader("X-User-Id") Integer idUsuarioOrganizacion
    ) {
        return listarPorEstado(idUsuarioOrganizacion, VoluntarioOrganizacion.PENDIENTE);
    }

    @PutMapping("/api/organizacion/voluntarios/solicitudes/{idVoluntario}/aceptar")
    public ResponseEntity<String> aceptarSolicitud(
            @RequestHeader("X-User-Id") Integer idUsuarioOrganizacion,
            @PathVariable Integer idVoluntario
    ) {
        return responderSolicitud(idUsuarioOrganizacion, idVoluntario,
                VoluntarioOrganizacion.ACEPTADA, "Solicitud aceptada correctamente");
    }

    @PutMapping("/api/organizacion/voluntarios/solicitudes/{idVoluntario}/rechazar")
    public ResponseEntity<String> rechazarSolicitud(
            @RequestHeader("X-User-Id") Integer idUsuarioOrganizacion,
            @PathVariable Integer idVoluntario
    ) {
        return responderSolicitud(idUsuarioOrganizacion, idVoluntario,
                VoluntarioOrganizacion.RECHAZADA, "Solicitud rechazada correctamente");
    }

    @DeleteMapping("/api/organizacion/voluntarios/{idVoluntario}")
    public ResponseEntity<String> desvincularVoluntario(
            @RequestHeader("X-User-Id") Integer idUsuarioOrganizacion,
            @PathVariable Integer idVoluntario
    ) {
        Integer idOrganizacion = obtenerIdOrganizacion(idUsuarioOrganizacion);

        if (idOrganizacion == null) {
            return sinOrganizacion();
        }

        VoluntarioOrganizacion relacion = relacionRepository
                .findById(new VoluntarioOrganizacionId(idVoluntario, idOrganizacion))
                .orElse(null);

        if (relacion == null || !VoluntarioOrganizacion.ACEPTADA.equals(relacion.getEstado())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El voluntario no está vinculado a esta organización");
        }

        relacionRepository.delete(relacion);
        relacionRepository.flush();

        return ResponseEntity.ok("Voluntario desvinculado correctamente");
    }

    // =========================================================
    // Helpers
    // =========================================================

    // Se mira el rol y no la tabla "voluntario": hay cuentas viejas con el
    // rol VOLUNTARIO que no tienen su fila en esa tabla.
    private boolean esVoluntario(Integer idUsuario) {
        return usuarioLookupRepository.tieneRol(idUsuario, "VOLUNTARIO");
    }

    /**
     * Organizacion del usuario autenticado, o null si no es una cuenta de
     * organizacion. Se exige el rol: otros usuarios podrian tener
     * id_organizacion por datos viejos.
     */
    private Integer obtenerIdOrganizacion(Integer idUsuario) {
        if (!usuarioLookupRepository.tieneRol(idUsuario, "ORGANIZACION")) {
            return null;
        }

        return usuarioLookupRepository.findById(idUsuario)
                .map(UsuarioLookup::getIdOrganizacion)
                .orElse(null);
    }

    private ResponseEntity<?> listarPorEstado(Integer idUsuarioOrganizacion, String estado) {
        Integer idOrganizacion = obtenerIdOrganizacion(idUsuarioOrganizacion);

        if (idOrganizacion == null) {
            return sinOrganizacion();
        }

        List<Integer> ids = relacionRepository.findById_IdOrganizacionAndEstado(idOrganizacion, estado)
                .stream()
                .map(relacion -> relacion.getId().getIdVoluntario())
                .toList();

        Map<Integer, UsuarioLookup> usuarios = usuarioLookupRepository.findAllById(ids)
                .stream()
                .collect(Collectors.toMap(UsuarioLookup::getIdUsuario, Function.identity()));

        List<VoluntarioOrganizacionResponse> respuesta = ids.stream()
                .map(usuarios::get)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(
                        UsuarioLookup::getNombreUsuario,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .map(usuario -> new VoluntarioOrganizacionResponse(
                        usuario.getIdUsuario(),
                        usuario.getNombreUsuario(),
                        usuario.getCelular(),
                        usuario.getCorreo()))
                .toList();

        return ResponseEntity.ok(respuesta);
    }

    private ResponseEntity<String> responderSolicitud(
            Integer idUsuarioOrganizacion,
            Integer idVoluntario,
            String nuevoEstado,
            String mensaje
    ) {
        Integer idOrganizacion = obtenerIdOrganizacion(idUsuarioOrganizacion);

        if (idOrganizacion == null) {
            return sinOrganizacion();
        }

        VoluntarioOrganizacion relacion = relacionRepository
                .findById(new VoluntarioOrganizacionId(idVoluntario, idOrganizacion))
                .orElse(null);

        if (relacion == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró la solicitud de este voluntario");
        }

        if (!VoluntarioOrganizacion.PENDIENTE.equals(relacion.getEstado())) {
            return ResponseEntity.badRequest().body("Esta solicitud ya fue procesada");
        }

        relacion.setEstado(nuevoEstado);
        relacionRepository.saveAndFlush(relacion);

        return ResponseEntity.ok(mensaje);
    }

    private OrganizacionVoluntarioResponse aOrganizacion(OrganizacionLookup organizacion, String estado) {
        UsuarioLookup contacto = usuarioLookupRepository
                .findCuentasOrganizacion(organizacion.getIdOrganizacion())
                .stream()
                .findFirst()
                .orElse(null);

        return new OrganizacionVoluntarioResponse(
                organizacion.getIdOrganizacion(),
                organizacion.getNombre(),
                organizacion.getDireccion(),
                contacto != null ? contacto.getCelular() : null,
                contacto != null ? contacto.getCorreo() : null,
                estado
        );
    }

    private static ResponseEntity<String> noEsVoluntario() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Esta función es solo para voluntarios");
    }

    private static ResponseEntity<String> sinOrganizacion() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("El usuario no tiene una organización asociada");
    }
}
