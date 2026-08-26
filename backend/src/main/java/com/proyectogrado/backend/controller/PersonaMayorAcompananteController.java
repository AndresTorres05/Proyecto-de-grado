package com.proyectogrado.backend.controller;

import com.proyectogrado.backend.dto.AcompananteResponse;
import com.proyectogrado.backend.model.PersonaMayorAcompanante;
import com.proyectogrado.backend.repository.PersonaMayorAcompananteRepository;
import com.proyectogrado.backend.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/persona-mayor/acompanantes")
public class PersonaMayorAcompananteController {

    private final PersonaMayorAcompananteRepository relacionRepository;
    private final JwtService jwtService;

    public PersonaMayorAcompananteController(
            PersonaMayorAcompananteRepository relacionRepository,
            JwtService jwtService
    ) {
        this.relacionRepository = relacionRepository;
        this.jwtService = jwtService;
    }

    @GetMapping
    public ResponseEntity<List<AcompananteResponse>> obtenerAcompanantes(
            @RequestHeader("Authorization") String authorizationHeader
    ) {

        String token = authorizationHeader.substring(7);

        Integer idPersonaMayor =
                jwtService.extraerIdUsuario(token);

        List<PersonaMayorAcompanante> relaciones =
                relacionRepository.findById_IdPersonaMayor(
                        idPersonaMayor
                );

        List<AcompananteResponse> respuesta = relaciones.stream()
                .map(PersonaMayorAcompanante::getAcompanante)
                .map(acompanante -> new AcompananteResponse(
                        acompanante.getIdUsuario(),
                        acompanante.getUsuario().getNombreUsuario(),
                        acompanante.getUsuario().getTelefono(),
                        acompanante.getParentesco()
                ))
                .toList();

        return ResponseEntity.ok(respuesta);
    }
}