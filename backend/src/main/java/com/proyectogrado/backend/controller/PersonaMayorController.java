package com.proyectogrado.backend.controller;

import com.proyectogrado.backend.dto.PersonaMayorResponse;
import com.proyectogrado.backend.dto.ActualizarPersonaMayorRequest;
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
    private final UsuarioRepository usuarioRepository;

    public PersonaMayorController(
            PersonaMayorRepository personaMayorRepository,
            JwtService jwtService,
            UsuarioRepository usuarioRepository

    ) {
        this.personaMayorRepository = personaMayorRepository;
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
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
        @PutMapping("/informacion")
    public ResponseEntity<PersonaMayorResponse> actualizarInformacion(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody ActualizarPersonaMayorRequest request
    ) {

        String token = authorizationHeader.substring(7);

        Integer idUsuario = jwtService.extraerIdUsuario(token);

        PersonaMayor personaMayor =
                personaMayorRepository.findById(idUsuario).orElse(null);

        if (personaMayor == null) {
            return ResponseEntity.notFound().build();
        }

        // Actualizar datos de usuario
        var usuario = usuarioRepository.findById(idUsuario).orElse(null);

        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }

        usuario.setNombreUsuario(request.getNombre());
        usuario.setCorreo(request.getCorreo());
        usuario.setTelefono(request.getTelefono());

        usuarioRepository.save(usuario);

        // Actualizar datos de persona mayor
        personaMayor.setFechaNacimiento(request.getFechaNacimiento());
        personaMayor.setGenero(request.getGenero());
        personaMayor.setDireccion(request.getDireccion());
        System.out.println("FECHA: " + personaMayor.getFechaNacimiento());
        System.out.println("GENERO: " + personaMayor.getGenero());
        System.out.println("DIRECCION: " + personaMayor.getDireccion());

        personaMayorRepository.save(personaMayor);
        System.out.println("PERSONA MAYOR GUARDADA");

        PersonaMayorResponse respuesta = new PersonaMayorResponse(
                personaMayor.getIdUsuario(),
                usuario.getNombreUsuario(),
                usuario.getTelefono(),
                usuario.getCorreo(),
                personaMayor.getFechaNacimiento(),
                personaMayor.getGenero(),
                personaMayor.getDireccion()
        );

        return ResponseEntity.ok(respuesta);
    }
}