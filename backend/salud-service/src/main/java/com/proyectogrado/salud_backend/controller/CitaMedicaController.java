package com.proyectogrado.salud_backend.controller;

import com.proyectogrado.salud_backend.dto.CitaMedicaRequest;
import com.proyectogrado.salud_backend.dto.CitaMedicaResponse;
import com.proyectogrado.salud_backend.model.CitaMedica;
import com.proyectogrado.salud_backend.repository.CitaMedicaRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/persona-mayor/citas-medicas")
public class CitaMedicaController {

    private final CitaMedicaRepository citaMedicaRepository;

    public CitaMedicaController(CitaMedicaRepository citaMedicaRepository) {
        this.citaMedicaRepository = citaMedicaRepository;
    }

    @GetMapping
    public ResponseEntity<List<CitaMedicaResponse>> listar(
            @RequestHeader("X-User-Id") Integer idPersonaMayor
    ) {
        List<CitaMedicaResponse> respuesta = citaMedicaRepository
                .findByIdPersonaMayorOrderByFechaAscHoraAsc(idPersonaMayor)
                .stream()
                .map(this::aRespuesta)
                .toList();

        return ResponseEntity.ok(respuesta);
    }

    @PostMapping
    public ResponseEntity<CitaMedicaResponse> crear(
            @RequestHeader("X-User-Id") Integer idPersonaMayor,
            @RequestBody CitaMedicaRequest request
    ) {
        CitaMedica cita = new CitaMedica();

        cita.setIdPersonaMayor(idPersonaMayor);
        aplicarCambios(cita, request);

        cita = citaMedicaRepository.save(cita);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(aRespuesta(cita));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @RequestHeader("X-User-Id") Integer idPersonaMayor,
            @PathVariable Integer id,
            @RequestBody CitaMedicaRequest request
    ) {
        CitaMedica cita = obtenerPropia(id, idPersonaMayor);

        if (cita == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Esta cita médica no pertenece a este usuario");
        }

        aplicarCambios(cita, request);

        cita = citaMedicaRepository.save(cita);

        return ResponseEntity.ok(aRespuesta(cita));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(
            @RequestHeader("X-User-Id") Integer idPersonaMayor,
            @PathVariable Integer id
    ) {
        CitaMedica cita = obtenerPropia(id, idPersonaMayor);

        if (cita == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Esta cita médica no pertenece a este usuario");
        }

        citaMedicaRepository.delete(cita);

        return ResponseEntity.noContent().build();
    }

    private CitaMedica obtenerPropia(
            Integer id,
            Integer idPersonaMayor
    ) {
        CitaMedica cita = citaMedicaRepository
                .findById(id)
                .orElse(null);

        if (cita == null ||
                !cita.getIdPersonaMayor().equals(idPersonaMayor)) {
            return null;
        }

        return cita;
    }

    private void aplicarCambios(
            CitaMedica cita,
            CitaMedicaRequest request
    ) {
        cita.setTitulo(request.getTitulo());
        cita.setLugar(request.getLugar());
        cita.setObservaciones(request.getObservaciones());

        if (request.getFecha() != null &&
                !request.getFecha().isBlank()) {
            cita.setFecha(LocalDate.parse(request.getFecha()));
        }

        if (request.getHora() != null &&
                !request.getHora().isBlank()) {
            cita.setHora(LocalTime.parse(request.getHora()));
        }
    }

    private CitaMedicaResponse aRespuesta(CitaMedica cita) {

        DateTimeFormatter formatoHora =
                DateTimeFormatter.ofPattern("HH:mm");

        return new CitaMedicaResponse(
                cita.getIdCita(),
                cita.getTitulo(),
                cita.getLugar(),
                cita.getFecha() != null
                        ? cita.getFecha().toString()
                        : null,
                cita.getHora() != null
                        ? cita.getHora().format(formatoHora)
                        : null,
                cita.getObservaciones()
        );
    }
}