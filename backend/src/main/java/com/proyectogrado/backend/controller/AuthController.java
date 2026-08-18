package com.proyectogrado.backend.controller;

import com.proyectogrado.backend.dto.LoginRequest;
import com.proyectogrado.backend.dto.LoginResponse;
import com.proyectogrado.backend.dto.RegistroRequest;
import com.proyectogrado.backend.model.Organizacion;
import com.proyectogrado.backend.model.PersonaMayor;
import com.proyectogrado.backend.model.Rol;
import com.proyectogrado.backend.model.Usuario;
import com.proyectogrado.backend.model.UsuarioRol;
import com.proyectogrado.backend.repository.OrganizacionRepository;
import com.proyectogrado.backend.repository.PersonaMayorRepository;
import com.proyectogrado.backend.repository.RolRepository;
import com.proyectogrado.backend.repository.UsuarioRepository;
import com.proyectogrado.backend.repository.UsuarioRolRepository;
import com.proyectogrado.backend.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
                           UsuarioRepository usuarioRepository,
                           RolRepository rolRepository,
                           UsuarioRolRepository usuarioRolRepository,
                           OrganizacionRepository organizacionRepository,
                           PersonaMayorRepository personaMayorRepository,
                           JwtService jwtService,
                           PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.usuarioRolRepository = usuarioRolRepository;
        this.organizacionRepository = organizacionRepository;
        this.personaMayorRepository = personaMayorRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getCorreo(),
                            request.getContrasena()
                    )
            );
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(null, null, "Correo o contraseña incorrectos"));
        }

        Usuario usuario = usuarioRepository.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String rolPrincipal = obtenerRolPrincipal(usuario);
        String token = jwtService.generarToken(usuario.getCorreo(), rolPrincipal);

        return ResponseEntity.ok(
                new LoginResponse(token, rolPrincipal, "Inicio de sesión exitoso", usuario.getIdUsuario())
        );
    }

    @PostMapping("/registro")
    public ResponseEntity<LoginResponse> registro(@RequestBody RegistroRequest request) {

        if (usuarioRepository.existsByCorreo(request.getCorreo())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new LoginResponse(null, null, "Ese correo ya está registrado"));
        }

        Rol rol = rolRepository.findByNombre(request.getRol()).orElse(null);
        if (rol == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new LoginResponse(null, null, "Rol inválido"));
        }

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(request.getNombreUsuario());
        usuario.setCorreo(request.getCorreo());
        usuario.setContrasenaHash(passwordEncoder.encode(request.getContrasena()));
        usuario = usuarioRepository.save(usuario);

        usuarioRolRepository.save(new UsuarioRol(usuario, rol));

        if ("ORGANIZACION".equals(rol.getNombre())) {
            Organizacion organizacion = new Organizacion();
            organizacion.setNombre(usuario.getNombreUsuario());
            organizacion.setCorreo(usuario.getCorreo());
            organizacion = organizacionRepository.save(organizacion);

            usuario.setIdOrganizacion(organizacion.getIdOrganizacion());
            usuario = usuarioRepository.save(usuario);
        } else if ("PERSONA_MAYOR".equals(rol.getNombre())) {
            personaMayorRepository.save(new PersonaMayor(usuario));
        }

        String token = jwtService.generarToken(usuario.getCorreo(), rol.getNombre());

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new LoginResponse(token, rol.getNombre(), "Cuenta creada exitosamente", usuario.getIdUsuario())
        );
    }

    private String obtenerRolPrincipal(Usuario usuario) {
        List<UsuarioRol> roles = usuarioRolRepository.findByUsuario_IdUsuario(usuario.getIdUsuario());
        if (roles.isEmpty()) {
            throw new RuntimeException("El usuario no tiene un rol asignado");
        }
        return roles.get(0).getRol().getNombre();
    }
}