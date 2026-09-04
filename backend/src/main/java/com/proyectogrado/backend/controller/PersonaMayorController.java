package com.proyectogrado.backend.controller;

import com.proyectogrado.backend.dto.PersonaMayorResponse;
import com.proyectogrado.backend.model.PersonaMayor;
import com.proyectogrado.backend.repository.PersonaMayorRepository;
import com.proyectogrado.backend.repository.UsuarioRepository;
import com.proyectogrado.backend.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/persona-mayor")
public class PersonaMayorController {

    private final PersonaMayorRepository personaMayorRepository;
    private final JwtService jwtService;

    public PersonaMayorController(
            PersonaMayorRepository personaMayorRepository,
            JwtService jwtService
    ) {
        this.personaMayorRepository = personaMayorRepository;
        this.jwtService = jwtService;
    }
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

        PersonaMayorResponse respuesta = new PersonaMayorResponse(
                personaMayor.getIdUsuario(),
                personaMayor.getUsuario().getNombreUsuario(),
                personaMayor.getUsuario().getTelefono(),
                personaMayor.getUsuario().getCorreo(),
                personaMayor.getFechaNacimiento(),
                personaMayor.getGenero(),
                personaMayor.getDireccion()
        );

        return ResponseEntity.ok(respuesta);
    }
}