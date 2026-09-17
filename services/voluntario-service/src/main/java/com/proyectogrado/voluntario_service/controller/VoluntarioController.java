package com.proyectogrado.voluntario_service.controller;

import com.proyectogrado.voluntario_service.dto.ActualizarDisponibilidadRequest;
import com.proyectogrado.voluntario_service.dto.VoluntarioPerfilResponse;
import com.proyectogrado.voluntario_service.model.UsuarioLookup;
import com.proyectogrado.voluntario_service.model.VoluntarioLookup;
import com.proyectogrado.voluntario_service.repository.UsuarioLookupRepository;
import com.proyectogrado.voluntario_service.repository.VoluntarioLookupRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Perfil propio del voluntario: ver sus datos y editar su disponibilidad.
 *
 * Todavía no existe ninguna relación (con organizaciones, personas
 * mayores o actividades) para el voluntario — ni en el monolito ni en
 * la base de datos actual. Cuando se defina esa relación, se agrega
 * su propio modelo/repositorio/controlador aquí, con el mismo patrón
 * que ya usan personamayor-service y acompanante-service.
 *
 * El id del usuario autenticado llega en el header X-User-Id, puesto
 * por el api-gateway despues de validar el JWT. Este servicio no valida
 * tokens.
 */
@RestController
@RequestMapping("/api/voluntario/perfil")
public class VoluntarioController {

    private final UsuarioLookupRepository usuarioLookupRepository;
    private final VoluntarioLookupRepository voluntarioLookupRepository;

    public VoluntarioController(
            UsuarioLookupRepository usuarioLookupRepository,
            VoluntarioLookupRepository voluntarioLookupRepository
    ) {
        this.usuarioLookupRepository = usuarioLookupRepository;
        this.voluntarioLookupRepository = voluntarioLookupRepository;
    }

    @GetMapping
    public ResponseEntity<VoluntarioPerfilResponse> verPerfil(
            @RequestHeader("X-User-Id") Integer idVoluntario
    ) {
        UsuarioLookup usuario = usuarioLookupRepository.findById(idVoluntario).orElse(null);
        VoluntarioLookup voluntario = voluntarioLookupRepository.findById(idVoluntario).orElse(null);

        if (usuario == null || voluntario == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new VoluntarioPerfilResponse(
                idVoluntario,
                usuario.getNombreUsuario(),
                usuario.getTelefono(),
                usuario.getCorreo(),
                voluntario.getDisponibilidad()
        ));
    }

    @PutMapping("/disponibilidad")
    public ResponseEntity<?> actualizarDisponibilidad(
            @RequestHeader("X-User-Id") Integer idVoluntario,
            @RequestBody ActualizarDisponibilidadRequest request
    ) {
        VoluntarioLookup voluntario = voluntarioLookupRepository.findById(idVoluntario).orElse(null);

        if (voluntario == null) {
            return ResponseEntity.notFound().build();
        }

        voluntario.setDisponibilidad(request.disponibilidad());
        voluntarioLookupRepository.save(voluntario);

        return ResponseEntity.ok("Disponibilidad actualizada correctamente");
    }
}
