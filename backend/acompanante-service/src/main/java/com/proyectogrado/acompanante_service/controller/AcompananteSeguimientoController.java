package com.proyectogrado.acompanante_service.controller;

import com.proyectogrado.acompanante_service.dto.ContactoResponse;
import com.proyectogrado.acompanante_service.dto.MedicamentoSeguimientoResponse;
import com.proyectogrado.acompanante_service.model.AcompananteInfoLookup;
import com.proyectogrado.acompanante_service.model.MedicamentoLookup;
import com.proyectogrado.acompanante_service.model.PersonaMayorAcompanante;
import com.proyectogrado.acompanante_service.model.UsuarioLookup;
import com.proyectogrado.acompanante_service.repository.AcompananteInfoLookupRepository;
import com.proyectogrado.acompanante_service.repository.MedicamentoLookupRepository;
import com.proyectogrado.acompanante_service.repository.PersonaMayorAcompananteRepository;
import com.proyectogrado.acompanante_service.repository.UsuarioLookupRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Le permite a un acompanante ver medicamentos y contactos de una
 * persona mayor que acompana, SOLO si la relacion esta ACEPTADA.
 *
 * Cruza tres dominios de datos (identidad en auth-backend, relacion en
 * este mismo servicio, medicamentos en salud-backend) via los lookups
 * de solo lectura ya establecidos en el proyecto.
 */
@RestController
@RequestMapping("/api/acompanante/seguimiento")
public class AcompananteSeguimientoController {

    private final PersonaMayorAcompananteRepository relacionRepository;
    private final UsuarioLookupRepository usuarioLookupRepository;
    private final AcompananteInfoLookupRepository acompananteInfoLookupRepository;
    private final MedicamentoLookupRepository medicamentoLookupRepository;

    public AcompananteSeguimientoController(
            PersonaMayorAcompananteRepository relacionRepository,
            UsuarioLookupRepository usuarioLookupRepository,
            AcompananteInfoLookupRepository acompananteInfoLookupRepository,
            MedicamentoLookupRepository medicamentoLookupRepository
    ) {
        this.relacionRepository = relacionRepository;
        this.usuarioLookupRepository = usuarioLookupRepository;
        this.acompananteInfoLookupRepository = acompananteInfoLookupRepository;
        this.medicamentoLookupRepository = medicamentoLookupRepository;
    }

    @GetMapping("/{idPersonaMayor}/medicamentos")
    public ResponseEntity<?> obtenerMedicamentos(
            @RequestHeader("X-User-Id") Integer idAcompanante,
            @PathVariable Integer idPersonaMayor
    ) {
        if (!tieneRelacionAceptada(idAcompanante, idPersonaMayor)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tienes autorización para consultar a esta persona mayor");
        }

        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter formatoFechaHora = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        List<MedicamentoSeguimientoResponse> respuesta = medicamentoLookupRepository
                .findByIdPersonaMayor(idPersonaMayor)
                .stream()
                .map(m -> new MedicamentoSeguimientoResponse(
                        m.getIdMedicamento(),
                        m.getNombre(),
                        m.getDosis(),
                        m.getFrecuencia(),
                        m.getIntervaloHoras(),
                        m.getHora() != null ? m.getHora().format(formatoHora) : null,
                        m.getProximaToma() != null ? m.getProximaToma().format(formatoFechaHora) : null,
                        m.getUltimaToma() != null ? m.getUltimaToma().format(formatoFechaHora) : null,
                        m.getActivo()
                ))
                .toList();

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{idPersonaMayor}/contactos")
    public ResponseEntity<?> obtenerContactos(
            @RequestHeader("X-User-Id") Integer idAcompanante,
            @PathVariable Integer idPersonaMayor
    ) {
        if (!tieneRelacionAceptada(idAcompanante, idPersonaMayor)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tienes autorización para consultar a esta persona mayor");
        }

        List<PersonaMayorAcompanante> relaciones = relacionRepository
                .findById_IdPersonaMayorAndEstado(idPersonaMayor, "ACEPTADA");

        List<ContactoResponse> respuesta = relaciones.stream()
                .map(relacionAcompanante -> {
                    Integer idOtroAcompanante =
                            relacionAcompanante.getId().getIdAcompanante();

                    UsuarioLookup usuario =
                            usuarioLookupRepository
                                    .findById(idOtroAcompanante)
                                    .orElse(null);

                    String tipoRelacion =
                            acompananteInfoLookupRepository
                                    .findById(idOtroAcompanante)
                                    .map(AcompananteInfoLookup::getRelacion)
                                    .orElse(null);

                    return new ContactoResponse(
                            idOtroAcompanante,
                            usuario != null
                                    ? usuario.getNombreUsuario()
                                    : null,
                            usuario != null
                                    ? usuario.getCelular()
                                    : null,
                            tipoRelacion
                    );
                })
                .toList();

        return ResponseEntity.ok(respuesta);
    }

    private boolean tieneRelacionAceptada(
            Integer idAcompanante,
            Integer idPersonaMayor
    ) {
        return relacionRepository
                .findById_IdAcompananteAndEstado(
                        idAcompanante,
                        "ACEPTADA"
                )
                .stream()
                .anyMatch(
                        relacion -> relacion.getId()
                                .getIdPersonaMayor()
                                .equals(idPersonaMayor)
                );
    }
}