package com.proyectogrado.backend.controller;

import com.proyectogrado.backend.dto.EnviarOtpRequest;
import com.proyectogrado.backend.dto.LoginResponse;
import com.proyectogrado.backend.dto.OtpLoginRequest;
import com.proyectogrado.backend.dto.OtpRegistroRequest;
import com.proyectogrado.backend.model.PersonaMayor;
import com.proyectogrado.backend.model.Rol;
import com.proyectogrado.backend.model.Usuario;
import com.proyectogrado.backend.model.UsuarioRol;
import com.proyectogrado.backend.repository.PersonaMayorRepository;
import com.proyectogrado.backend.repository.RolRepository;
import com.proyectogrado.backend.repository.UsuarioRepository;
import com.proyectogrado.backend.repository.UsuarioRolRepository;
import com.proyectogrado.backend.security.JwtService;
import com.proyectogrado.backend.security.TwilioOtpService;
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

    private final TwilioOtpService twilioOtpService;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final PersonaMayorRepository personaMayorRepository;
    private final JwtService jwtService;

    public OtpController(
            TwilioOtpService twilioOtpService,
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            UsuarioRolRepository usuarioRolRepository,
            PersonaMayorRepository personaMayorRepository,
            JwtService jwtService
    ) {
        this.twilioOtpService = twilioOtpService;
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.usuarioRolRepository = usuarioRolRepository;
        this.personaMayorRepository = personaMayorRepository;
        this.jwtService = jwtService;
    }

    @PostMapping("/enviar")
    public ResponseEntity<LoginResponse> enviar(
            @RequestBody EnviarOtpRequest request
    ) {
        try {
            twilioOtpService.enviarCodigo(request.getTelefono());

            return ResponseEntity.ok(
                    new LoginResponse(null, null, "Código enviado")
            );

        } catch (Exception e) {

            System.out.println("ERROR TWILIO: " + e.getMessage());
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(
                            new LoginResponse(
                                    null,
                                    null,
                                    "No se pudo enviar el código: " + e.getMessage()
                            )
                    );
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody OtpLoginRequest request
    ) {

        boolean valido = twilioOtpService.verificarCodigo(
                request.getTelefono(),
                request.getCodigo()
        );

        if (!valido) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
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
                        usuario.getIdUsuario()
                )
        );
    }

    @PostMapping("/registro")
    public ResponseEntity<LoginResponse> registro(
            @RequestBody OtpRegistroRequest request
    ) {

        if (!ROLES_POR_TELEFONO.contains(request.getRol())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(
                            new LoginResponse(
                                    null,
                                    null,
                                    "Ese rol no se registra por teléfono"
                            )
                    );
        }

        boolean valido = twilioOtpService.verificarCodigo(
                request.getTelefono(),
                request.getCodigo()
        );

        if (!valido) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(
                            new LoginResponse(
                                    null,
                                    null,
                                    "Código incorrecto o expirado"
                            )
                    );
        }

        if (usuarioRepository.existsByTelefono(request.getTelefono())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(
                            new LoginResponse(
                                    null,
                                    null,
                                    "Ese teléfono ya está registrado"
                            )
                    );
        }

        Rol rol = rolRepository
                .findByNombre(request.getRol())
                .orElseThrow(
                        () -> new RuntimeException(
                                "Rol no configurado en el sistema"
                        )
                );

        Usuario usuario = new Usuario();

        usuario.setNombreUsuario(request.getNombreUsuario());
        usuario.setTelefono(request.getTelefono());

        usuario = usuarioRepository.save(usuario);

        usuarioRolRepository.save(
                new UsuarioRol(usuario, rol)
        );

        if ("PERSONA_MAYOR".equals(rol.getNombre())) {
            personaMayorRepository.save(
                    new PersonaMayor(usuario)
            );
        }

        String token = jwtService.generarToken(
                usuario.getIdUsuario(),
                rol.getNombre()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new LoginResponse(
                                token,
                                rol.getNombre(),
                                "Cuenta creada exitosamente",
                                usuario.getIdUsuario()
                        )
                );
    }

    private String obtenerRolPrincipal(Usuario usuario) {

        List<UsuarioRol> roles =
                usuarioRolRepository.findByUsuario_IdUsuario(
                        usuario.getIdUsuario()
                );

        if (roles.isEmpty()) {
            throw new RuntimeException(
                    "El usuario no tiene un rol asignado"
            );
        }

        return roles.get(0).getRol().getNombre();
    }
}