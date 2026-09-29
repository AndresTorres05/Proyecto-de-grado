package com.proyectogrado.salud_backend.controller;

import com.proyectogrado.salud_backend.config.ZonaHoraria;
import com.proyectogrado.salud_backend.dto.MedicamentoRequest;
import com.proyectogrado.salud_backend.dto.MedicamentoResponse;
import com.proyectogrado.salud_backend.model.Medicamento;
import com.proyectogrado.salud_backend.repository.MedicamentoRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/persona-mayor/medicamentos")
public class MedicamentoController {

    private final MedicamentoRepository medicamentoRepository;

    public MedicamentoController(MedicamentoRepository medicamentoRepository) {
        this.medicamentoRepository = medicamentoRepository;
    }

    @GetMapping
    public ResponseEntity<List<MedicamentoResponse>> listar(
            @RequestHeader("X-User-Id") Integer idPersonaMayor
    ) {
        List<MedicamentoResponse> respuesta = medicamentoRepository
                .findByIdPersonaMayor(idPersonaMayor)
                .stream()
                .map(this::aRespuesta)
                .toList();

        return ResponseEntity.ok(respuesta);
    }

    @PostMapping
    public ResponseEntity<MedicamentoResponse> crear(
            @RequestHeader("X-User-Id") Integer idPersonaMayor,
            @RequestBody MedicamentoRequest request
    ) {
        Medicamento medicamento = new Medicamento();
        medicamento.setIdPersonaMayor(idPersonaMayor);
        aplicarCambios(medicamento, request);
        recalcularProximaToma(medicamento);

        medicamento = medicamentoRepository.save(medicamento);

        return ResponseEntity.status(HttpStatus.CREATED).body(aRespuesta(medicamento));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @RequestHeader("X-User-Id") Integer idPersonaMayor,
            @PathVariable Integer id,
            @RequestBody MedicamentoRequest request
    ) {
        Medicamento medicamento = obtenerPropio(id, idPersonaMayor);
        if (medicamento == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Este medicamento no pertenece a este usuario");
        }

        aplicarCambios(medicamento, request);
        recalcularProximaToma(medicamento);

        medicamento = medicamentoRepository.save(medicamento);

        return ResponseEntity.ok(aRespuesta(medicamento));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(
            @RequestHeader("X-User-Id") Integer idPersonaMayor,
            @PathVariable Integer id
    ) {
        Medicamento medicamento = obtenerPropio(id, idPersonaMayor);
        if (medicamento == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Este medicamento no pertenece a este usuario");
        }

        medicamentoRepository.delete(medicamento);

        return ResponseEntity.noContent().build();
    }

    private Medicamento obtenerPropio(Integer id, Integer idPersonaMayor) {
        Medicamento medicamento = medicamentoRepository.findById(id).orElse(null);

        if (medicamento == null || !medicamento.getIdPersonaMayor().equals(idPersonaMayor)) {
            return null;
        }

        return medicamento;
    }

    private void aplicarCambios(Medicamento medicamento, MedicamentoRequest request) {
        medicamento.setNombre(request.getNombre());
        medicamento.setDosis(request.getDosis());
        medicamento.setFrecuencia(request.getFrecuencia());
        medicamento.setIntervaloHoras(request.getIntervaloHoras());

        if (request.getHora() != null && !request.getHora().isBlank()) {
            medicamento.setHora(LocalTime.parse(request.getHora()));
        }

        if (request.getFechaInicio() != null && !request.getFechaInicio().isBlank()) {
            medicamento.setFechaInicio(LocalDate.parse(request.getFechaInicio()));
        }

        if (request.getFechaFin() != null && !request.getFechaFin().isBlank()) {
            medicamento.setFechaFin(LocalDate.parse(request.getFechaFin()));
        } else {
            medicamento.setFechaFin(null);
        }
    }

    private void recalcularProximaToma(Medicamento medicamento) {
        LocalDate fecha = medicamento.getFechaInicio() != null ? medicamento.getFechaInicio() : ZonaHoraria.hoy();
        LocalTime hora = medicamento.getHora() != null ? medicamento.getHora() : ZonaHoraria.ahora().toLocalTime();

        LocalDateTime proximaToma = LocalDateTime.of(fecha, hora);

        // La hora puede ser la de la ultima toma (ya pasada): se avanza en
        // saltos del intervalo hasta la siguiente toma pendiente.
        Integer intervalo = medicamento.getIntervaloHoras();
        LocalDateTime ahora = ZonaHoraria.ahora();
        if (intervalo != null && intervalo > 0 && proximaToma.isBefore(ahora)) {
            long horasAtrasadas = Duration.between(proximaToma, ahora).toHours();
            long saltos = horasAtrasadas / intervalo + 1;
            proximaToma = proximaToma.plusHours(saltos * intervalo);
        }

        medicamento.setProximaToma(proximaToma);
        medicamento.setUltimoRecordatorioEnviado(null);
    }

    private MedicamentoResponse aRespuesta(Medicamento medicamento) {
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter formatoFechaHora = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        return new MedicamentoResponse(
                medicamento.getIdMedicamento(),
                medicamento.getNombre(),
                medicamento.getDosis(),
                medicamento.getFrecuencia(),
                medicamento.getIntervaloHoras(),
                medicamento.getHora() != null ? medicamento.getHora().format(formatoHora) : null,
                medicamento.getFechaInicio() != null ? medicamento.getFechaInicio().toString() : null,
                medicamento.getFechaFin() != null ? medicamento.getFechaFin().toString() : null,
                medicamento.getProximaToma() != null ? medicamento.getProximaToma().format(formatoFechaHora) : null,
                medicamento.getUltimaToma() != null ? medicamento.getUltimaToma().format(formatoFechaHora) : null,
                medicamento.getActivo()
        );
    }
}
