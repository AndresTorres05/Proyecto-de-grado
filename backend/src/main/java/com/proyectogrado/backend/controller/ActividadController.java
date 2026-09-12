package com.proyectogrado.backend.controller;

import com.proyectogrado.backend.dto.ActividadDisponibleResponse;
import com.proyectogrado.backend.dto.ActividadRequest;
import com.proyectogrado.backend.dto.ActividadResponse;
import com.proyectogrado.backend.dto.AsistenciaRequest;
import com.proyectogrado.backend.dto.ParticipanteActividadResponse;

import com.proyectogrado.backend.model.Acompanante;
import com.proyectogrado.backend.model.Actividad;
import com.proyectogrado.backend.model.Participacion;
import com.proyectogrado.backend.model.PersonaMayor;
import com.proyectogrado.backend.model.PersonaMayorAcompanante;
import com.proyectogrado.backend.model.PersonaMayorOrganizacion;
import com.proyectogrado.backend.model.Usuario;

import com.proyectogrado.backend.repository.AcompananteRepository;
import com.proyectogrado.backend.repository.ActividadRepository;
import com.proyectogrado.backend.repository.ParticipacionRepository;
import com.proyectogrado.backend.repository.PersonaMayorAcompananteRepository;
import com.proyectogrado.backend.repository.PersonaMayorOrganizacionRepository;
import com.proyectogrado.backend.repository.PersonaMayorRepository;
import com.proyectogrado.backend.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/actividades")
@CrossOrigin(origins = "http://localhost:4200")
public class ActividadController {

    private final ActividadRepository actividadRepository;
    private final UsuarioRepository usuarioRepository;
    private final PersonaMayorRepository personaMayorRepository;
    private final ParticipacionRepository participacionRepository;

    // NUEVOS REPOSITORIOS
    private final PersonaMayorOrganizacionRepository personaMayorOrganizacionRepository;
    private final PersonaMayorAcompananteRepository personaMayorAcompananteRepository;
    private final AcompananteRepository acompananteRepository;

    public ActividadController(
            ActividadRepository actividadRepository,
            UsuarioRepository usuarioRepository,
            PersonaMayorRepository personaMayorRepository,
            ParticipacionRepository participacionRepository,
            PersonaMayorOrganizacionRepository personaMayorOrganizacionRepository,
            PersonaMayorAcompananteRepository personaMayorAcompananteRepository,
            AcompananteRepository acompananteRepository
    ) {
        this.actividadRepository = actividadRepository;
        this.usuarioRepository = usuarioRepository;
        this.personaMayorRepository = personaMayorRepository;
        this.participacionRepository = participacionRepository;
        this.personaMayorOrganizacionRepository = personaMayorOrganizacionRepository;
        this.personaMayorAcompananteRepository = personaMayorAcompananteRepository;
        this.acompananteRepository = acompananteRepository;
    }

    // =========================================================
    // LISTAR ACTIVIDADES SEGÚN EL ROL / RELACIÓN
    // =========================================================

    @GetMapping
    public ResponseEntity<?> listar(Authentication authentication) {

        Usuario usuario = usuarioActual(authentication);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Usuario no autenticado");
        }

        // =====================================================
        // ORGANIZACIÓN
        // Una organización solamente ve sus propias actividades
        // =====================================================

        if (usuario.getIdOrganizacion() != null) {

            List<ActividadResponse> actividades =
                    actividadRepository
                            .findByIdOrganizacion(usuario.getIdOrganizacion())
                            .stream()
                            .map(this::aResponse)
                            .toList();

            return ResponseEntity.ok(actividades);
        }

        // =====================================================
        // PERSONA MAYOR
        // Solo actividades de organizaciones ACEPTADAS
        // =====================================================

        PersonaMayor personaMayor =
                personaMayorRepository
                        .findById(usuario.getIdUsuario())
                        .orElse(null);

        if (personaMayor != null) {

            Set<Integer> idsOrganizaciones =
                    personaMayorOrganizacionRepository
                            .findById_IdPersonaMayorAndEstado(
                                    personaMayor.getIdUsuario(),
                                    "ACEPTADA"
                            )
                            .stream()
                            .map(relacion ->
                                    relacion.getId().getIdOrganizacion()
                            )
                            .collect(Collectors.toSet());

            List<ActividadResponse> actividades =
                    idsOrganizaciones.stream()
                            .flatMap(idOrganizacion ->
                                    actividadRepository
                                            .findByIdOrganizacion(idOrganizacion)
                                            .stream()
                            )
                            .map(this::aResponse)
                            .toList();

            return ResponseEntity.ok(actividades);
        }

        // =====================================================
        // ACOMPAÑANTE
        // Solo actividades de las organizaciones de las
        // personas mayores que acompaña y que están ACEPTADAS
        // =====================================================

        Acompanante acompanante =
                acompananteRepository
                        .findById(usuario.getIdUsuario())
                        .orElse(null);

        if (acompanante != null) {

            // Personas mayores acompañadas por este acompañante
            List<PersonaMayorAcompanante> relacionesAcompanante =
                    personaMayorAcompananteRepository
                            .findById_IdAcompananteAndEstado(
                                    acompanante.getIdUsuario(),
                                    "ACEPTADA"
                            );

            // Obtener las organizaciones asociadas a esas
            // personas mayores
            Set<Integer> idsOrganizaciones = new HashSet<>();

            for (PersonaMayorAcompanante relacionAcompanante
                    : relacionesAcompanante) {

                Integer idPersonaMayor =
                        relacionAcompanante
                                .getPersonaMayor()
                                .getIdUsuario();

                List<PersonaMayorOrganizacion> relacionesOrganizacion =
                        personaMayorOrganizacionRepository
                                .findById_IdPersonaMayorAndEstado(
                                        idPersonaMayor,
                                        "ACEPTADA"
                                );

                for (PersonaMayorOrganizacion relacionOrganizacion
                        : relacionesOrganizacion) {

                    idsOrganizaciones.add(
                            relacionOrganizacion
                                    .getId()
                                    .getIdOrganizacion()
                    );
                }
            }

            List<ActividadResponse> actividades =
                    idsOrganizaciones.stream()
                            .flatMap(idOrganizacion ->
                                    actividadRepository
                                            .findByIdOrganizacion(idOrganizacion)
                                            .stream()
                            )
                            .map(this::aResponse)
                            .toList();

            return ResponseEntity.ok(actividades);
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("No tienes permisos para consultar actividades");
    }


    // =========================================================
    // ACTIVIDADES PROPIAS DE LA ORGANIZACIÓN
    // =========================================================

    @GetMapping("/mias")
    public ResponseEntity<?> listarMias(Authentication authentication) {

        Usuario usuario = usuarioActual(authentication);

        if (usuario == null || usuario.getIdOrganizacion() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Solo una organización tiene actividades propias");
        }

        List<ActividadResponse> actividades =
                actividadRepository
                        .findByIdOrganizacion(usuario.getIdOrganizacion())
                        .stream()
                        .map(this::aResponse)
                        .toList();

        return ResponseEntity.ok(actividades);
    }


    // =========================================================
    // ACTIVIDADES DISPONIBLES PARA PERSONA MAYOR
    // =========================================================

    @GetMapping("/disponibles")
    public ResponseEntity<?> listarDisponibles(
            Authentication authentication
    ) {

        PersonaMayor personaMayor =
                personaMayorActual(authentication);

        if (personaMayor == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Solo una persona mayor puede ver esto");
        }

        // =====================================================
        // ORGANIZACIONES A LAS QUE ESTÁ ASOCIADA
        // =====================================================

        Set<Integer> idsOrganizaciones =
                personaMayorOrganizacionRepository
                        .findById_IdPersonaMayorAndEstado(
                                personaMayor.getIdUsuario(),
                                "ACEPTADA"
                        )
                        .stream()
                        .map(relacion ->
                                relacion.getId().getIdOrganizacion()
                        )
                        .collect(Collectors.toSet());

        // =====================================================
        // ACTIVIDADES DE ESAS ORGANIZACIONES
        // =====================================================

        List<Actividad> actividades =
                idsOrganizaciones.stream()
                        .flatMap(idOrganizacion ->
                                actividadRepository
                                        .findByIdOrganizacion(idOrganizacion)
                                        .stream()
                        )
                        .toList();

        // =====================================================
        // ACTIVIDADES EN LAS QUE YA ESTÁ INSCRITA
        // =====================================================

        Set<Integer> idsInscritos =
                participacionRepository
                        .findById_IdPersonaMayor(
                                personaMayor.getIdUsuario()
                        )
                        .stream()
                        .map(p ->
                                p.getId().getIdActividad()
                        )
                        .collect(Collectors.toSet());

        // =====================================================
        // CONSTRUIR RESPUESTA
        // =====================================================

        List<ActividadDisponibleResponse> respuesta =
                actividades.stream()
                        .map(a ->
                                new ActividadDisponibleResponse(
                                        a.getIdActividad(),
                                        a.getNombre(),
                                        a.getDescripcion(),
                                        a.getFecha(),
                                        a.getHora(),
                                        a.getLugar(),
                                        a.getTipo(),
                                        a.getCupos(),
                                        idsInscritos.contains(
                                                a.getIdActividad()
                                        )
                                )
                        )
                        .toList();

        return ResponseEntity.ok(respuesta);
    }


    // =========================================================
    // PARTICIPANTES DE UNA ACTIVIDAD
    // SOLO ORGANIZACIÓN PROPIETARIA
    // =========================================================

    @GetMapping("/{id}/participantes")
    public ResponseEntity<?> listarParticipantes(
            @PathVariable Integer id,
            Authentication authentication
    ) {

        Usuario usuario = usuarioActual(authentication);

        if (usuario == null || usuario.getIdOrganizacion() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Solo una organización puede consultar participantes");
        }

        Actividad actividad =
                actividadRepository.findById(id).orElse(null);

        if (actividad == null) {
            return ResponseEntity.notFound().build();
        }

        if (!usuario.getIdOrganizacion()
                .equals(actividad.getIdOrganizacion())) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Esta actividad no pertenece a tu organización");
        }

        List<ParticipanteActividadResponse> participantes =
                participacionRepository
                        .findById_IdActividad(id)
                        .stream()
                        .map(p ->
                                new ParticipanteActividadResponse(
                                        p.getPersonaMayor().getIdUsuario(),
                                        p.getPersonaMayor()
                                                .getUsuario()
                                                .getNombreUsuario(),
                                        p.getPersonaMayor()
                                                .getUsuario()
                                                .getTelefono(),
                                        p.getAsistio()
                                )
                        )
                        .toList();

        return ResponseEntity.ok(participantes);
    }


    // =========================================================
    // REGISTRAR ASISTENCIA
    // SOLO ORGANIZACIÓN PROPIETARIA
    // =========================================================

    @PutMapping("/{id}/participantes/{idPersonaMayor}/asistencia")
    public ResponseEntity<?> registrarAsistencia(
            @PathVariable Integer id,
            @PathVariable Integer idPersonaMayor,
            @RequestBody AsistenciaRequest request,
            Authentication authentication
    ) {

        Usuario usuario = usuarioActual(authentication);

        if (usuario == null || usuario.getIdOrganizacion() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Solo una organización puede registrar asistencia");
        }

        Actividad actividad =
                actividadRepository.findById(id).orElse(null);

        if (actividad == null) {
            return ResponseEntity.notFound().build();
        }

        if (!usuario.getIdOrganizacion()
                .equals(actividad.getIdOrganizacion())) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Esta actividad no pertenece a tu organización");
        }

        Participacion participacion =
                participacionRepository
                        .findById_IdPersonaMayorAndId_IdActividad(
                                idPersonaMayor,
                                id
                        )
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
            Authentication authentication
    ) {

        PersonaMayor personaMayor =
                personaMayorActual(authentication);

        if (personaMayor == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Solo una persona mayor puede inscribirse");
        }

        Actividad actividad =
                actividadRepository.findById(id).orElse(null);

        if (actividad == null) {
            return ResponseEntity.notFound().build();
        }

        // =====================================================
        // VERIFICAR QUE LA PERSONA MAYOR PERTENEZCA A LA
        // ORGANIZACIÓN DE LA ACTIVIDAD
        // =====================================================

        boolean asociada =
                personaMayorOrganizacionRepository
                        .findById_IdPersonaMayorAndEstado(
                                personaMayor.getIdUsuario(),
                                "ACEPTADA"
                        )
                        .stream()
                        .anyMatch(relacion ->
                                relacion.getId()
                                        .getIdOrganizacion()
                                        .equals(
                                                actividad.getIdOrganizacion()
                                        )
                        );

        if (!asociada) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(
                            "No puedes inscribirte en actividades de una organización a la que no estás asociado"
                    );
        }

        // =====================================================
        // VERIFICAR SI YA ESTÁ INSCRITA
        // =====================================================

        boolean yaInscrito =
                participacionRepository
                        .findById_IdPersonaMayorAndId_IdActividad(
                                personaMayor.getIdUsuario(),
                                id
                        )
                        .isPresent();

        if (yaInscrito) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Ya estás inscrito en esta actividad");
        }

        // =====================================================
        // VERIFICAR CUPOS
        // =====================================================

        if (actividad.getCupos() != null) {

            long participantesActuales =
                    participacionRepository
                            .findById_IdActividad(id)
                            .size();

            if (participantesActuales >= actividad.getCupos()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("La actividad ya alcanzó el límite de cupos");
            }
        }

        // =====================================================
        // CREAR INSCRIPCIÓN
        // =====================================================

        participacionRepository.save(
                new Participacion(personaMayor, actividad)
        );

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    // =========================================================
    // CANCELAR INSCRIPCIÓN
    // =========================================================

    @DeleteMapping("/{id}/inscribirse")
    public ResponseEntity<?> cancelarInscripcion(
            @PathVariable Integer id,
            Authentication authentication
    ) {

        PersonaMayor personaMayor =
                personaMayorActual(authentication);

        if (personaMayor == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(
                            "Solo una persona mayor puede cancelar su inscripción"
                    );
        }

        Participacion participacion =
                participacionRepository
                        .findById_IdPersonaMayorAndId_IdActividad(
                                personaMayor.getIdUsuario(),
                                id
                        )
                        .orElse(null);

        if (participacion == null) {
            return ResponseEntity.notFound().build();
        }

        participacionRepository.delete(participacion);

        return ResponseEntity.noContent().build();
    }


    // =========================================================
    // CREAR ACTIVIDAD
    // SOLO ORGANIZACIÓN
    // =========================================================

    @PostMapping
    public ResponseEntity<?> crear(
            @RequestBody ActividadRequest request,
            Authentication authentication
    ) {

        Usuario usuario = usuarioActual(authentication);

        if (usuario == null || usuario.getIdOrganizacion() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Solo una organización puede crear actividades");
        }

        if (request.getNombre() == null
                || request.getNombre().isBlank()) {

            return ResponseEntity.badRequest()
                    .body("El nombre es obligatorio");
        }

        if (request.getFecha() != null
                && request.getFecha().isBefore(LocalDate.now())) {

            return ResponseEntity.badRequest()
                    .body(
                            "No se puede crear una actividad con una fecha anterior a hoy"
                    );
        }

        if (request.getCupos() != null
                && request.getCupos() <= 0) {

            return ResponseEntity.badRequest()
                    .body("Los cupos deben ser mayores a 0");
        }

        Actividad actividad = new Actividad();

        actividad.setIdOrganizacion(
                usuario.getIdOrganizacion()
        );

        actividad.setNombre(request.getNombre());
        actividad.setDescripcion(request.getDescripcion());
        actividad.setFecha(request.getFecha());
        actividad.setHora(request.getHora());
        actividad.setLugar(request.getLugar());
        actividad.setTipo(request.getTipo());
        actividad.setCupos(request.getCupos());
        actividad.setResponsable(request.getResponsable());

        actividad = actividadRepository.save(actividad);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(aResponse(actividad));
    }


    // =========================================================
    // ACTUALIZAR ACTIVIDAD
    // SOLO ORGANIZACIÓN PROPIETARIA
    // =========================================================

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Integer id,
            @RequestBody ActividadRequest request,
            Authentication authentication
    ) {

        Usuario usuario = usuarioActual(authentication);

        Actividad actividad =
                actividadRepository.findById(id).orElse(null);

        if (actividad == null) {
            return ResponseEntity.notFound().build();
        }

        if (usuario == null
                || usuario.getIdOrganizacion() == null
                || !usuario.getIdOrganizacion()
                        .equals(actividad.getIdOrganizacion())) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(
                            "Esta actividad no pertenece a tu organización"
                    );
        }

        if (request.getNombre() == null
                || request.getNombre().isBlank()) {

            return ResponseEntity.badRequest()
                    .body("El nombre es obligatorio");
        }

        if (request.getCupos() != null
                && request.getCupos() <= 0) {

            return ResponseEntity.badRequest()
                    .body("Los cupos deben ser mayores a 0");
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
    // ELIMINAR ACTIVIDAD
    // SOLO ORGANIZACIÓN PROPIETARIA
    // =========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(
            @PathVariable Integer id,
            Authentication authentication
    ) {

        Usuario usuario = usuarioActual(authentication);

        Actividad actividad =
                actividadRepository.findById(id).orElse(null);

        if (actividad == null) {
            return ResponseEntity.notFound().build();
        }

        if (usuario == null
                || usuario.getIdOrganizacion() == null
                || !usuario.getIdOrganizacion()
                        .equals(actividad.getIdOrganizacion())) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(
                            "Esta actividad no pertenece a tu organización"
                    );
        }

        actividadRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }


    // =========================================================
    // OBTENER USUARIO ACTUAL
    // =========================================================

    private Usuario usuarioActual(
            Authentication authentication
    ) {

        if (authentication == null) {
            return null;
        }

        String identificador = authentication.getName();

        return usuarioRepository
                .findByCorreo(identificador)
                .or(() ->
                        usuarioRepository
                                .findByTelefono(identificador)
                )
                .orElse(null);
    }


    // =========================================================
    // OBTENER PERSONA MAYOR ACTUAL
    // =========================================================

    private PersonaMayor personaMayorActual(
            Authentication authentication
    ) {

        Usuario usuario = usuarioActual(authentication);

        if (usuario == null) {
            return null;
        }

        return personaMayorRepository
                .findById(usuario.getIdUsuario())
                .orElse(null);
    }


    // =========================================================
    // CONVERTIR A RESPONSE
    // =========================================================

    private ActividadResponse aResponse(
            Actividad a
    ) {

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