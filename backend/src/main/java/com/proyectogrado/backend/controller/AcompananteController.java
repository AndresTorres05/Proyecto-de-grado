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


    // =========================================================
    // PERSONAS MAYORES ACEPTADAS
    // =========================================================

    @GetMapping
    public ResponseEntity<List<PersonaMayorResponse>> obtenerPersonasMayores(
            @RequestHeader("Authorization") String authorizationHeader
    ) {

        String token = authorizationHeader.substring(7);

        Integer idAcompanante =
                jwtService.extraerIdUsuario(token);

        List<PersonaMayorAcompanante> relaciones =
                relacionRepository.findById_IdAcompananteAndEstado(
                        idAcompanante,
                        "ACEPTADA"
                );


        List<PersonaMayorResponse> respuesta =
                relaciones.stream()

                        .map(PersonaMayorAcompanante::getPersonaMayor)

                        .map(personaMayor -> {

                            boolean tieneContrasena =
                                    personaMayor.getUsuario()
                                            .getContrasenaHash() != null
                                    &&
                                    !personaMayor.getUsuario()
                                            .getContrasenaHash()
                                            .isBlank();

                            return new PersonaMayorResponse(
                                    personaMayor.getIdUsuario(),
                                    personaMayor.getUsuario()
                                            .getNombreUsuario(),
                                    personaMayor.getUsuario()
                                            .getTelefono(),
                                    personaMayor.getUsuario()
                                            .getCorreo(),
                                    personaMayor.getFechaNacimiento(),
                                    personaMayor.getGenero(),
                                    personaMayor.getDireccion(),
                                    tieneContrasena
                            );
                        })

                        .toList();

        return ResponseEntity.ok(respuesta);
    }


    // =========================================================
    // SOLICITUDES PENDIENTES
    // =========================================================

    @GetMapping("/solicitudes")
    public ResponseEntity<List<PersonaMayorResponse>> obtenerSolicitudesPendientes(
            @RequestHeader("Authorization") String authorizationHeader
    ) {

        String token = authorizationHeader.substring(7);

        Integer idAcompanante =
                jwtService.extraerIdUsuario(token);

        List<PersonaMayorAcompanante> relaciones =
                relacionRepository.findById_IdAcompananteAndEstado(
                        idAcompanante,
                        "PENDIENTE"
                );


        List<PersonaMayorResponse> respuesta =
                relaciones.stream()

                        .map(PersonaMayorAcompanante::getPersonaMayor)

                        .map(personaMayor -> {

                            boolean tieneContrasena =
                                    personaMayor.getUsuario()
                                            .getContrasenaHash() != null
                                    &&
                                    !personaMayor.getUsuario()
                                            .getContrasenaHash()
                                            .isBlank();

                            return new PersonaMayorResponse(
                                    personaMayor.getIdUsuario(),
                                    personaMayor.getUsuario()
                                            .getNombreUsuario(),
                                    personaMayor.getUsuario()
                                            .getTelefono(),
                                    personaMayor.getUsuario()
                                            .getCorreo(),
                                    personaMayor.getFechaNacimiento(),
                                    personaMayor.getGenero(),
                                    personaMayor.getDireccion(),
                                    tieneContrasena
                            );
                        })

                        .toList();

        return ResponseEntity.ok(respuesta);
    }


    // =========================================================
    // ACEPTAR SOLICITUD
    // =========================================================

    @PutMapping("/solicitudes/{idPersonaMayor}/aceptar")
    public ResponseEntity<String> aceptarSolicitud(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Integer idPersonaMayor
    ) {

        String token = authorizationHeader.substring(7);

        Integer idAcompanante =
                jwtService.extraerIdUsuario(token);

        List<PersonaMayorAcompanante> relaciones =
                relacionRepository.findById_IdAcompanante(
                        idAcompanante
                );


        PersonaMayorAcompanante relacion =
                relaciones.stream()

                        .filter(r ->
                                r.getPersonaMayor()
                                        .getIdUsuario()
                                        .equals(idPersonaMayor)
                        )

                        .findFirst()

                        .orElse(null);


        if (relacion == null) {
            return ResponseEntity.notFound().build();
        }


        if (!"PENDIENTE".equals(relacion.getEstado())) {

            return ResponseEntity.badRequest()
                    .body("Esta solicitud ya fue procesada");
        }


        relacion.setEstado("ACEPTADA");

        relacionRepository.save(relacion);


        return ResponseEntity.ok(
                "Solicitud de acompañamiento aceptada"
        );
    }


    // =========================================================
    // RECHAZAR SOLICITUD
    // =========================================================

    @PutMapping("/solicitudes/{idPersonaMayor}/rechazar")
    public ResponseEntity<String> rechazarSolicitud(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Integer idPersonaMayor
    ) {

        String token = authorizationHeader.substring(7);

        Integer idAcompanante =
                jwtService.extraerIdUsuario(token);

        List<PersonaMayorAcompanante> relaciones =
                relacionRepository.findById_IdAcompanante(
                        idAcompanante
                );


        PersonaMayorAcompanante relacion =
                relaciones.stream()

                        .filter(r ->
                                r.getPersonaMayor()
                                        .getIdUsuario()
                                        .equals(idPersonaMayor)
                        )

                        .findFirst()

                        .orElse(null);


        if (relacion == null) {
            return ResponseEntity.notFound().build();
        }


        if (!"PENDIENTE".equals(relacion.getEstado())) {

            return ResponseEntity.badRequest()
                    .body("Esta solicitud ya fue procesada");
        }


        relacion.setEstado("RECHAZADA");

        relacionRepository.save(relacion);


        return ResponseEntity.ok(
                "Solicitud de acompañamiento rechazada"
        );
    }
}