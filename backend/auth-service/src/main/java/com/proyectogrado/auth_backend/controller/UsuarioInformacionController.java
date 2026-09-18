package com.proyectogrado.auth_backend.controller;

import com.proyectogrado.auth_backend.dto.ActualizarInformacionRequest;
import com.proyectogrado.auth_backend.dto.InformacionUsuarioResponse;
import com.proyectogrado.auth_backend.model.Usuario;
import com.proyectogrado.auth_backend.repository.UsuarioRepository;
import com.proyectogrado.auth_backend.security.JwtService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Informacion de identidad (nombre/correo/telefono), valida para
 * cualquier rol: persona mayor, acompanante u organizacion. El telefono
 * NUNCA se edita aqui porque se usa para login por OTP.
 *
 * Sirve tanto para persona-mayor-service como para acompanante-service
 * y organizacion-service: todos apuntan aqui en vez de duplicar esta
 * logica, porque nombre/correo son de identidad, no de perfil de rol.
 */
@RestController
@RequestMapping("/api/auth/informacion")
public class UsuarioInformacionController {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    public UsuarioInformacionController(
            UsuarioRepository usuarioRepository,
            JwtService jwtService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
    }

    @GetMapping
    public ResponseEntity<InformacionUsuarioResponse> obtenerInformacion(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        Usuario usuario = usuarioDelToken(authorizationHeader);

        return ResponseEntity.ok(aRespuesta(usuario));
    }

    @PutMapping
    public ResponseEntity<?> actualizarInformacion(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody ActualizarInformacionRequest request
    ) {
        Usuario usuario = usuarioDelToken(authorizationHeader);

        if (request.getNombre() == null || request.getNombre().isBlank()) {
            return ResponseEntity.badRequest().body("El nombre es obligatorio");
        }

        usuario.setNombreUsuario(request.getNombre().trim());

        usuario.setCorreo(
                request.getCorreo() == null || request.getCorreo().isBlank()
                        ? null
                        : request.getCorreo().trim()
        );

        usuario = usuarioRepository.save(usuario);

        return ResponseEntity.ok(aRespuesta(usuario));
    }

    private Usuario usuarioDelToken(String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        Integer idUsuario = jwtService.extraerIdUsuario(token);

        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private InformacionUsuarioResponse aRespuesta(Usuario usuario) {
        boolean tieneContrasena = usuario.getContrasenaHash() != null
                && !usuario.getContrasenaHash().isBlank();

        return new InformacionUsuarioResponse(
                usuario.getIdUsuario(),
                usuario.getNombreUsuario(),
                usuario.getTelefono(),
                usuario.getCorreo(),
                tieneContrasena
        );
    }
}