package com.proyectogrado.backend.controller;

import com.proyectogrado.backend.dto.LoginRequest;
import com.proyectogrado.backend.dto.LoginResponse;
import com.proyectogrado.backend.dto.RegistroRequest;
import com.proyectogrado.backend.model.Rol;
import com.proyectogrado.backend.model.Usuario;
import com.proyectogrado.backend.repository.UsuarioRepository;
import com.proyectogrado.backend.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
                           UsuarioRepository usuarioRepository,
                           JwtService jwtService,
                           PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
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
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(null, null, "Correo o contraseña incorrectos"));
        }

        Usuario usuario = usuarioRepository.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String token = jwtService.generarToken(usuario.getCorreo(), usuario.getRol().name());

        return ResponseEntity.ok(
                new LoginResponse(token, usuario.getRol().name(), "Inicio de sesión exitoso")
        );
    }

    @PostMapping("/registro")
    public ResponseEntity<LoginResponse> registro(@RequestBody RegistroRequest request) {

        if (usuarioRepository.existsByCorreo(request.getCorreo())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new LoginResponse(null, null, "Ese correo ya está registrado"));
        }

        Rol rolConvertido;
        try {
            rolConvertido = Rol.valueOf(request.getRol());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new LoginResponse(null, null, "Rol inválido"));
        }

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(request.getNombreUsuario());
        usuario.setCorreo(request.getCorreo());
        usuario.setContrasenaHash(passwordEncoder.encode(request.getContrasena()));
        usuario.setRol(rolConvertido);

        usuarioRepository.save(usuario);

        String token = jwtService.generarToken(usuario.getCorreo(), usuario.getRol().name());

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new LoginResponse(token, usuario.getRol().name(), "Cuenta creada exitosamente")
        );
    }
}