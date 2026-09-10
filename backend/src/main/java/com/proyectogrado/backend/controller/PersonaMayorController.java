package com.proyectogrado.backend.controller;

import com.proyectogrado.backend.dto.ActualizarPersonaMayorRequest;
import com.proyectogrado.backend.dto.CambiarContrasenaRequest;
import com.proyectogrado.backend.dto.PersonaMayorResponse;
import com.proyectogrado.backend.model.PersonaMayor;
import com.proyectogrado.backend.repository.PersonaMayorRepository;
import com.proyectogrado.backend.repository.UsuarioRepository;
import com.proyectogrado.backend.security.JwtService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/persona-mayor")
public class PersonaMayorController {

    private final PersonaMayorRepository personaMayorRepository;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public PersonaMayorController(
            PersonaMayorRepository personaMayorRepository,
            JwtService jwtService,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.personaMayorRepository = personaMayorRepository;
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================================================
    // OBTENER INFORMACIÓN
    // =========================================================

    @GetMapping("/informacion")
    public ResponseEntity<PersonaMayorResponse> obtenerInformacion(
            @RequestHeader("Authorization") String authorizationHeader
    ) {

        String token = authorizationHeader.substring(7);

        Integer idUsuario = jwtService.extraerIdUsuario(token);

        PersonaMayor personaMayor =
                personaMayorRepository.findById(idUsuario).orElse(null);

        if (personaMayor == null) {
            return ResponseEntity.notFound().build();
        }

        var usuario = personaMayor.getUsuario();

        boolean tieneContrasena =
                usuario.getContrasenaHash() != null
                        && !usuario.getContrasenaHash().isBlank();

        PersonaMayorResponse respuesta = new PersonaMayorResponse(
                personaMayor.getIdUsuario(),
                usuario.getNombreUsuario(),
                usuario.getTelefono(),
                usuario.getCorreo(),
                personaMayor.getFechaNacimiento(),
                personaMayor.getGenero(),
                personaMayor.getDireccion(),
                tieneContrasena
        );

        return ResponseEntity.ok(respuesta);
    }


    // =========================================================
    // ACTUALIZAR INFORMACIÓN PERSONAL
    // =========================================================

    @PutMapping("/informacion")
    public ResponseEntity<PersonaMayorResponse> actualizarInformacion(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody ActualizarPersonaMayorRequest request
    ) {

        String token = authorizationHeader.substring(7);

        Integer idUsuario = jwtService.extraerIdUsuario(token);

        PersonaMayor personaMayor =
                personaMayorRepository.findById(idUsuario).orElse(null);

        if (personaMayor == null) {
            return ResponseEntity.notFound().build();
        }

        var usuario = usuarioRepository.findById(idUsuario).orElse(null);

        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }

        // -----------------------------------------------------
        // Actualizar datos del usuario
        // -----------------------------------------------------

        usuario.setNombreUsuario(request.getNombre());
        usuario.setCorreo(request.getCorreo());

        /*
         * El teléfono NO se modifica aquí.
         *
         * El teléfono se utiliza para el inicio de sesión mediante
         * OTP, por lo que no queremos cambiarlo accidentalmente
         * desde el formulario de información personal.
         */

        usuarioRepository.save(usuario);


        // -----------------------------------------------------
        // Actualizar datos de persona mayor
        // -----------------------------------------------------

        personaMayor.setFechaNacimiento(
                request.getFechaNacimiento()
        );

        personaMayor.setGenero(
                request.getGenero()
        );

        personaMayor.setDireccion(
                request.getDireccion()
        );

        personaMayorRepository.save(personaMayor);


        // -----------------------------------------------------
        // Saber si tiene contraseña
        // -----------------------------------------------------

        boolean tieneContrasena =
                usuario.getContrasenaHash() != null
                        && !usuario.getContrasenaHash().isBlank();


        // -----------------------------------------------------
        // Crear respuesta
        // -----------------------------------------------------

        PersonaMayorResponse respuesta = new PersonaMayorResponse(
                personaMayor.getIdUsuario(),
                usuario.getNombreUsuario(),
                usuario.getTelefono(),
                usuario.getCorreo(),
                personaMayor.getFechaNacimiento(),
                personaMayor.getGenero(),
                personaMayor.getDireccion(),
                tieneContrasena
        );

        return ResponseEntity.ok(respuesta);
    }


    // =========================================================
    // AGREGAR / CAMBIAR CONTRASEÑA
    // =========================================================

    @PutMapping("/contrasena")
    public ResponseEntity<?> cambiarContrasena(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody CambiarContrasenaRequest request
    ) {

        String token = authorizationHeader.substring(7);

        Integer idUsuario = jwtService.extraerIdUsuario(token);

        var usuario = usuarioRepository.findById(idUsuario).orElse(null);

        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }


        // -----------------------------------------------------
        // Validar nueva contraseña
        // -----------------------------------------------------

        String nuevaContrasena = request.getNuevaContrasena();

        if (nuevaContrasena == null || nuevaContrasena.isBlank()) {

            return ResponseEntity.badRequest()
                    .body("La nueva contraseña es obligatoria");
        }

        if (nuevaContrasena.length() < 6) {

            return ResponseEntity.badRequest()
                    .body("La contraseña debe tener mínimo 6 caracteres");
        }


        // -----------------------------------------------------
        // Comprobar si ya tiene contraseña
        // -----------------------------------------------------

        boolean tieneContrasena =
                usuario.getContrasenaHash() != null
                        && !usuario.getContrasenaHash().isBlank();


        // -----------------------------------------------------
        // Si ya tiene contraseña, verificar la anterior
        // -----------------------------------------------------

        if (tieneContrasena) {

            String contrasenaActual =
                    request.getContrasenaActual();

            if (contrasenaActual == null
                    || contrasenaActual.isBlank()) {

                return ResponseEntity.badRequest()
                        .body("Debes ingresar tu contraseña actual");
            }


            boolean contraseñaCorrecta =
                    passwordEncoder.matches(
                            contrasenaActual,
                            usuario.getContrasenaHash()
                    );

            if (!contraseñaCorrecta) {

                return ResponseEntity.status(401)
                        .body("La contraseña actual es incorrecta");
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
                "Contraseña actualizada correctamente"
        );
    }
}