package com.proyectogrado.backend.controller;

import com.proyectogrado.backend.dto.AcompananteResponse;
import com.proyectogrado.backend.dto.MedicamentoResponse;
import com.proyectogrado.backend.model.Medicamento;
import com.proyectogrado.backend.model.PersonaMayorAcompanante;
import com.proyectogrado.backend.model.Usuario;
import com.proyectogrado.backend.repository.MedicamentoRepository;
import com.proyectogrado.backend.repository.PersonaMayorAcompananteRepository;
import com.proyectogrado.backend.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/acompanante/seguimiento")
@CrossOrigin(origins = "http://localhost:4200")
public class AcompananteSeguimientoController {

    private final MedicamentoRepository medicamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PersonaMayorAcompananteRepository relacionRepository;

    public AcompananteSeguimientoController(
            MedicamentoRepository medicamentoRepository,
            UsuarioRepository usuarioRepository,
            PersonaMayorAcompananteRepository relacionRepository
    ) {
        this.medicamentoRepository = medicamentoRepository;
        this.usuarioRepository = usuarioRepository;
        this.relacionRepository = relacionRepository;
    }

    /**
     * Obtiene los medicamentos de una persona mayor
     * solamente si el acompañante tiene una relación ACEPTADA.
     */
    @GetMapping("/{idPersonaMayor}/medicamentos")
    public ResponseEntity<?> obtenerMedicamentos(
            @PathVariable Integer idPersonaMayor
    ) {

        try {

            Usuario acompanante = obtenerUsuarioAutenticado();

            if (acompanante == null) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("Usuario no autenticado");
            }

            Integer idAcompanante = acompanante.getIdUsuario();

            /*
             * Buscamos las relaciones ACEPTADAS de este acompañante
             * y comprobamos si dentro de ellas está la persona mayor
             * que está intentando consultar.
             */
            List<PersonaMayorAcompanante> relacionesAceptadas =
                    relacionRepository.findById_IdAcompananteAndEstado(
                            idAcompanante,
                            "ACEPTADA"
                    );

            boolean tieneRelacion = relacionesAceptadas.stream()
                    .anyMatch(relacion ->
                            relacion.getId()
                                    .getIdPersonaMayor()
                                    .equals(idPersonaMayor)
                    );

            if (!tieneRelacion) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body("No tienes autorización para consultar a esta persona mayor");
            }

            /*
             * La relación es válida.
             * Ahora obtenemos los medicamentos de la persona mayor.
             */
            List<MedicamentoResponse> respuesta =
                    medicamentoRepository
                            .findByPersonaMayor_IdUsuario(idPersonaMayor)
                            .stream()
                            .map(this::aRespuesta)
                            .toList();

            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener los medicamentos");
        }
    }


    @GetMapping("/{idPersonaMayor}/contactos")
public ResponseEntity<?> obtenerContactos(
        @PathVariable Integer idPersonaMayor
) {

    try {

        Usuario acompanante = obtenerUsuarioAutenticado();

        if (acompanante == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Usuario no autenticado");
        }

        Integer idAcompanante = acompanante.getIdUsuario();

        /*
         * Verificamos que el acompañante tenga una relación
         * ACEPTADA con la persona mayor.
         */
        List<PersonaMayorAcompanante> relacionesAceptadas =
                relacionRepository.findById_IdAcompananteAndEstado(
                        idAcompanante,
                        "ACEPTADA"
                );

        boolean tieneRelacion = relacionesAceptadas.stream()
                .anyMatch(relacion ->
                        relacion.getId()
                                .getIdPersonaMayor()
                                .equals(idPersonaMayor)
                );

        if (!tieneRelacion) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("No tienes autorización para consultar a esta persona mayor");
        }

        /*
         * Obtenemos los contactos/acompañantes de la
         * persona mayor seleccionada.
         */
        List<PersonaMayorAcompanante> relaciones =
                relacionRepository.findById_IdPersonaMayorAndEstado(
                        idPersonaMayor,
                        "ACEPTADA"
                );

        List<AcompananteResponse> respuesta =
                relaciones.stream()
                        .map(PersonaMayorAcompanante::getAcompanante)
                        .map(acompananteRelacionado ->
                                new AcompananteResponse(
                                        acompananteRelacionado.getIdUsuario(),
                                        acompananteRelacionado.getUsuario().getNombreUsuario(),
                                        acompananteRelacionado.getUsuario().getTelefono(),
                                        acompananteRelacionado.getParentesco()
                                )
                        )
                        .toList();

        return ResponseEntity.ok(respuesta);

    } catch (Exception e) {

        e.printStackTrace();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al obtener los contactos");
    }
}

    /**
     * Obtiene el usuario que actualmente está autenticado
     * mediante el JWT.
     */
    private Usuario obtenerUsuarioAutenticado() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (
                authentication == null ||
                !authentication.isAuthenticated()
        ) {
            return null;
        }

        String identificador = authentication.getName();

        return usuarioRepository
                .findByCorreo(identificador)
                .or(() -> usuarioRepository.findByTelefono(identificador))
                .orElse(null);
    }

    /**
     * Convierte Medicamento en MedicamentoResponse
     * para enviarlo al frontend.
     */
    private MedicamentoResponse aRespuesta(
            Medicamento medicamento
    ) {

        DateTimeFormatter formatoHora =
                DateTimeFormatter.ofPattern("HH:mm");

        DateTimeFormatter formatoFechaHora =
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        return new MedicamentoResponse(
                medicamento.getIdMedicamento(),
                medicamento.getNombre(),
                medicamento.getDosis(),
                medicamento.getFrecuencia(),
                medicamento.getIntervaloHoras(),

                medicamento.getHora() != null
                        ? medicamento.getHora().format(formatoHora)
                        : null,

                medicamento.getFechaInicio() != null
                        ? medicamento.getFechaInicio().toString()
                        : null,

                medicamento.getFechaFin() != null
                        ? medicamento.getFechaFin().toString()
                        : null,

                medicamento.getProximaToma() != null
                        ? medicamento.getProximaToma().format(formatoFechaHora)
                        : null,

                medicamento.getUltimaToma() != null
                        ? medicamento.getUltimaToma().format(formatoFechaHora)
                        : null,

                medicamento.getActivo()
        );
    }
}