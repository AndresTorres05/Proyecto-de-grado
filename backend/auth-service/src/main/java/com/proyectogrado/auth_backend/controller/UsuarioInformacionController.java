package com.proyectogrado.auth_backend.controller;

import com.proyectogrado.auth_backend.dto.ActualizarInformacionRequest;
import com.proyectogrado.auth_backend.dto.InformacionUsuarioResponse;
import com.proyectogrado.auth_backend.model.PersonaMayor;
import com.proyectogrado.auth_backend.model.Usuario;
import com.proyectogrado.auth_backend.repository.PersonaMayorRepository;
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

    // Largo máximo del nombre de la EPS / IPS
    private static final int MAX_SALUD = 120;

    private final UsuarioRepository usuarioRepository;
    private final PersonaMayorRepository personaMayorRepository;
    private final JwtService jwtService;
    private final EmailValidationService emailValidationService;

    public UsuarioInformacionController(
            UsuarioRepository usuarioRepository,
            PersonaMayorRepository personaMayorRepository,
            JwtService jwtService,
            EmailValidationService emailValidationService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.personaMayorRepository = personaMayorRepository;
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

        String eps = textoOpcional(request.getEps());
        String ips = textoOpcional(request.getIps());

        if ((eps != null && eps.length() > MAX_SALUD) || (ips != null && ips.length() > MAX_SALUD)) {
            return ResponseEntity.badRequest()
                    .body("El nombre de la EPS o IPS no puede tener más de " + MAX_SALUD + " caracteres");
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

        // EPS e IPS viven en la tabla persona_mayor: solo se guardan si el
        // usuario es persona mayor (para los demás roles se ignoran).
        personaMayorRepository.findById(usuario.getIdUsuario()).ifPresent(personaMayor -> {
            personaMayor.setEps(eps);
            personaMayor.setIps(ips);
            personaMayorRepository.save(personaMayor);
        });

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

        PersonaMayor personaMayor =
                personaMayorRepository.findById(usuario.getIdUsuario()).orElse(null);

        return new InformacionUsuarioResponse(
                usuario.getIdUsuario(),
                usuario.getNombreUsuario(),
                usuario.getCelular(),
                usuario.getCorreo(),
                usuario.getFechaNacimiento(),
                usuario.getGenero(),
                usuario.getDireccion(),
                personaMayor != null ? personaMayor.getEps() : null,
                personaMayor != null ? personaMayor.getIps() : null,
                tieneContrasena
        );
    }

    private static String textoOpcional(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}