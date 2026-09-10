package com.proyectogrado.backend.controller;

import com.proyectogrado.backend.dto.LoginRequest;
import com.proyectogrado.backend.dto.LoginResponse;
import com.proyectogrado.backend.dto.RegistroRequest;
import com.proyectogrado.backend.model.Acompanante;
import com.proyectogrado.backend.model.Organizacion;
import com.proyectogrado.backend.model.PersonaMayor;
import com.proyectogrado.backend.model.Rol;
import com.proyectogrado.backend.model.Usuario;
import com.proyectogrado.backend.model.UsuarioRol;
import com.proyectogrado.backend.model.Voluntario;
import com.proyectogrado.backend.repository.AcompananteRepository;
import com.proyectogrado.backend.repository.OrganizacionRepository;
import com.proyectogrado.backend.repository.PersonaMayorRepository;
import com.proyectogrado.backend.repository.RolRepository;
import com.proyectogrado.backend.repository.UsuarioRepository;
import com.proyectogrado.backend.repository.UsuarioRolRepository;
import com.proyectogrado.backend.repository.VoluntarioRepository;
import com.proyectogrado.backend.security.JwtService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final OrganizacionRepository organizacionRepository;
    private final PersonaMayorRepository personaMayorRepository;
    private final AcompananteRepository acompananteRepository;
    private final VoluntarioRepository voluntarioRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            AuthenticationManager authenticationManager,
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            UsuarioRolRepository usuarioRolRepository,
            OrganizacionRepository organizacionRepository,
            PersonaMayorRepository personaMayorRepository,
            AcompananteRepository acompananteRepository,
            VoluntarioRepository voluntarioRepository,
            JwtService jwtService,
            PasswordEncoder passwordEncoder) {

        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.usuarioRolRepository = usuarioRolRepository;
        this.organizacionRepository = organizacionRepository;
        this.personaMayorRepository = personaMayorRepository;
        this.acompananteRepository = acompananteRepository;
        this.voluntarioRepository = voluntarioRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================================================
    // LOGIN CON CORREO + CONTRASEÑA
    // =========================================================

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request) {

        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getCorreo(),
                            request.getContrasena()
                    )
            );

        } catch (AuthenticationException e) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            new LoginResponse(
                                    null,
                                    null,
                                    "Correo o contraseña incorrectos"
                            )
                    );
        }

        Usuario usuario = usuarioRepository
                .findByCorreo(request.getCorreo())
                .orElseThrow(
                        () -> new RuntimeException(
                                "Usuario no encontrado"
                        )
                );

        String rolPrincipal = obtenerRolPrincipal(usuario);

        String token = jwtService.generarToken(
                usuario.getIdUsuario(),
                rolPrincipal
        );

        return ResponseEntity.ok(
                new LoginResponse(
                        token,
                        rolPrincipal,
                        "Inicio de sesión exitoso",
                        usuario.getIdUsuario(),
                        usuario.getNombreUsuario()
                )
        );
    }

    // =========================================================
    // REGISTRO CON CORREO + CONTRASEÑA
    // =========================================================

    @PostMapping("/registro")
    public ResponseEntity<LoginResponse> registro(
            @RequestBody RegistroRequest request) {

        // -----------------------------------------------------
        // 1. Buscar rol
        // -----------------------------------------------------

        Rol rol = rolRepository
                .findByNombre(request.getRol())
                .orElse(null);

        if (rol == null) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            new LoginResponse(
                                    null,
                                    null,
                                    "Rol inválido"
                            )
                    );
        }

        String nombreRol = rol.getNombre();

        // -----------------------------------------------------
        // 2. Validar correo y contraseña
        // -----------------------------------------------------

        boolean esPersonaMayor =
                "PERSONA_MAYOR".equals(nombreRol);

        boolean esAcompanante =
                "ACOMPANANTE".equals(nombreRol);

        boolean esOrganizacion =
                "ORGANIZACION".equals(nombreRol);

        boolean esVoluntario =
                "VOLUNTARIO".equals(nombreRol);

        boolean usaRegistroConContrasena =
                request.getContrasena() != null
                && !request.getContrasena().isBlank();

        // Si hay contraseña, el correo es obligatorio
        if (usaRegistroConContrasena) {

            if (request.getCorreo() == null
                    || request.getCorreo().isBlank()) {

                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(
                                new LoginResponse(
                                        null,
                                        null,
                                        "Para usar una contraseña debes ingresar un correo"
                                )
                        );
            }
        }

        // Organización y voluntario siempre necesitan correo
        if ((esOrganizacion || esVoluntario)
                && (request.getCorreo() == null
                || request.getCorreo().isBlank())) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            new LoginResponse(
                                    null,
                                    null,
                                    "El correo es obligatorio para este tipo de usuario"
                            )
                    );
        }

        // -----------------------------------------------------
        // 3. Verificar correo duplicado
        // -----------------------------------------------------

        if (request.getCorreo() != null
                && !request.getCorreo().isBlank()
                && usuarioRepository.existsByCorreo(
                        request.getCorreo().trim())) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(
                            new LoginResponse(
                                    null,
                                    null,
                                    "Ese correo ya está registrado"
                            )
                    );
        }

        // -----------------------------------------------------
        // 4. Teléfono obligatorio para Persona Mayor/Acompañante
        // -----------------------------------------------------

        if (esPersonaMayor || esAcompanante) {

            if (request.getTelefono() == null
                    || request.getTelefono().isBlank()) {

                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(
                                new LoginResponse(
                                        null,
                                        null,
                                        "El teléfono es obligatorio"
                                )
                        );
            }

            if (usuarioRepository.existsByTelefono(
                    request.getTelefono())) {

                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(
                                new LoginResponse(
                                        null,
                                        null,
                                        "Ese teléfono ya está registrado"
                                )
                        );
            }
        }

        // -----------------------------------------------------
        // 5. Crear usuario
        // -----------------------------------------------------

        Usuario usuario = new Usuario();

        usuario.setNombreUsuario(
                request.getNombreUsuario()
        );

        // Correo opcional para Persona Mayor/Acompañante
        if (request.getCorreo() != null
                && !request.getCorreo().isBlank()) {

            usuario.setCorreo(
                    request.getCorreo().trim()
            );
        }

        // Teléfono para Persona Mayor/Acompañante
        if (esPersonaMayor || esAcompanante) {

            usuario.setTelefono(
                    request.getTelefono()
            );
        }

        // Contraseña
        if (usaRegistroConContrasena) {
    usuario.setContrasenaHash(
            passwordEncoder.encode(request.getContrasena())
    );
}

        usuario = usuarioRepository.saveAndFlush(usuario);

        // -----------------------------------------------------
        // 6. Asignar rol
        // -----------------------------------------------------

        usuarioRolRepository.saveAndFlush(
                new UsuarioRol(usuario, rol)
        );

        // -----------------------------------------------------
        // 7. Crear registro específico
        // -----------------------------------------------------

        if (esPersonaMayor) {

            PersonaMayor personaMayor =
                    new PersonaMayor(usuario);

            if (request.getFechaNacimiento() != null
                    && !request.getFechaNacimiento().isBlank()) {

                personaMayor.setFechaNacimiento(
                        LocalDate.parse(
                                request.getFechaNacimiento()
                        )
                );
            }

            personaMayor.setGenero(
                    request.getGenero()
            );

            personaMayor.setDireccion(
                    request.getDireccion()
            );

            personaMayorRepository.save(personaMayor);

        } else if (esAcompanante) {

            Acompanante acompanante =
                    new Acompanante(usuario, null);

            acompananteRepository.save(acompanante);

        } else if (esOrganizacion) {

            Organizacion organizacion =
                    new Organizacion();

            organizacion.setDireccion(
                    request.getDireccion()
            );

            organizacion =
                    organizacionRepository.save(organizacion);

            usuario.setIdOrganizacion(
                    organizacion.getIdOrganizacion()
            );

            usuarioRepository.save(usuario);

        } else if (esVoluntario) {

            voluntarioRepository.save(
                    new Voluntario(
                            usuario,
                            request.getDisponibilidad()
                    )
            );
        }

        // -----------------------------------------------------
        // 8. Generar JWT
        // -----------------------------------------------------

        String token = jwtService.generarToken(
                usuario.getIdUsuario(),
                nombreRol
        );

        // -----------------------------------------------------
        // 9. Responder
        // -----------------------------------------------------

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new LoginResponse(
                                token,
                                nombreRol,
                                "Cuenta creada exitosamente",
                                usuario.getIdUsuario(),
                                usuario.getNombreUsuario()
                        )
                );
    }

    // =========================================================
    // OBTENER ROL PRINCIPAL
    // =========================================================

    private String obtenerRolPrincipal(
            Usuario usuario) {

        List<UsuarioRol> roles =
                usuarioRolRepository
                        .findByUsuario_IdUsuario(
                                usuario.getIdUsuario()
                        );

        if (roles.isEmpty()) {

            throw new RuntimeException(
                    "El usuario no tiene un rol asignado"
            );
        }

        return roles
                .get(0)
                .getRol()
                .getNombre();
    }
}