package com.proyectogrado.persona_mayor_service.controller;

import com.proyectogrado.persona_mayor_service.dto.ActualizarPersonaMayorRequest;
import com.proyectogrado.persona_mayor_service.dto.PersonaMayorInformacionResponse;
import com.proyectogrado.persona_mayor_service.model.PersonaMayorLookup;
import com.proyectogrado.persona_mayor_service.model.UsuarioLookup;
import com.proyectogrado.persona_mayor_service.repository.PersonaMayorLookupRepository;
import com.proyectogrado.persona_mayor_service.repository.UsuarioLookupRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * Perfil propio de la persona mayor: fechaNacimiento/genero/direccion.
 *
 * nombre/correo/telefono son de identidad (auth-backend) y solo se leen
 * aqui (UsuarioLookup); NO se editan desde este servicio.
 */
@RestController
@RequestMapping("/api/persona-mayor/informacion")
public class PersonaMayorInformacionController {

    private final PersonaMayorLookupRepository personaMayorLookupRepository;
    private final UsuarioLookupRepository usuarioLookupRepository;

    public PersonaMayorInformacionController(
            PersonaMayorLookupRepository personaMayorLookupRepository,
            UsuarioLookupRepository usuarioLookupRepository
    ) {
        this.personaMayorLookupRepository = personaMayorLookupRepository;
        this.usuarioLookupRepository = usuarioLookupRepository;
    }

    @GetMapping
    public ResponseEntity<?> obtenerInformacion(
            @RequestHeader("X-User-Id") Integer idUsuario
    ) {
        PersonaMayorLookup personaMayor = personaMayorLookupRepository.findById(idUsuario).orElse(null);

        if (personaMayor == null) {
            return ResponseEntity.notFound().build();
        }

        UsuarioLookup usuario = usuarioLookupRepository.findById(idUsuario).orElse(null);

        return ResponseEntity.ok(aRespuesta(personaMayor, usuario));
    }

    @PutMapping
    public ResponseEntity<?> actualizarInformacion(
            @RequestHeader("X-User-Id") Integer idUsuario,
            @RequestBody ActualizarPersonaMayorRequest request
    ) {
        PersonaMayorLookup personaMayor = personaMayorLookupRepository.findById(idUsuario).orElse(null);

        if (personaMayor == null) {
            return ResponseEntity.notFound().build();
        }

        if (request.getFechaNacimiento() != null && !request.getFechaNacimiento().isBlank()) {
            personaMayor.setFechaNacimiento(LocalDate.parse(request.getFechaNacimiento()));
        }

        personaMayor.setGenero(request.getGenero());
        personaMayor.setDireccion(request.getDireccion());

        personaMayor = personaMayorLookupRepository.save(personaMayor);

        UsuarioLookup usuario = usuarioLookupRepository.findById(idUsuario).orElse(null);

        return ResponseEntity.ok(aRespuesta(personaMayor, usuario));
    }

    private PersonaMayorInformacionResponse aRespuesta(PersonaMayorLookup personaMayor, UsuarioLookup usuario) {
        return new PersonaMayorInformacionResponse(
                personaMayor.getIdUsuario(),
                usuario != null ? usuario.getNombreUsuario() : null,
                usuario != null ? usuario.getTelefono() : null,
                usuario != null ? usuario.getCorreo() : null,
                personaMayor.getFechaNacimiento() != null ? personaMayor.getFechaNacimiento().toString() : null,
                personaMayor.getGenero(),
                personaMayor.getDireccion()
        );
    }
}