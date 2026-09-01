package com.proyectogrado.backend.controller;

import com.proyectogrado.backend.dto.MedicamentoRequest;
import com.proyectogrado.backend.dto.MedicamentoResponse;
import com.proyectogrado.backend.model.Medicamento;
import com.proyectogrado.backend.model.PersonaMayor;
import com.proyectogrado.backend.model.Usuario;
import com.proyectogrado.backend.repository.MedicamentoRepository;
import com.proyectogrado.backend.repository.PersonaMayorRepository;
import com.proyectogrado.backend.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/medicamentos")
@CrossOrigin(origins = "http://localhost:4200")
public class MedicamentoController {

    private final MedicamentoRepository medicamentoRepository;
    private final PersonaMayorRepository personaMayorRepository;
    private final UsuarioRepository usuarioRepository;

    public MedicamentoController(MedicamentoRepository medicamentoRepository,
                                  PersonaMayorRepository personaMayorRepository,
                                  UsuarioRepository usuarioRepository) {
        this.medicamentoRepository = medicamentoRepository;
        this.personaMayorRepository = personaMayorRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public ResponseEntity<List<MedicamentoResponse>> listar(Authentication authentication) {
        PersonaMayor personaMayor = obtenerPersonaMayorActual(authentication);

        List<MedicamentoResponse> respuesta = medicamentoRepository
                .findByPersonaMayor_IdUsuario(personaMayor.getIdUsuario())
                .stream()
                .map(this::aRespuesta)
                .toList();

        return ResponseEntity.ok(respuesta);
    }

    @PostMapping
    public ResponseEntity<MedicamentoResponse> crear(@RequestBody MedicamentoRequest request,
                                                       Authentication authentication) {
        PersonaMayor personaMayor = obtenerPersonaMayorActual(authentication);

        Medicamento medicamento = new Medicamento();
        medicamento.setPersonaMayor(personaMayor);
        aplicarCambios(medicamento, request);
        recalcularProximaToma(medicamento);

        medicamento = medicamentoRepository.save(medicamento);

        return ResponseEntity.status(HttpStatus.CREATED).body(aRespuesta(medicamento));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicamentoResponse> actualizar(@PathVariable Integer id,
                                                            @RequestBody MedicamentoRequest request,
                                                            Authentication authentication) {
        PersonaMayor personaMayor = obtenerPersonaMayorActual(authentication);
        Medicamento medicamento = obtenerPropio(id, personaMayor);

        aplicarCambios(medicamento, request);
        recalcularProximaToma(medicamento);

        medicamento = medicamentoRepository.save(medicamento);

        return ResponseEntity.ok(aRespuesta(medicamento));
    }

    @PostMapping("/{id}/confirmar-toma")
    public ResponseEntity<MedicamentoResponse> confirmarToma(@PathVariable Integer id,
                                                               Authentication authentication) {
        PersonaMayor personaMayor = obtenerPersonaMayorActual(authentication);
        Medicamento medicamento = obtenerPropio(id, personaMayor);

        LocalDateTime ahora = LocalDateTime.now();
        medicamento.setUltimaToma(ahora);
        medicamento.setProximaToma(ahora.plusHours(medicamento.getIntervaloHoras()));
        medicamento.setUltimoRecordatorioEnviado(null);

        medicamento = medicamentoRepository.save(medicamento);

        return ResponseEntity.ok(aRespuesta(medicamento));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id, Authentication authentication) {
        PersonaMayor personaMayor = obtenerPersonaMayorActual(authentication);
        Medicamento medicamento = obtenerPropio(id, personaMayor);

        medicamentoRepository.delete(medicamento);

        return ResponseEntity.noContent().build();
    }

    private Medicamento obtenerPropio(Integer id, PersonaMayor personaMayor) {
        Medicamento medicamento = medicamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicamento no encontrado"));

        if (!medicamento.getPersonaMayor().getIdUsuario().equals(personaMayor.getIdUsuario())) {
            throw new RuntimeException("Este medicamento no pertenece a este usuario");
        }

        return medicamento;
    }

    private PersonaMayor obtenerPersonaMayorActual(Authentication authentication) {
        String identificador = authentication.getName();

        Usuario usuario = usuarioRepository.findByCorreo(identificador)
                .or(() -> usuarioRepository.findByTelefono(identificador))
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return personaMayorRepository.findById(usuario.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Este usuario no es una persona mayor"));
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
        LocalDate fecha = medicamento.getFechaInicio() != null ? medicamento.getFechaInicio() : LocalDate.now();
        LocalTime hora = medicamento.getHora() != null ? medicamento.getHora() : LocalTime.now();

        medicamento.setProximaToma(LocalDateTime.of(fecha, hora));
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