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
                relacionRepository.findById_IdPersonaMayorAndEstado(
                        idPersonaMayor,
                        "ACEPTADA"
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

    // El teléfono no está registrado
    if (usuario == null) {
        return ResponseEntity.badRequest()
                .body("No existe un usuario registrado con ese teléfono");
    }

    // Verificar que el usuario esté registrado como acompañante
    Acompanante acompanante =
            acompananteRepository.findById(
                    usuario.getIdUsuario()
            ).orElse(null);

    if (acompanante == null) {
        return ResponseEntity.badRequest()
                .body("El usuario existe, pero no está registrado como acompañante");
    }

    // Verificar si ya existe una relación entre ambos
    PersonaMayorAcompanante relacionExistente =
            relacionRepository.findById(
                    new com.proyectogrado.backend.model.PersonaMayorAcompananteId(
                            idPersonaMayor,
                            acompanante.getIdUsuario()
                    )
            ).orElse(null);

    if (relacionExistente != null) {

        if ("ACEPTADA".equals(relacionExistente.getEstado())) {
            return ResponseEntity.badRequest()
                    .body("Este acompañante ya está registrado");
        }

        if ("PENDIENTE".equals(relacionExistente.getEstado())) {
            return ResponseEntity.badRequest()
                    .body("Ya existe una solicitud pendiente para este acompañante");
        }

        // Si anteriormente fue rechazada, permitimos enviar una nueva solicitud
        relacionExistente.setEstado("PENDIENTE");

        acompanante.setParentesco(
                request.getParentesco()
        );

        acompananteRepository.saveAndFlush(acompanante);
        relacionRepository.saveAndFlush(relacionExistente);

        return ResponseEntity.ok(
                "Solicitud de acompañamiento enviada correctamente"
        );
    }

    // Crear una nueva solicitud pendiente
    PersonaMayorAcompanante relacion =
            new PersonaMayorAcompanante(
                    personaMayor,
                    acompanante
            );

    relacion.setEstado("PENDIENTE");

    acompanante.setParentesco(
            request.getParentesco()
    );

    acompananteRepository.saveAndFlush(acompanante);
    relacionRepository.saveAndFlush(relacion);

    return ResponseEntity.ok(
            "Solicitud de acompañamiento enviada correctamente"
    );
}
}