package com.proyectogrado.backend.controller;

import com.proyectogrado.backend.model.Organizacion;
import com.proyectogrado.backend.model.PersonaMayor;
import com.proyectogrado.backend.model.PersonaMayorOrganizacion;
import com.proyectogrado.backend.model.PersonaMayorOrganizacionId;
import com.proyectogrado.backend.model.Usuario;
import com.proyectogrado.backend.repository.OrganizacionRepository;
import com.proyectogrado.backend.repository.PersonaMayorOrganizacionRepository;
import com.proyectogrado.backend.repository.PersonaMayorRepository;
import com.proyectogrado.backend.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class PersonaMayorOrganizacionController {

    private final PersonaMayorOrganizacionRepository relacionRepository;
    private final PersonaMayorRepository personaMayorRepository;
    private final OrganizacionRepository organizacionRepository;
    private final UsuarioRepository usuarioRepository;

    public PersonaMayorOrganizacionController(
            PersonaMayorOrganizacionRepository relacionRepository,
            PersonaMayorRepository personaMayorRepository,
            OrganizacionRepository organizacionRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.relacionRepository = relacionRepository;
        this.personaMayorRepository = personaMayorRepository;
        this.organizacionRepository = organizacionRepository;
        this.usuarioRepository = usuarioRepository;
    }


    // =========================================================
    // ORGANIZACIÓN
    // OBTENER PERSONAS MAYORES ASOCIADAS
    // =========================================================

    @GetMapping("/api/organizacion/personas-mayores")
    public ResponseEntity<?> obtenerPersonasMayoresOrganizacion() {

        try {

            Usuario usuario = obtenerUsuarioAutenticado();

            if (usuario == null) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("Usuario no autenticado");
            }

            Integer idOrganizacion = usuario.getIdOrganizacion();

            if (idOrganizacion == null) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(
                                "El usuario no tiene una organización asociada"
                        );
            }

            List<PersonaMayorOrganizacion> relaciones =
                    relacionRepository
                            .findById_IdOrganizacionAndEstado(
                                    idOrganizacion,
                                    "ACEPTADA"
                            );

            List<PersonaMayorResponse> respuesta =
                    relaciones.stream()
                            .map(PersonaMayorOrganizacion::getPersonaMayor)
                            .map(personaMayor ->
                                    new PersonaMayorResponse(
                                            personaMayor.getIdUsuario(),
                                            personaMayor.getUsuario()
                                                    .getNombreUsuario(),
                                            personaMayor.getUsuario()
                                                    .getTelefono()
                                    )
                            )
                            .toList();

            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            "Error al obtener las personas mayores"
                    );
        }
    }

    // =========================================================
// ORGANIZACIÓN
// CANCELAR ASOCIACIÓN CON PERSONA MAYOR
// =========================================================

@DeleteMapping("/api/organizacion/personas-mayores/{idPersonaMayor}")
public ResponseEntity<?> cancelarAsociacionPersonaMayor(
        @PathVariable Integer idPersonaMayor
) {

    try {

        Usuario usuario = obtenerUsuarioAutenticado();

        if (usuario == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Usuario no autenticado");
        }

        Integer idOrganizacion = usuario.getIdOrganizacion();

        if (idOrganizacion == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("El usuario no tiene una organización asociada");
        }

        // =====================================================
        // BUSCAR LA RELACIÓN
        // =====================================================

        PersonaMayorOrganizacionId idRelacion =
                new PersonaMayorOrganizacionId(
                        idPersonaMayor,
                        idOrganizacion
                );

        PersonaMayorOrganizacion relacion =
                relacionRepository
                        .findById(idRelacion)
                        .orElse(null);

        if (relacion == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("No existe una asociación con esta persona mayor");
        }

        // =====================================================
        // ELIMINAR SOLO LA RELACIÓN
        // NO SE ELIMINA NINGÚN PERFIL
        // =====================================================

        relacionRepository.delete(relacion);
        relacionRepository.flush();

        return ResponseEntity.ok(
                "Asociación cancelada correctamente"
        );

    } catch (Exception e) {

        e.printStackTrace();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al cancelar la asociación");
    }
}


    // =========================================================
    // ORGANIZACIÓN
    // ENVIAR SOLICITUD DE ASOCIACIÓN
    // =========================================================

    @PostMapping("/api/organizacion/personas-mayores")
    public ResponseEntity<?> asociarPersonaMayor(
            @RequestBody AsociarPersonaMayorRequest request
    ) {

        try {

            Usuario usuario = obtenerUsuarioAutenticado();

            if (usuario == null) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("Usuario no autenticado");
            }

            Integer idOrganizacion = usuario.getIdOrganizacion();

            if (idOrganizacion == null) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(
                                "El usuario no tiene una organización asociada"
                        );
            }

            Organizacion organizacion =
                    organizacionRepository
                            .findById(idOrganizacion)
                            .orElse(null);

            if (organizacion == null) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("Organización no encontrada");
            }


            // =====================================================
            // BUSCAR USUARIO POR TELÉFONO
            // =====================================================

            Usuario usuarioPersonaMayor =
                    usuarioRepository
                            .findByTelefono(request.telefono())
                            .orElse(null);

            if (usuarioPersonaMayor == null) {
                return ResponseEntity
                        .badRequest()
                        .body(
                                "No existe un usuario registrado con ese teléfono"
                        );
            }


            // =====================================================
            // VERIFICAR QUE SEA PERSONA MAYOR
            // =====================================================

            PersonaMayor personaMayor =
                    personaMayorRepository
                            .findById(
                                    usuarioPersonaMayor.getIdUsuario()
                            )
                            .orElse(null);

            if (personaMayor == null) {
                return ResponseEntity
                        .badRequest()
                        .body(
                                "El usuario existe, pero no está registrado como persona mayor"
                        );
            }


            // =====================================================
            // VERIFICAR RELACIÓN EXISTENTE
            // =====================================================

            PersonaMayorOrganizacionId idRelacion =
                    new PersonaMayorOrganizacionId(
                            personaMayor.getIdUsuario(),
                            organizacion.getIdOrganizacion()
                    );

            PersonaMayorOrganizacion relacionExistente =
                    relacionRepository
                            .findById(idRelacion)
                            .orElse(null);

            if (relacionExistente != null) {

                if ("ACEPTADA".equals(
                        relacionExistente.getEstado()
                )) {

                    return ResponseEntity
                            .badRequest()
                            .body(
                                    "Esta persona mayor ya está asociada a la organización"
                            );
                }

                if ("PENDIENTE".equals(
                        relacionExistente.getEstado()
                )) {

                    return ResponseEntity
                            .badRequest()
                            .body(
                                    "Ya existe una solicitud pendiente para esta persona mayor"
                            );
                }

                // Si estaba RECHAZADA,
                // permitimos enviar una nueva solicitud.

                relacionExistente.setEstado("PENDIENTE");

                relacionRepository.saveAndFlush(
                        relacionExistente
                );

                return ResponseEntity.ok(
                        "Solicitud de asociación enviada correctamente"
                );
            }


            // =====================================================
            // CREAR NUEVA SOLICITUD
            // =====================================================

            PersonaMayorOrganizacion relacion =
                    new PersonaMayorOrganizacion(
                            personaMayor,
                            organizacion
                    );

            relacion.setEstado("PENDIENTE");

            relacionRepository.saveAndFlush(relacion);

            return ResponseEntity.ok(
                    "Solicitud de asociación enviada correctamente"
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            "Error al enviar la solicitud de asociación"
                    );
        }
    }


    // =========================================================
    // PERSONA MAYOR
    // OBTENER SOLICITUDES PENDIENTES
    // =========================================================

    @GetMapping("/api/persona-mayor/organizaciones/solicitudes")
    public ResponseEntity<?> obtenerSolicitudesOrganizaciones() {

        try {

            Usuario usuario = obtenerUsuarioAutenticado();

            if (usuario == null) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("Usuario no autenticado");
            }

            Integer idPersonaMayor =
                    usuario.getIdUsuario();

            List<PersonaMayorOrganizacion> relaciones =
                    relacionRepository
                            .findById_IdPersonaMayorAndEstado(
                                    idPersonaMayor,
                                    "PENDIENTE"
                            );

            List<OrganizacionSolicitudResponse> respuesta =
                    relaciones.stream()
                            .map(relacion -> {

                                Integer idOrganizacion =
                                        relacion.getId()
                                                .getIdOrganizacion();

                                Organizacion organizacion =
                                        organizacionRepository
                                                .findById(idOrganizacion)
                                                .orElse(null);

                                List<Usuario> usuariosOrganizacion =
                                        usuarioRepository.findByIdOrganizacion(idOrganizacion);

                                Usuario usuarioOrganizacion =
                                        usuariosOrganizacion.isEmpty()
                                                ? null
                                                : usuariosOrganizacion.get(0);

                                return new OrganizacionSolicitudResponse(
                                        idOrganizacion,

                                        usuarioOrganizacion != null
                                                ? usuarioOrganizacion
                                                        .getNombreUsuario()
                                                : "Organización",

                                        usuarioOrganizacion != null
                                                ? usuarioOrganizacion
                                                        .getTelefono()
                                                : null,

                                        usuarioOrganizacion != null
                                                ? usuarioOrganizacion
                                                        .getCorreo()
                                                : null,

                                        organizacion != null
                                                ? organizacion.getDireccion()
                                                : null
                                );
                            })
                            .toList();

            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            "Error al obtener las solicitudes de organizaciones"
                    );
        }
    }


@GetMapping("/api/persona-mayor/organizaciones")
public ResponseEntity<?> obtenerOrganizacionesPersonaMayor() {

    try {

        Usuario usuario = obtenerUsuarioAutenticado();

        if (usuario == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Usuario no autenticado");
        }

        Integer idPersonaMayor = usuario.getIdUsuario();

        List<PersonaMayorOrganizacion> relaciones =
                relacionRepository
                        .findById_IdPersonaMayorAndEstado(
                                idPersonaMayor,
                                "ACEPTADA"
                        );

        List<OrganizacionSolicitudResponse> respuesta =
                relaciones.stream()
                        .map(relacion -> {

                            Integer idOrganizacion =
                                    relacion.getId()
                                            .getIdOrganizacion();

                            Organizacion organizacion =
                                    organizacionRepository
                                            .findById(idOrganizacion)
                                            .orElse(null);

                        List<Usuario> usuariosOrganizacion =
                                usuarioRepository.findByIdOrganizacion(idOrganizacion);

                        Usuario usuarioOrganizacion =
                                usuariosOrganizacion.isEmpty()
                                        ? null
                                        : usuariosOrganizacion.get(0);

                            return new OrganizacionSolicitudResponse(
                                    idOrganizacion,

                                    usuarioOrganizacion != null
                                            ? usuarioOrganizacion.getNombreUsuario()
                                            : "Organización",

                                    usuarioOrganizacion != null
                                            ? usuarioOrganizacion.getTelefono()
                                            : null,

                                    usuarioOrganizacion != null
                                            ? usuarioOrganizacion.getCorreo()
                                            : null,

                                    organizacion != null
                                            ? organizacion.getDireccion()
                                            : null
                            );
                        })
                        .toList();

        return ResponseEntity.ok(respuesta);

    } catch (Exception e) {

        e.printStackTrace();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al obtener las organizaciones");
    }
}


    // =========================================================
    // PERSONA MAYOR
    // ACEPTAR SOLICITUD
    // =========================================================

    @PutMapping(
            "/api/persona-mayor/organizaciones/solicitudes/{idOrganizacion}/aceptar"
    )
    public ResponseEntity<?> aceptarSolicitudOrganizacion(
            @PathVariable Integer idOrganizacion
    ) {

        try {

            Usuario usuario = obtenerUsuarioAutenticado();

            if (usuario == null) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("Usuario no autenticado");
            }

            Integer idPersonaMayor =
                    usuario.getIdUsuario();

            PersonaMayorOrganizacion relacion =
                    relacionRepository
                            .findById(
                                    new PersonaMayorOrganizacionId(
                                            idPersonaMayor,
                                            idOrganizacion
                                    )
                            )
                            .orElse(null);

            if (relacion == null) {

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(
                                "No se encontró la solicitud de esta organización"
                        );
            }

            if (!"PENDIENTE".equals(
                    relacion.getEstado()
            )) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                "Esta solicitud ya fue procesada"
                        );
            }

            relacion.setEstado("ACEPTADA");

            relacionRepository.saveAndFlush(relacion);

            return ResponseEntity.ok(
                    "Solicitud aceptada correctamente"
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            "Error al aceptar la solicitud"
                    );
        }
    }


    // =========================================================
    // PERSONA MAYOR
    // RECHAZAR SOLICITUD
    // =========================================================

    @PutMapping(
            "/api/persona-mayor/organizaciones/solicitudes/{idOrganizacion}/rechazar"
    )
    public ResponseEntity<?> rechazarSolicitudOrganizacion(
            @PathVariable Integer idOrganizacion
    ) {

        try {

            Usuario usuario = obtenerUsuarioAutenticado();

            if (usuario == null) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("Usuario no autenticado");
            }

            Integer idPersonaMayor =
                    usuario.getIdUsuario();

            PersonaMayorOrganizacion relacion =
                    relacionRepository
                            .findById(
                                    new PersonaMayorOrganizacionId(
                                            idPersonaMayor,
                                            idOrganizacion
                                    )
                            )
                            .orElse(null);

            if (relacion == null) {

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(
                                "No se encontró la solicitud de esta organización"
                        );
            }

            if (!"PENDIENTE".equals(
                    relacion.getEstado()
            )) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                "Esta solicitud ya fue procesada"
                        );
            }

            relacion.setEstado("RECHAZADA");

            relacionRepository.saveAndFlush(relacion);

            return ResponseEntity.ok(
                    "Solicitud rechazada correctamente"
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            "Error al rechazar la solicitud"
                    );
        }
    }


    // =========================================================
// PERSONA MAYOR
// CANCELAR ASOCIACIÓN CON ORGANIZACIÓN
// =========================================================

@DeleteMapping("/api/persona-mayor/organizaciones/{idOrganizacion}")
public ResponseEntity<?> cancelarAsociacionOrganizacion(
        @PathVariable Integer idOrganizacion
) {

    try {

        Usuario usuario = obtenerUsuarioAutenticado();

        if (usuario == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Usuario no autenticado");
        }

        Integer idPersonaMayor =
                usuario.getIdUsuario();

        PersonaMayorOrganizacionId idRelacion =
                new PersonaMayorOrganizacionId(
                        idPersonaMayor,
                        idOrganizacion
                );

        PersonaMayorOrganizacion relacion =
                relacionRepository
                        .findById(idRelacion)
                        .orElse(null);

        if (relacion == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            "No existe una asociación con esta organización"
                    );
        }

        if (!"ACEPTADA".equals(relacion.getEstado())) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "La asociación no está activa"
                    );
        }

        relacionRepository.delete(relacion);
        relacionRepository.flush();

        return ResponseEntity.ok(
                "Asociación cancelada correctamente"
        );

    } catch (Exception e) {

        e.printStackTrace();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        "Error al cancelar la asociación"
                );
    }
}


    // =========================================================
    // OBTENER USUARIO AUTENTICADO
    // =========================================================

    private Usuario obtenerUsuarioAutenticado() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            return null;
        }

        String identificador =
                authentication.getName();

        return usuarioRepository
                .findByCorreo(identificador)
                .or(() ->
                        usuarioRepository.findByTelefono(
                                identificador
                        )
                )
                .orElse(null);
    }


    // =========================================================
    // DTO - PERSONA MAYOR
    // =========================================================

    public record PersonaMayorResponse(
            Integer idUsuario,
            String nombre,
            String telefono
    ) {}


    // =========================================================
    // DTO - SOLICITUD DE ORGANIZACIÓN
    // =========================================================

    public record OrganizacionSolicitudResponse(
            Integer idOrganizacion,
            String nombre,
            String telefono,
            String correo,
            String direccion
    ) {}


    // =========================================================
    // REQUEST - ASOCIAR PERSONA MAYOR
    // =========================================================

    public record AsociarPersonaMayorRequest(
            String telefono
    ) {}
}