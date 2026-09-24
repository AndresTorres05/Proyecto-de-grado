package com.proyectogrado.actividad_service.controller;

import com.proyectogrado.actividad_service.dto.ActividadDisponibleResponse;
import com.proyectogrado.actividad_service.dto.ActividadRequest;
import com.proyectogrado.actividad_service.dto.ActividadResponse;
import com.proyectogrado.actividad_service.dto.AsistenciaRequest;
import com.proyectogrado.actividad_service.dto.ParticipanteActividadResponse;

import com.proyectogrado.actividad_service.model.Actividad;
import com.proyectogrado.actividad_service.model.Participacion;
import com.proyectogrado.actividad_service.model.PersonaMayorAcompananteLookup;
import com.proyectogrado.actividad_service.model.UsuarioLookup;

import com.proyectogrado.actividad_service.repository.AcompananteLookupRepository;
import com.proyectogrado.actividad_service.repository.ActividadRepository;
import com.proyectogrado.actividad_service.repository.ParticipacionRepository;
import com.proyectogrado.actividad_service.repository.PersonaMayorAcompananteLookupRepository;
import com.proyectogrado.actividad_service.repository.PersonaMayorLookupRepository;
import com.proyectogrado.actividad_service.repository.PersonaMayorOrganizacionLookupRepository;
import com.proyectogrado.actividad_service.repository.UsuarioLookupRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Actividades organizadas por una organizacion, inscripciones de
 * personas mayores y consulta de esas actividades por sus
 * acompañantes.
 *
 * El id del usuario autenticado llega en el header X-User-Id, puesto
 * por el api-gateway despues de validar el JWT. Este servicio no valida
 * tokens ni conoce roles explicitos: el rol se deduce consultando (de
 * solo lectura) las tablas "usuario", "persona_mayor" y "acompanante",
 * igual que en organizacion-service / personamayor-service.
 *
 * Los paths se mantienen identicos a los que exponia el monolito
 * (/api/actividades/**) para que el frontend no tenga que cambiar nada;
 * solo se agrega la ruta correspondiente en el api-gateway.
 */
@RestController
@RequestMapping("/api/actividades")
public class ActividadController {

    private final ActividadRepository actividadRepository;
    private final ParticipacionRepository participacionRepository;
    private final UsuarioLookupRepository usuarioLookupRepository;
    private final PersonaMayorLookupRepository personaMayorLookupRepository;
    private final AcompananteLookupRepository acompananteLookupRepository;
    private final PersonaMayorOrganizacionLookupRepository personaMayorOrganizacionRepository;
    private final PersonaMayorAcompananteLookupRepository personaMayorAcompananteRepository;

    public ActividadController(
            ActividadRepository actividadRepository,
            ParticipacionRepository participacionRepository,
            UsuarioLookupRepository usuarioLookupRepository,
            PersonaMayorLookupRepository personaMayorLookupRepository,
            AcompananteLookupRepository acompananteLookupRepository,
            PersonaMayorOrganizacionLookupRepository personaMayorOrganizacionRepository,
            PersonaMayorAcompananteLookupRepository personaMayorAcompananteRepository
    ) {
        this.actividadRepository = actividadRepository;
        this.participacionRepository = participacionRepository;
        this.usuarioLookupRepository = usuarioLookupRepository;
        this.personaMayorLookupRepository = personaMayorLookupRepository;
        this.acompananteLookupRepository = acompananteLookupRepository;
        this.personaMayorOrganizacionRepository = personaMayorOrganizacionRepository;
        this.personaMayorAcompananteRepository = personaMayorAcompananteRepository;
    }

    // =========================================================
    // LISTAR ACTIVIDADES SEGÚN EL ROL / RELACIÓN
    // =========================================================

    @GetMapping
    public ResponseEntity<?> listar(@RequestHeader("X-User-Id") Integer idUsuario) {

        // ORGANIZACIÓN: solamente ve sus propias actividades
        Integer idOrganizacion = resolverIdOrganizacion(idUsuario);

        if (idOrganizacion != null) {
            return ResponseEntity.ok(
                    actividadRepository.findByIdOrganizacion(idOrganizacion)
                            .stream()
                            .map(this::aResponse)
                            .toList()
            );
        }

        // PERSONA MAYOR: solo actividades de organizaciones ACEPTADAS
        if (esPersonaMayor(idUsuario)) {

            Set<Integer> idsOrganizaciones = organizacionesAceptadasDe(idUsuario);

            return ResponseEntity.ok(
                    actividadesDe(idsOrganizaciones)
                            .stream()
                            .map(this::aResponse)
                            .toList()
            );
        }

        // ACOMPAÑANTE: actividades de las organizaciones de las
        // personas mayores que acompaña (relación ACEPTADA)
        if (esAcompanante(idUsuario)) {

            List<PersonaMayorAcompananteLookup> relacionesAcompanante =
                    personaMayorAcompananteRepository
                            .findById_IdAcompananteAndEstado(idUsuario, "ACEPTADA");

            Set<Integer> idsOrganizaciones = new HashSet<>();

            for (PersonaMayorAcompananteLookup relacion : relacionesAcompanante) {
                idsOrganizaciones.addAll(
                        organizacionesAceptadasDe(relacion.getId().getIdPersonaMayor())
                );
            }

            return ResponseEntity.ok(
                    actividadesDe(idsOrganizaciones)
                            .stream()
                            .map(this::aResponse)
                            .toList()
            );
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("No tienes permisos para consultar actividades");
    }

    // =========================================================
    // ACTIVIDADES PROPIAS DE LA ORGANIZACIÓN
    // =========================================================

    @GetMapping("/mias")
    public ResponseEntity<?> listarMias(@RequestHeader("X-User-Id") Integer idUsuario) {

        Integer idOrganizacion = resolverIdOrganizacion(idUsuario);

        if (idOrganizacion == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Solo una organización tiene actividades propias");
        }

        return ResponseEntity.ok(
                actividadRepository.findByIdOrganizacion(idOrganizacion)
                        .stream()
                        .map(this::aResponse)
                        .toList()
        );
    }

    // =========================================================
    // ACTIVIDADES DISPONIBLES PARA PERSONA MAYOR
    // =========================================================

    @GetMapping("/disponibles")
    public ResponseEntity<?> listarDisponibles(@RequestHeader("X-User-Id") Integer idPersonaMayor) {

        if (!esPersonaMayor(idPersonaMayor)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Solo una persona mayor puede ver esto");
        }

        Set<Integer> idsOrganizaciones = organizacionesAceptadasDe(idPersonaMayor);

        List<Actividad> actividades = actividadesDe(idsOrganizaciones);

        Set<Integer> idsInscritos =
                participacionRepository.findById_IdPersonaMayor(idPersonaMayor)
                        .stream()
                        .map(p -> p.getId().getIdActividad())
                        .collect(Collectors.toSet());

        List<ActividadDisponibleResponse> respuesta = actividades.stream()
                .map(a -> new ActividadDisponibleResponse(
                        a.getIdActividad(),
                        a.getNombre(),
                        a.getDescripcion(),
                        a.getFecha(),
                        a.getHora(),
                        a.getLugar(),
                        a.getTipo(),
                        a.getCupos(),
                        idsInscritos.contains(a.getIdActividad())
                ))
                .toList();

        return ResponseEntity.ok(respuesta);
    }

    // =========================================================
    // PARTICIPANTES DE UNA ACTIVIDAD - SOLO ORGANIZACIÓN PROPIETARIA
    // =========================================================

    @GetMapping("/{id}/participantes")
    public ResponseEntity<?> listarParticipantes(
            @PathVariable Integer id,
            @RequestHeader("X-User-Id") Integer idUsuario
    ) {

        Integer idOrganizacion = resolverIdOrganizacion(idUsuario);

        if (idOrganizacion == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Solo una organización puede consultar participantes");
        }

        Actividad actividad = actividadRepository.findById(id).orElse(null);

        if (actividad == null) {
            return ResponseEntity.notFound().build();
        }

        if (!idOrganizacion.equals(actividad.getIdOrganizacion())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Esta actividad no pertenece a tu organización");
        }

        List<ParticipanteActividadResponse> participantes =
                participacionRepository.findById_IdActividad(id)
                        .stream()
                        .map(p -> {
                            Integer idPersonaMayor = p.getId().getIdPersonaMayor();
                            UsuarioLookup usuario =
                                    usuarioLookupRepository.findById(idPersonaMayor).orElse(null);

                            return new ParticipanteActividadResponse(
                                    idPersonaMayor,
                                    usuario != null ? usuario.getNombreUsuario() : null,
                                    usuario != null ? usuario.getCelular() : null,
                                    p.getAsistio()
                            );
                        })
                        .toList();

        return ResponseEntity.ok(participantes);
    }

    // =========================================================
    // REGISTRAR ASISTENCIA - SOLO ORGANIZACIÓN PROPIETARIA
    // =========================================================

    @PutMapping("/{id}/participantes/{idPersonaMayor}/asistencia")
    public ResponseEntity<?> registrarAsistencia(
            @PathVariable Integer id,
            @PathVariable Integer idPersonaMayor,
            @RequestBody AsistenciaRequest request,
            @RequestHeader("X-User-Id") Integer idUsuario
    ) {

        Integer idOrganizacion = resolverIdOrganizacion(idUsuario);

        if (idOrganizacion == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Solo una organización puede registrar asistencia");
        }

        Actividad actividad = actividadRepository.findById(id).orElse(null);

        if (actividad == null) {
            return ResponseEntity.notFound().build();
        }

        if (!idOrganizacion.equals(actividad.getIdOrganizacion())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Esta actividad no pertenece a tu organización");
        }

        Participacion participacion =
                participacionRepository
                        .findById_IdPersonaMayorAndId_IdActividad(idPersonaMayor, id)
                        .orElse(null);

        if (participacion == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("La persona mayor no está inscrita en esta actividad");
        }

        participacion.setAsistio(request.getAsistio());
        participacionRepository.save(participacion);

        return ResponseEntity.ok().build();
    }

    // =========================================================
    // INSCRIBIR PERSONA MAYOR
    // =========================================================

    @PostMapping("/{id}/inscribirse")
    public ResponseEntity<?> inscribirse(
            @PathVariable Integer id,
            @RequestHeader("X-User-Id") Integer idPersonaMayor
    ) {

        if (!esPersonaMayor(idPersonaMayor)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Solo una persona mayor puede inscribirse");
        }

        Actividad actividad = actividadRepository.findById(id).orElse(null);

        if (actividad == null) {
            return ResponseEntity.notFound().build();
        }

        boolean asociada = organizacionesAceptadasDe(idPersonaMayor)
                .contains(actividad.getIdOrganizacion());

        if (!asociada) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No puedes inscribirte en actividades de una organización a la que no estás asociado");
        }

        boolean yaInscrito =
                participacionRepository
                        .findById_IdPersonaMayorAndId_IdActividad(idPersonaMayor, id)
                        .isPresent();

        if (yaInscrito) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Ya estás inscrito en esta actividad");
        }

        if (actividad.getCupos() != null) {

            long participantesActuales =
                    participacionRepository.findById_IdActividad(id).size();

            if (participantesActuales >= actividad.getCupos()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("La actividad ya alcanzó el límite de cupos");
            }
        }

        participacionRepository.save(new Participacion(idPersonaMayor, id));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // =========================================================
    // CANCELAR INSCRIPCIÓN
    // =========================================================

    @DeleteMapping("/{id}/inscribirse")
    public ResponseEntity<?> cancelarInscripcion(
            @PathVariable Integer id,
            @RequestHeader("X-User-Id") Integer idPersonaMayor
    ) {

        if (!esPersonaMayor(idPersonaMayor)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Solo una persona mayor puede cancelar su inscripción");
        }

        Participacion participacion =
                participacionRepository
                        .findById_IdPersonaMayorAndId_IdActividad(idPersonaMayor, id)
                        .orElse(null);

        if (participacion == null) {
            return ResponseEntity.notFound().build();
        }

        participacionRepository.delete(participacion);

        return ResponseEntity.noContent().build();
    }

    // =========================================================
    // CREAR ACTIVIDAD - SOLO ORGANIZACIÓN
    // =========================================================

    @PostMapping
    public ResponseEntity<?> crear(
            @RequestBody ActividadRequest request,
            @RequestHeader("X-User-Id") Integer idUsuario
    ) {

        Integer idOrganizacion = resolverIdOrganizacion(idUsuario);

        if (idOrganizacion == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Solo una organización puede crear actividades");
        }

        if (request.getNombre() == null || request.getNombre().isBlank()) {
            return ResponseEntity.badRequest().body("El nombre es obligatorio");
        }

        if (request.getFecha() != null && request.getFecha().isBefore(LocalDate.now())) {
            return ResponseEntity.badRequest()
                    .body("No se puede crear una actividad con una fecha anterior a hoy");
        }

        if (request.getCupos() != null && request.getCupos() <= 0) {
            return ResponseEntity.badRequest().body("Los cupos deben ser mayores a 0");
        }

        Actividad actividad = new Actividad();
        actividad.setIdOrganizacion(idOrganizacion);
        actividad.setNombre(request.getNombre());
        actividad.setDescripcion(request.getDescripcion());
        actividad.setFecha(request.getFecha());
        actividad.setHora(request.getHora());
        actividad.setLugar(request.getLugar());
        actividad.setTipo(request.getTipo());
        actividad.setCupos(request.getCupos());
        actividad.setResponsable(request.getResponsable());

        actividad = actividadRepository.save(actividad);

        return ResponseEntity.status(HttpStatus.CREATED).body(aResponse(actividad));
    }

    // =========================================================
    // ACTUALIZAR ACTIVIDAD - SOLO ORGANIZACIÓN PROPIETARIA
    // =========================================================

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Integer id,
            @RequestBody ActividadRequest request,
            @RequestHeader("X-User-Id") Integer idUsuario
    ) {

        Integer idOrganizacion = resolverIdOrganizacion(idUsuario);

        Actividad actividad = actividadRepository.findById(id).orElse(null);

        if (actividad == null) {
            return ResponseEntity.notFound().build();
        }

        if (idOrganizacion == null || !idOrganizacion.equals(actividad.getIdOrganizacion())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Esta actividad no pertenece a tu organización");
        }

        if (request.getNombre() == null || request.getNombre().isBlank()) {
            return ResponseEntity.badRequest().body("El nombre es obligatorio");
        }

        if (request.getCupos() != null && request.getCupos() <= 0) {
            return ResponseEntity.badRequest().body("Los cupos deben ser mayores a 0");
        }

        actividad.setNombre(request.getNombre());
        actividad.setDescripcion(request.getDescripcion());
        actividad.setFecha(request.getFecha());
        actividad.setHora(request.getHora());
        actividad.setLugar(request.getLugar());
        actividad.setTipo(request.getTipo());
        actividad.setCupos(request.getCupos());
        actividad.setResponsable(request.getResponsable());

        actividad = actividadRepository.save(actividad);

        return ResponseEntity.ok(aResponse(actividad));
    }

    // =========================================================
    // ELIMINAR ACTIVIDAD - SOLO ORGANIZACIÓN PROPIETARIA
    // =========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(
            @PathVariable Integer id,
            @RequestHeader("X-User-Id") Integer idUsuario
    ) {

        Integer idOrganizacion = resolverIdOrganizacion(idUsuario);

        Actividad actividad = actividadRepository.findById(id).orElse(null);

        if (actividad == null) {
            return ResponseEntity.notFound().build();
        }

        if (idOrganizacion == null || !idOrganizacion.equals(actividad.getIdOrganizacion())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Esta actividad no pertenece a tu organización");
        }

        actividadRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private Integer resolverIdOrganizacion(Integer idUsuario) {
        return usuarioLookupRepository.findById(idUsuario)
                .map(UsuarioLookup::getIdOrganizacion)
                .orElse(null);
    }

    private boolean esPersonaMayor(Integer idUsuario) {
        return personaMayorLookupRepository.existsById(idUsuario);
    }

    private boolean esAcompanante(Integer idUsuario) {
        return acompananteLookupRepository.existsById(idUsuario);
    }

    private Set<Integer> organizacionesAceptadasDe(Integer idPersonaMayor) {
        return personaMayorOrganizacionRepository
                .findById_IdPersonaMayorAndEstado(idPersonaMayor, "ACEPTADA")
                .stream()
                .map(relacion -> relacion.getId().getIdOrganizacion())
                .collect(Collectors.toSet());
    }

    private List<Actividad> actividadesDe(Set<Integer> idsOrganizaciones) {
        return idsOrganizaciones.stream()
                .flatMap(idOrganizacion -> actividadRepository.findByIdOrganizacion(idOrganizacion).stream())
                .toList();
    }

    private ActividadResponse aResponse(Actividad a) {
        return new ActividadResponse(
                a.getIdActividad(),
                a.getIdOrganizacion(),
                a.getNombre(),
                a.getDescripcion(),
                a.getFecha(),
                a.getHora(),
                a.getLugar(),
                a.getTipo(),
                a.getCupos(),
                a.getResponsable()
        );
    }
}
