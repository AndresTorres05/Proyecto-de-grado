package com.proyectogrado.backend.controller;

import com.proyectogrado.backend.model.Organizacion;
import com.proyectogrado.backend.model.Usuario;
import com.proyectogrado.backend.repository.OrganizacionRepository;
import com.proyectogrado.backend.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/organizacion")
@CrossOrigin(origins = "http://localhost:4200")
public class OrganizacionController {

    private final OrganizacionRepository organizacionRepository;
    private final UsuarioRepository usuarioRepository;

    public OrganizacionController(
            OrganizacionRepository organizacionRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.organizacionRepository = organizacionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // ==========================================
    // OBTENER INFORMACIÓN DE LA ORGANIZACIÓN
    // ==========================================

    @GetMapping("/informacion")
    public ResponseEntity<?> obtenerInformacion() {

        try {

            Usuario usuario = obtenerUsuarioAutenticado();

            if (usuario == null) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("Usuario no autenticado");
            }

            Integer idOrganizacion = usuario.getIdOrganizacion();

            if (idOrganizacion == null) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("El usuario no tiene una organización asociada");
            }

            Organizacion organizacion =
                    organizacionRepository.findById(idOrganizacion)
                            .orElse(null);

            if (organizacion == null) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("Organización no encontrada");
            }

            return ResponseEntity.ok(
                    new OrganizacionInformacionResponse(
                            organizacion.getIdOrganizacion(),
                            usuario.getNombreUsuario(),
                            usuario.getCorreo(),
                            usuario.getTelefono(),
                            organizacion.getDireccion()
                    )
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener la información de la organización");
        }
    }


    // ==========================================
    // ACTUALIZAR INFORMACIÓN DE LA ORGANIZACIÓN
    // ==========================================

    @PutMapping("/informacion")
    public ResponseEntity<?> actualizarInformacion(
            @RequestBody OrganizacionInformacionRequest datosActualizados
    ) {

        try {

            Usuario usuario = obtenerUsuarioAutenticado();

            if (usuario == null) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("Usuario no autenticado");
            }

            Integer idOrganizacion = usuario.getIdOrganizacion();

            if (idOrganizacion == null) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("El usuario no tiene una organización asociada");
            }

            Organizacion organizacion =
                    organizacionRepository.findById(idOrganizacion)
                            .orElse(null);

            if (organizacion == null) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("Organización no encontrada");
            }


            // ==========================================
            // ACTUALIZAR DATOS GENERALES DEL USUARIO
            // ==========================================

            usuario.setNombreUsuario(datosActualizados.nombre());
            usuario.setCorreo(datosActualizados.correo());
            usuario.setTelefono(datosActualizados.telefono());

            usuarioRepository.save(usuario);


            // ==========================================
            // ACTUALIZAR DATOS PROPIOS DE LA ORGANIZACIÓN
            // ==========================================

            organizacion.setDireccion(datosActualizados.direccion());

            Organizacion organizacionGuardada =
                    organizacionRepository.save(organizacion);


            // ==========================================
            // RESPUESTA
            // ==========================================

            return ResponseEntity.ok(
                    new OrganizacionInformacionResponse(
                            organizacionGuardada.getIdOrganizacion(),
                            usuario.getNombreUsuario(),
                            usuario.getCorreo(),
                            usuario.getTelefono(),
                            organizacionGuardada.getDireccion()
                    )
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar la información de la organización");
        }
    }


    // ==========================================
    // OBTENER USUARIO AUTENTICADO
    // ==========================================

    private Usuario obtenerUsuarioAutenticado() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            return null;
        }

        String identificador = authentication.getName();

        return usuarioRepository
                .findByCorreo(identificador)
                .or(() ->
                        usuarioRepository.findByTelefono(identificador)
                )
                .orElse(null);
    }


    // ==========================================
    // DTO PARA MOSTRAR INFORMACIÓN
    // ==========================================

    public record OrganizacionInformacionResponse(
            Integer idOrganizacion,
            String nombre,
            String correo,
            String telefono,
            String direccion
    ) {}


    // ==========================================
    // DTO PARA ACTUALIZAR INFORMACIÓN
    // ==========================================

    public record OrganizacionInformacionRequest(
            String nombre,
            String correo,
            String telefono,
            String direccion
    ) {}
}