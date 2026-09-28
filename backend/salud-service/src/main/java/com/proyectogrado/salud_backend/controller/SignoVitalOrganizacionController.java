package com.proyectogrado.salud_backend.controller;

import com.proyectogrado.salud_backend.dto.SignoVitalRequest;
import com.proyectogrado.salud_backend.dto.SignoVitalResponse;
import com.proyectogrado.salud_backend.model.PersonaMayorOrganizacion;
import com.proyectogrado.salud_backend.model.SignoVital;
import com.proyectogrado.salud_backend.repository.PersonaMayorOrganizacionRepository;
import com.proyectogrado.salud_backend.repository.SignoVitalRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.proyectogrado.salud_backend.model.UsuarioLookup;
import com.proyectogrado.salud_backend.repository.UsuarioLookupRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/organizacion/signos-vitales")
public class SignoVitalOrganizacionController {

    private final SignoVitalRepository signoVitalRepository;
    private final PersonaMayorOrganizacionRepository personaMayorOrganizacionRepository;
    private final UsuarioLookupRepository usuarioLookupRepository; 

    public SignoVitalOrganizacionController(
            SignoVitalRepository signoVitalRepository,
            PersonaMayorOrganizacionRepository personaMayorOrganizacionRepository,
            UsuarioLookupRepository usuarioLookupRepository
    ) {
        this.signoVitalRepository = signoVitalRepository;
        this.personaMayorOrganizacionRepository = personaMayorOrganizacionRepository;
        this.usuarioLookupRepository = usuarioLookupRepository;
    }

    @PostMapping("/{idPersonaMayor}")
    public ResponseEntity<?> crear(
            @RequestHeader("X-User-Id") Integer idUsuarioOrganizacion,
            @PathVariable Integer idPersonaMayor,
            @RequestBody SignoVitalRequest request
    ) {

        Integer idOrganizacion = usuarioLookupRepository
        .findById(idUsuarioOrganizacion)
        .map(UsuarioLookup::getIdOrganizacion)
        .orElse(null);

if (idOrganizacion == null) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body("El usuario no tiene una organización asociada");
}

        // Verificar que la persona mayor pertenece a la organización
        PersonaMayorOrganizacion relacion =
                personaMayorOrganizacionRepository
                        .findById_IdPersonaMayorAndId_IdOrganizacionAndEstado(
                                idPersonaMayor,
                                idOrganizacion,
                                "ACEPTADA"
                        )
                        .orElse(null);

        if (relacion == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("La persona mayor no está asociada a esta organización");
        }

        SignoVital signoVital = new SignoVital();

        signoVital.setIdPersonaMayor(idPersonaMayor);
        signoVital.setFechaHora(LocalDateTime.now());

        signoVital.setPresionSistolica(
                request.getPresionSistolica()
        );

        signoVital.setPresionDiastolica(
                request.getPresionDiastolica()
        );

        signoVital.setFrecuenciaCardiaca(
                request.getFrecuenciaCardiaca()
        );

        signoVital.setTemperatura(
                request.getTemperatura()
        );

        signoVital.setSaturacionOxigeno(
                request.getSaturacionOxigeno()
        );

        signoVital.setFrecuenciaRespiratoria(
                request.getFrecuenciaRespiratoria()
        );

        signoVital.setPeso(
                request.getPeso()
        );

        signoVital.setObservaciones(
                request.getObservaciones()
        );

        signoVital = signoVitalRepository.save(signoVital);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(aRespuesta(signoVital));
    }

    private SignoVitalResponse aRespuesta(SignoVital signoVital) {

        DateTimeFormatter formato =
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        return new SignoVitalResponse(
                signoVital.getIdSignoVital(),
                signoVital.getFechaHora() != null
                        ? signoVital.getFechaHora().format(formato)
                        : null,
                signoVital.getPresionSistolica(),
                signoVital.getPresionDiastolica(),
                signoVital.getFrecuenciaCardiaca(),
                signoVital.getTemperatura(),
                signoVital.getSaturacionOxigeno(),
                signoVital.getFrecuenciaRespiratoria(),
                signoVital.getPeso(),
                signoVital.getObservaciones()
        );
    }
}