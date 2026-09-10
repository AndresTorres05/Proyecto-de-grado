package com.proyectogrado.backend.controller;

import com.proyectogrado.backend.dto.EnviarOtpRequest;
import com.proyectogrado.backend.dto.LoginResponse;
import com.proyectogrado.backend.dto.OtpLoginRequest;
import com.proyectogrado.backend.dto.OtpRegistroRequest;
import com.proyectogrado.backend.model.PersonaMayor;
import com.proyectogrado.backend.model.Acompanante;
import com.proyectogrado.backend.model.Rol;
import com.proyectogrado.backend.model.Usuario;
import com.proyectogrado.backend.model.UsuarioRol;
import com.proyectogrado.backend.repository.PersonaMayorRepository;
import com.proyectogrado.backend.repository.AcompananteRepository;
import com.proyectogrado.backend.repository.RolRepository;
import com.proyectogrado.backend.repository.UsuarioRepository;
import com.proyectogrado.backend.repository.UsuarioRolRepository;
import com.proyectogrado.backend.security.JwtService;
import com.proyectogrado.backend.security.TextBeeOtpService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/auth/otp")
@CrossOrigin(origins = "http://localhost:4200")
public class OtpController {

    private static final Set<String> ROLES_POR_TELEFONO =
            Set.of("PERSONA_MAYOR", "ACOMPANANTE");

    private final TextBeeOtpService textBeeOtpService;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final PersonaMayorRepository personaMayorRepository;
    private final AcompananteRepository acompananteRepository;
    private final JwtService jwtService;

    public OtpController(
            TextBeeOtpService textBeeOtpService,
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            UsuarioRolRepository usuarioRolRepository,
            PersonaMayorRepository personaMayorRepository,
            AcompananteRepository acompananteRepository,
            JwtService jwtService
    ) {
        this.textBeeOtpService = textBeeOtpService;
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.usuarioRolRepository = usuarioRolRepository;
        this.personaMayorRepository = personaMayorRepository;
        this.acompananteRepository = acompananteRepository;
        this.jwtService = jwtService;
    }

    // =========================================================
    // ENVIAR OTP
    // =========================================================

    @PostMapping("/enviar")
    public ResponseEntity<LoginResponse> enviar(
            @RequestBody EnviarOtpRequest request
    ) {
        try {

            textBeeOtpService.enviarCodigo(request.getTelefono());

            return ResponseEntity.ok(
                    new LoginResponse(
                            null,
                            null,
                            "Código enviado"
                    )
            );

        } catch (Exception e) {

            System.out.println("ERROR TEXTBEE: " + e.getMessage());
            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.BAD_GATEWAY)
                    .body(
                            new LoginResponse(
                                    null,
                                    null,
                                    "No se pudo enviar el código: "
                                            + e.getMessage()
                            )
                    );
        }
    }

    // =========================================================
    // LOGIN CON OTP
    // =========================================================

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody OtpLoginRequest request
    ) {

        boolean valido = textBeeOtpService.verificarCodigo(
                request.getTelefono(),
                request.getCodigo()
        );

        if (!valido) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            new LoginResponse(
                                    null,
                                    null,
                                    "Código incorrecto o expirado"
                            )
                    );
        }

        Usuario usuario = usuarioRepository
                .findByTelefono(request.getTelefono())
                .orElse(null);

        if (usuario == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            new LoginResponse(
                                    null,
                                    null,
                                    "No existe una cuenta con ese teléfono"
                            )
                    );
        }

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
    // REGISTRO CON OTP
    // =========================================================

    @PostMapping("/registro")
    public ResponseEntity<LoginResponse> registro(
            @RequestBody OtpRegistroRequest request
    ) {

        System.out.println("========== REGISTRO OTP ==========");
        System.out.println("Nombre: " + request.getNombreUsuario());
        System.out.println("Telefono: " + request.getTelefono());
        System.out.println("Rol: " + request.getRol());

        // -----------------------------------------------------
        // 1. Verificar que el rol pueda registrarse por teléfono
        // -----------------------------------------------------

        if (!ROLES_POR_TELEFONO.contains(request.getRol())) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            new LoginResponse(
                                    null,
                                    null,
                                    "Ese rol no se registra por teléfono"
                            )
                    );
        }

        // -----------------------------------------------------
        // 2. Verificar código OTP
        // -----------------------------------------------------

        boolean valido = textBeeOtpService.verificarCodigo(
                request.getTelefono(),
                request.getCodigo()
        );

        if (!valido) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            new LoginResponse(
                                    null,
                                    null,
                                    "Código incorrecto o expirado"
                            )
                    );
        }

        // -----------------------------------------------------
        // 3. Verificar teléfono existente
        // -----------------------------------------------------

        if (usuarioRepository.existsByTelefono(
                request.getTelefono()
        )) {

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

        // -----------------------------------------------------
        // 4. Buscar rol
        // -----------------------------------------------------

        Rol rol = rolRepository
                .findByNombre(request.getRol())
                .orElseThrow(
                        () -> new RuntimeException(
                                "Rol no configurado en el sistema"
                        )
                );

        // -----------------------------------------------------
        // 5. Crear usuario
        // -----------------------------------------------------

        Usuario usuario = new Usuario();

usuario.setNombreUsuario(request.getNombreUsuario());
usuario.setTelefono(request.getTelefono());

if ("PERSONA_MAYOR".equals(rol.getNombre())
        && request.getCorreo() != null
        && !request.getCorreo().isBlank()) {

    if (usuarioRepository.existsByCorreo(request.getCorreo())) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new LoginResponse(
                        null,
                        null,
                        "Ese correo ya está registrado"
                ));
    }

    usuario.setCorreo(request.getCorreo().trim());
}

usuario = usuarioRepository.saveAndFlush(usuario);

        System.out.println(
                "Usuario creado con ID: "
                        + usuario.getIdUsuario()
        );

        // -----------------------------------------------------
        // 6. Asignar rol
        // -----------------------------------------------------

        UsuarioRol usuarioRol =
                new UsuarioRol(usuario, rol);

        usuarioRolRepository.saveAndFlush(usuarioRol);

        System.out.println(
                "Rol asignado: "
                        + rol.getNombre()
        );

        // -----------------------------------------------------
        // 7. Crear registro específico según el rol
        // -----------------------------------------------------

       if ("PERSONA_MAYOR".equals(rol.getNombre())) {

    PersonaMayor personaMayor = new PersonaMayor(usuario);

    if (request.getFechaNacimiento() != null && !request.getFechaNacimiento().isBlank()) {
        personaMayor.setFechaNacimiento(java.time.LocalDate.parse(request.getFechaNacimiento()));
    }
    personaMayor.setGenero(request.getGenero());
    personaMayor.setDireccion(request.getDireccion());

    personaMayorRepository.save(personaMayor);

} else if ("ACOMPANANTE".equals(rol.getNombre())) {

    Acompanante acompanante = new Acompanante(usuario, null);

    acompananteRepository.save(acompanante);
}

        // -----------------------------------------------------
        // 8. Generar JWT
        // -----------------------------------------------------

        String token = jwtService.generarToken(
                usuario.getIdUsuario(),
                rol.getNombre()
        );

        // -----------------------------------------------------
        // 9. Responder al frontend
        // -----------------------------------------------------

        System.out.println(
                "Registro terminado correctamente"
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new LoginResponse(
                                token,
                                rol.getNombre(),
                                "Cuenta creada exitosamente",
                                usuario.getIdUsuario(),
                                usuario.getNombreUsuario()
                        )
                );
    }

    // =========================================================
    // OBTENER ROL PRINCIPAL
    // =========================================================

    private String obtenerRolPrincipal(Usuario usuario) {

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