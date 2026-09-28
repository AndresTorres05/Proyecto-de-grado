package com.proyectogrado.auth_backend.controller;

import com.proyectogrado.auth_backend.dto.ActualizarInformacionRequest;
import com.proyectogrado.auth_backend.dto.InformacionUsuarioResponse;
import com.proyectogrado.auth_backend.model.Usuario;
import com.proyectogrado.auth_backend.repository.UsuarioRepository;
import com.proyectogrado.auth_backend.security.JwtService;
import com.proyectogrado.auth_backend.service.EmailValidationService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Informacion de identidad (nombre/correo/celular), valida para
 * cualquier rol: persona mayor, acompanante u organizacion. El celular
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
    private final EmailValidationService emailValidationService;

    public UsuarioInformacionController(
            UsuarioRepository usuarioRepository,
            JwtService jwtService,
            EmailValidationService emailValidationService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
        this.emailValidationService = emailValidationService;
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

        String correo = request.getCorreo() == null || request.getCorreo().isBlank()
                ? null
                : request.getCorreo().trim().toLowerCase();

        // Solo se verifica si el correo cambio (evita gastar consultas
        // de Hunter cada vez que se edita solo el nombre).
        if (correo != null && !correo.equalsIgnoreCase(usuario.getCorreo())) {

            if (usuarioRepository.existsByCorreo(correo)) {
                return ResponseEntity.badRequest()
                        .body("Ya existe un usuario registrado con ese correo");
            }

            try {
                if (!emailValidationService.puedeRecibirCorreos(correo)) {
                    return ResponseEntity.badRequest().body(
                            "El correo electrónico no parece ser válido o no puede recibir correos. "
                                    + "Verifica que esté escrito correctamente."
                    );
                }
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body("No se pudo verificar el correo en este momento. Intenta de nuevo más tarde.");
            }
        }

usuario.setNombreUsuario(request.getNombre().trim());
usuario.setCorreo(correo);
usuario.setFechaNacimiento(request.getFechaNacimiento());
usuario.setGenero(request.getGenero());
usuario.setDireccion(request.getDireccion());

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
                usuario.getCelular(),
                usuario.getCorreo(),
                usuario.getFechaNacimiento(),
                usuario.getGenero(),
                usuario.getDireccion(),
                tieneContrasena
        );
    }
}