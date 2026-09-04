package com.proyectogrado.backend.controller;

import com.proyectogrado.backend.dto.PersonaMayorResponse;
import com.proyectogrado.backend.model.PersonaMayorAcompanante;
import com.proyectogrado.backend.repository.PersonaMayorAcompananteRepository;
import com.proyectogrado.backend.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/acompanante/personas-mayores")
public class AcompananteController {

    private final PersonaMayorAcompananteRepository relacionRepository;
    private final JwtService jwtService;

    public AcompananteController(
            PersonaMayorAcompananteRepository relacionRepository,
            JwtService jwtService
    ) {
        this.relacionRepository = relacionRepository;
        this.jwtService = jwtService;
    }

    @GetMapping
    public ResponseEntity<List<PersonaMayorResponse>> obtenerPersonasMayores(
            @RequestHeader("Authorization") String authorizationHeader
    ) {

        String token = authorizationHeader.substring(7);

        Integer idAcompanante =
                jwtService.extraerIdUsuario(token);

        List<PersonaMayorAcompanante> relaciones =
                relacionRepository.findById_IdAcompanante(
                        idAcompanante
                );

        List<PersonaMayorResponse> respuesta = relaciones.stream()
                .map(PersonaMayorAcompanante::getPersonaMayor)
                .map(personaMayor -> new PersonaMayorResponse(
                        personaMayor.getIdUsuario(),
                        personaMayor.getUsuario().getNombreUsuario(),
                        personaMayor.getUsuario().getTelefono(),
                        personaMayor.getUsuario().getCorreo(),
                        personaMayor.getFechaNacimiento(),
                        personaMayor.getGenero(),
                        personaMayor.getDireccion()
                ))
                .toList();

        return ResponseEntity.ok(respuesta);
    }
}
