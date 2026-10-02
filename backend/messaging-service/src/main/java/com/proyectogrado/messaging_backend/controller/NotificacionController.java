package com.proyectogrado.messaging_backend.controller;

import com.proyectogrado.messaging_backend.model.Notificacion;
import com.proyectogrado.messaging_backend.model.UsuarioLookup;
import com.proyectogrado.messaging_backend.repository.NotificacionRepository;
import com.proyectogrado.messaging_backend.repository.UsuarioLookupRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Notificaciones (SMS enviados) del usuario autenticado, para la campanita
 * del panel. Pasa por el gateway con JwtAuth, que agrega el encabezado
 * X-User-Id; cada usuario solo ve las de su propio celular.
 */
@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final NotificacionRepository notificacionRepository;
    private final UsuarioLookupRepository usuarioLookupRepository;

    public NotificacionController(
            NotificacionRepository notificacionRepository,
            UsuarioLookupRepository usuarioLookupRepository
    ) {
        this.notificacionRepository = notificacionRepository;
        this.usuarioLookupRepository = usuarioLookupRepository;
    }

    /** Las 20 más recientes y cuántas faltan por leer. */
    @GetMapping
    public ResponseEntity<?> listar(@RequestHeader("X-User-Id") Integer idUsuario) {

        String celular = celularDe(idUsuario);

        if (celular == null) {
            return ResponseEntity.ok(Map.of("noLeidas", 0, "notificaciones", List.of()));
        }

        List<NotificacionResponse> notificaciones =
                notificacionRepository.findTop20ByCelularOrderByFechaEnvioDesc(celular)
                        .stream()
                        .map(NotificacionResponse::de)
                        .toList();

        return ResponseEntity.ok(Map.of(
                "noLeidas", notificacionRepository.countByCelularAndLeidaFalse(celular),
                "notificaciones", notificaciones
        ));
    }

    /** Se llama al abrir la campanita: todas quedan como leídas. */
    @PutMapping("/leidas")
    public ResponseEntity<Void> marcarLeidas(@RequestHeader("X-User-Id") Integer idUsuario) {

        String celular = celularDe(idUsuario);

        if (celular != null) {
            notificacionRepository.marcarLeidas(celular);
        }

        return ResponseEntity.noContent().build();
    }

    /** Celular del usuario, o null si no tiene. Las notificaciones se guardan por celular. */
    private String celularDe(Integer idUsuario) {
        return usuarioLookupRepository.findById(idUsuario)
                .map(UsuarioLookup::getCelular)
                .filter(celular -> !celular.isBlank())
                .orElse(null);
    }

    /** Notificación tal como la recibe el frontend. */
    public record NotificacionResponse(Long id, String mensaje, Instant fechaEnvio, boolean leida) {
        static NotificacionResponse de(Notificacion n) {
            return new NotificacionResponse(n.getIdNotificacion(), n.getMensaje(), n.getFechaEnvio(), n.isLeida());
        }
    }
}
