package com.proyectogrado.backend.controller;

import com.proyectogrado.backend.dto.AcompanantePerfilResponse;
import com.proyectogrado.backend.dto.PersonaMayorResponse;
import com.proyectogrado.backend.dto.ActualizarAcompananteRequest;
import com.proyectogrado.backend.dto.CambiarContrasenaRequest;

import com.proyectogrado.backend.model.PersonaMayorAcompanante;
import com.proyectogrado.backend.model.Usuario;

import com.proyectogrado.backend.repository.PersonaMayorAcompananteRepository;
import com.proyectogrado.backend.repository.UsuarioRepository;

import com.proyectogrado.backend.security.JwtService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/acompanante")
public class AcompananteController {

    private final PersonaMayorAcompananteRepository relacionRepository;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AcompananteController(
            PersonaMayorAcompananteRepository relacionRepository,
            JwtService jwtService,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.relacionRepository = relacionRepository;
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================================================
    // INFORMACIÓN DEL ACOMPAÑANTE
    // =========================================================

    @GetMapping("/informacion")
    public ResponseEntity<AcompanantePerfilResponse> obtenerInformacion(
            @RequestHeader("Authorization") String authorizationHeader
    ) {

        String token = authorizationHeader.substring(7);

        Integer idUsuario = jwtService.extraerIdUsuario(token);

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() ->
                        new RuntimeException("Usuario no encontrado")
                );

        boolean tieneContrasena =
                usuario.getContrasenaHash() != null
                        && !usuario.getContrasenaHash().isBlank();

        AcompanantePerfilResponse respuesta =
                new AcompanantePerfilResponse(
                        usuario.getIdUsuario(),
                        usuario.getNombreUsuario(),
                        usuario.getTelefono(),
                        usuario.getCorreo(),
                        tieneContrasena
                );

        return ResponseEntity.ok(respuesta);
    }

    // =========================================================
    // ACTUALIZAR INFORMACIÓN DEL ACOMPAÑANTE
    // =========================================================

    @PutMapping("/informacion")
    public ResponseEntity<AcompanantePerfilResponse> actualizarInformacion(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody ActualizarAcompananteRequest request
    ) {

        String token = authorizationHeader.substring(7);

        Integer idUsuario = jwtService.extraerIdUsuario(token);

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() ->
                        new RuntimeException("Usuario no encontrado")
                );

        if (request.getNombre() == null
                || request.getNombre().isBlank()) {

            return ResponseEntity.badRequest().build();
        }

        usuario.setNombreUsuario(
                request.getNombre().trim()
        );

        usuario.setCorreo(
                request.getCorreo() == null
                        || request.getCorreo().isBlank()
                        ? null
                        : request.getCorreo().trim()
        );

        usuarioRepository.save(usuario);

        boolean tieneContrasena =
                usuario.getContrasenaHash() != null
                        && !usuario.getContrasenaHash().isBlank();

        AcompanantePerfilResponse respuesta =
                new AcompanantePerfilResponse(
                        usuario.getIdUsuario(),
                        usuario.getNombreUsuario(),
                        usuario.getTelefono(),
                        usuario.getCorreo(),
                        tieneContrasena
                );

        return ResponseEntity.ok(respuesta);
    }

    // =========================================================
    // CAMBIAR / AGREGAR CONTRASEÑA
    // =========================================================

    @PutMapping("/contrasena")
    public ResponseEntity<String> cambiarContrasena(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody CambiarContrasenaRequest request
    ) {

        String token = authorizationHeader.substring(7);

        Integer idUsuario = jwtService.extraerIdUsuario(token);

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() ->
                        new RuntimeException("Usuario no encontrado")
                );

        String nuevaContrasena =
                request.getNuevaContrasena();

        if (nuevaContrasena == null
                || nuevaContrasena.isBlank()) {

            return ResponseEntity.badRequest()
                    .body("La nueva contraseña es obligatoria");
        }

        if (nuevaContrasena.length() < 6) {

            return ResponseEntity.badRequest()
                    .body(
                            "La contraseña debe tener mínimo 6 caracteres"
                    );
        }

        String contrasenaActual =
                request.getContrasenaActual();

        boolean tieneContrasena =
                usuario.getContrasenaHash() != null
                        && !usuario.getContrasenaHash().isBlank();

        // -----------------------------------------------------
        // Si ya tenía contraseña, debe proporcionar la actual
        // -----------------------------------------------------

        if (tieneContrasena) {

            if (contrasenaActual == null
                    || contrasenaActual.isBlank()) {

                return ResponseEntity.badRequest()
                        .body(
                                "Debes ingresar tu contraseña actual"
                        );
            }

            if (!passwordEncoder.matches(
                    contrasenaActual,
                    usuario.getContrasenaHash()
            )) {

                return ResponseEntity.status(401)
                        .body(
                                "La contraseña actual es incorrecta"
                        );
            }
        }

        // -----------------------------------------------------
        // Guardar nueva contraseña
        // -----------------------------------------------------

        usuario.setContrasenaHash(
                passwordEncoder.encode(nuevaContrasena)
        );

        usuarioRepository.save(usuario);

        return ResponseEntity.ok(
                "Contraseña guardada correctamente"
        );
    }

    // =========================================================
    // PERSONAS MAYORES ACEPTADAS
    // =========================================================

    @GetMapping("/personas-mayores")
    public ResponseEntity<List<PersonaMayorResponse>> obtenerPersonasMayores(
            @RequestHeader("Authorization") String authorizationHeader
    ) {

        String token = authorizationHeader.substring(7);

        Integer idAcompanante =
                jwtService.extraerIdUsuario(token);

        List<PersonaMayorAcompanante> relaciones =
                relacionRepository.findById_IdAcompananteAndEstado(
                        idAcompanante,
                        "ACEPTADA"
                );

        List<PersonaMayorResponse> respuesta =
                relaciones.stream()
                        .map(PersonaMayorAcompanante::getPersonaMayor)
                        .map(personaMayor -> {

                            boolean tieneContrasena =
                                    personaMayor.getUsuario()
                                            .getContrasenaHash() != null
                                            &&
                                            !personaMayor.getUsuario()
                                                    .getContrasenaHash()
                                                    .isBlank();

                            return new PersonaMayorResponse(
                                    personaMayor.getIdUsuario(),
                                    personaMayor.getUsuario()
                                            .getNombreUsuario(),
                                    personaMayor.getUsuario()
                                            .getTelefono(),
                                    personaMayor.getUsuario()
                                            .getCorreo(),
                                    personaMayor.getFechaNacimiento(),
                                    personaMayor.getGenero(),
                                    personaMayor.getDireccion(),
                                    tieneContrasena
                            );
                        })
                        .toList();

        return ResponseEntity.ok(respuesta);
    }

    // =========================================================
    // SOLICITUDES PENDIENTES
    // =========================================================

    @GetMapping("/personas-mayores/solicitudes")
    public ResponseEntity<List<PersonaMayorResponse>> obtenerSolicitudesPendientes(
            @RequestHeader("Authorization") String authorizationHeader
    ) {

        String token = authorizationHeader.substring(7);

        Integer idAcompanante =
                jwtService.extraerIdUsuario(token);

        List<PersonaMayorAcompanante> relaciones =
                relacionRepository.findById_IdAcompananteAndEstado(
                        idAcompanante,
                        "PENDIENTE"
                );

        List<PersonaMayorResponse> respuesta =
                relaciones.stream()
                        .map(PersonaMayorAcompanante::getPersonaMayor)
                        .map(personaMayor -> {

                            boolean tieneContrasena =
                                    personaMayor.getUsuario()
                                            .getContrasenaHash() != null
                                            &&
                                            !personaMayor.getUsuario()
                                                    .getContrasenaHash()
                                                    .isBlank();

                            return new PersonaMayorResponse(
                                    personaMayor.getIdUsuario(),
                                    personaMayor.getUsuario()
                                            .getNombreUsuario(),
                                    personaMayor.getUsuario()
                                            .getTelefono(),
                                    personaMayor.getUsuario()
                                            .getCorreo(),
                                    personaMayor.getFechaNacimiento(),
                                    personaMayor.getGenero(),
                                    personaMayor.getDireccion(),
                                    tieneContrasena
                            );
                        })
                        .toList();

        return ResponseEntity.ok(respuesta);
    }

    // =========================================================
    // ACEPTAR SOLICITUD
    // =========================================================

    @PutMapping(
            "/personas-mayores/solicitudes/{idPersonaMayor}/aceptar"
    )
    public ResponseEntity<String> aceptarSolicitud(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Integer idPersonaMayor
    ) {

        String token = authorizationHeader.substring(7);

        Integer idAcompanante =
                jwtService.extraerIdUsuario(token);

        List<PersonaMayorAcompanante> relaciones =
                relacionRepository.findById_IdAcompanante(
                        idAcompanante
                );

        PersonaMayorAcompanante relacion =
                relaciones.stream()
                        .filter(r ->
                                r.getPersonaMayor()
                                        .getIdUsuario()
                                        .equals(idPersonaMayor)
                        )
                        .findFirst()
                        .orElse(null);

        if (relacion == null) {
            return ResponseEntity.notFound().build();
        }

        if (!"PENDIENTE".equals(relacion.getEstado())) {
            return ResponseEntity.badRequest()
                    .body(
                            "Esta solicitud ya fue procesada"
                    );
        }

        relacion.setEstado("ACEPTADA");

        relacionRepository.save(relacion);

        return ResponseEntity.ok(
                "Solicitud de acompañamiento aceptada"
        );
    }

    // =========================================================
    // RECHAZAR SOLICITUD
    // =========================================================

    @PutMapping(
            "/personas-mayores/solicitudes/{idPersonaMayor}/rechazar"
    )
    public ResponseEntity<String> rechazarSolicitud(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Integer idPersonaMayor
    ) {

        String token = authorizationHeader.substring(7);

        Integer idAcompanante =
                jwtService.extraerIdUsuario(token);

        List<PersonaMayorAcompanante> relaciones =
                relacionRepository.findById_IdAcompanante(
                        idAcompanante
                );

        PersonaMayorAcompanante relacion =
                relaciones.stream()
                        .filter(r ->
                                r.getPersonaMayor()
                                        .getIdUsuario()
                                        .equals(idPersonaMayor)
                        )
                        .findFirst()
                        .orElse(null);

        if (relacion == null) {
            return ResponseEntity.notFound().build();
        }

        if (!"PENDIENTE".equals(relacion.getEstado())) {
            return ResponseEntity.badRequest()
                    .body(
                            "Esta solicitud ya fue procesada"
                    );
        }

        relacion.setEstado("RECHAZADA");

        relacionRepository.save(relacion);

        return ResponseEntity.ok(
                "Solicitud de acompañamiento rechazada"
        );
    }
}