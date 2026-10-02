package com.proyectogrado.voluntario_service.controller;

import com.proyectogrado.voluntario_service.dto.VoluntarioPerfilResponse;
import com.proyectogrado.voluntario_service.model.UsuarioLookup;
import com.proyectogrado.voluntario_service.model.VoluntarioLookup;
import com.proyectogrado.voluntario_service.repository.UsuarioLookupRepository;
import com.proyectogrado.voluntario_service.repository.VoluntarioLookupRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Perfil propio del voluntario: ver sus datos. Por ahora el panel del
 * voluntario en el frontend no consume este endpoint.
 *
 * Los vínculos del voluntario con organizaciones están en
 * VoluntarioOrganizacionController. Con personas mayores o actividades
 * todavía no tiene ninguno.
 *
 * El id del usuario autenticado llega en el encabezado X-User-Id, que pone
 * el gateway después de validar el token. Este servicio no valida tokens.
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

    /** Datos de la cuenta del voluntario autenticado. */
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
                usuario.getCelular(),
                usuario.getCorreo()
        ));
    }
}
