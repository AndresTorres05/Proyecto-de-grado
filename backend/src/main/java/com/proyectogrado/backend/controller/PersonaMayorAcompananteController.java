package com.proyectogrado.backend.controller;

import com.proyectogrado.backend.dto.AgregarAcompananteRequest;
import com.proyectogrado.backend.model.Acompanante;
import com.proyectogrado.backend.model.PersonaMayor;
import com.proyectogrado.backend.model.Usuario;
import com.proyectogrado.backend.repository.AcompananteRepository;
import com.proyectogrado.backend.repository.PersonaMayorRepository;
import com.proyectogrado.backend.repository.UsuarioRepository;
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
        private final UsuarioRepository usuarioRepository;
        private final AcompananteRepository acompananteRepository;
        private final PersonaMayorRepository personaMayorRepository;

        public PersonaMayorAcompananteController(
                PersonaMayorAcompananteRepository relacionRepository,
                JwtService jwtService,
                UsuarioRepository usuarioRepository,
                AcompananteRepository acompananteRepository,
                PersonaMayorRepository personaMayorRepository
        ) {
                this.relacionRepository = relacionRepository;
                this.jwtService = jwtService;
                this.usuarioRepository = usuarioRepository;
                this.acompananteRepository = acompananteRepository;
                this.personaMayorRepository = personaMayorRepository;
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
    @PostMapping
        public ResponseEntity<?> agregarAcompanante(
                @RequestHeader("Authorization") String authorizationHeader,
                @RequestBody AgregarAcompananteRequest request
        ) {

        String token = authorizationHeader.substring(7);

        Integer idPersonaMayor =
                jwtService.extraerIdUsuario(token);

        PersonaMayor personaMayor =
                personaMayorRepository.findById(idPersonaMayor)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "No se encontró la persona mayor"
                                )
                        );

        Usuario usuario =
                usuarioRepository.findByTelefono(request.getTelefono())
                        .orElse(null);

        Acompanante acompanante;

        if (usuario == null) {

                usuario = new Usuario();

                usuario.setNombreUsuario(
                        request.getNombreUsuario()
                );

                usuario.setTelefono(
                        request.getTelefono()
                );

                usuario = usuarioRepository.saveAndFlush(usuario);

                acompanante = new Acompanante(
                        usuario,
                        request.getParentesco()
                );

                acompananteRepository.saveAndFlush(acompanante);

        } else {

                acompanante =
                        acompananteRepository.findById(
                                usuario.getIdUsuario()
                        ).orElse(null);

                if (acompanante == null) {
                return ResponseEntity.badRequest()
                        .body("El usuario existe pero no es un acompañante");
                }

                acompanante.setParentesco(
                        request.getParentesco()
                );

                acompananteRepository.saveAndFlush(acompanante);
        }

        PersonaMayorAcompanante relacion =
                new PersonaMayorAcompanante(
                        personaMayor,
                        acompanante
                );

        relacionRepository.saveAndFlush(relacion);

        return ResponseEntity.ok(
                "Acompañante agregado correctamente"
        );
        }
}