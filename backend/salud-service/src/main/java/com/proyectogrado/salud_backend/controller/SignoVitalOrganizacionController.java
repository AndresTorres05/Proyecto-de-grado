package com.proyectogrado.salud_backend.controller;

import com.proyectogrado.salud_backend.dto.SignoVitalRequest;
import com.proyectogrado.salud_backend.dto.SignoVitalResponse;
import com.proyectogrado.salud_backend.model.PersonaMayorOrganizacion;
import com.proyectogrado.salud_backend.model.SignoVital;
import com.proyectogrado.salud_backend.repository.PersonaMayorOrganizacionRepository;
import com.proyectogrado.salud_backend.repository.SignoVitalRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.proyectogrado.salud_backend.model.UsuarioLookup;
import com.proyectogrado.salud_backend.repository.UsuarioLookupRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/organizacion/signos-vitales")
public class SignoVitalOrganizacionController {

    private final SignoVitalRepository signoVitalRepository;
    private final PersonaMayorOrganizacionRepository personaMayorOrganizacionRepository;
    private final UsuarioLookupRepository usuarioLookupRepository; 

    public SignoVitalOrganizacionController(
            SignoVitalRepository signoVitalRepository,
            PersonaMayorOrganizacionRepository personaMayorOrganizacionRepository,
            UsuarioLookupRepository usuarioLookupRepository
    ) {
        this.signoVitalRepository = signoVitalRepository;
        this.personaMayorOrganizacionRepository = personaMayorOrganizacionRepository;
        this.usuarioLookupRepository = usuarioLookupRepository;
    }

    // Límites de lo físicamente posible (fuera de esto es un error al
    // digitar). Deben coincidir con LIMITES de frontend/core/signos-vitales/rangos.ts.
    private static final double[] SISTOLICA = {60, 260};
    private static final double[] DIASTOLICA = {30, 160};
    private static final double[] PULSO = {30, 220};
    private static final double[] TEMPERATURA = {32, 43};
    private static final double[] OXIGENO = {50, 100};
    private static final double[] RESPIRACION = {5, 60};
    private static final double[] PESO = {20, 250};

    // Últimos 10 registros de la persona mayor, del más reciente al más antiguo.
    @GetMapping("/{idPersonaMayor}")
    public ResponseEntity<?> listarUltimos(
            @RequestHeader("X-User-Id") Integer idUsuarioOrganizacion,
            @PathVariable Integer idPersonaMayor
    ) {
        ResponseEntity<?> accesoDenegado =
                validarAcceso(idUsuarioOrganizacion, idPersonaMayor);

        if (accesoDenegado != null) {
            return accesoDenegado;
        }

        List<SignoVitalResponse> respuesta = signoVitalRepository
                .findTop10ByIdPersonaMayorOrderByFechaHoraDesc(idPersonaMayor)
                .stream()
                .map(this::aRespuesta)
                .toList();

        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/{idPersonaMayor}")
    public ResponseEntity<?> crear(
            @RequestHeader("X-User-Id") Integer idUsuarioOrganizacion,
            @PathVariable Integer idPersonaMayor,
            @RequestBody SignoVitalRequest request
    ) {
        ResponseEntity<?> accesoDenegado =
                validarAcceso(idUsuarioOrganizacion, idPersonaMayor);

        if (accesoDenegado != null) {
            return accesoDenegado;
        }

        String errorMedicion = validarMedicion(request);
        if (errorMedicion != null) {
            return ResponseEntity.badRequest().body(errorMedicion);
        }

        SignoVital signoVital = new SignoVital();

        signoVital.setIdPersonaMayor(idPersonaMayor);
        signoVital.setFechaHora(LocalDateTime.now());

        signoVital.setPresionSistolica(
                request.getPresionSistolica()
        );

        signoVital.setPresionDiastolica(
                request.getPresionDiastolica()
        );

        signoVital.setFrecuenciaCardiaca(
                request.getFrecuenciaCardiaca()
        );

        signoVital.setTemperatura(
                request.getTemperatura()
        );

        signoVital.setSaturacionOxigeno(
                request.getSaturacionOxigeno()
        );

        signoVital.setFrecuenciaRespiratoria(
                request.getFrecuenciaRespiratoria()
        );

        signoVital.setPeso(
                request.getPeso()
        );

        signoVital.setObservaciones(
                request.getObservaciones()
        );
signoVital = signoVitalRepository.saveAndFlush(signoVital);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(aRespuesta(signoVital));
    }

    // Devuelve la respuesta de error si la organización no puede acceder a
    // la persona mayor, o null si la persona está asociada y aceptada.
    /** Mensaje de error si la medición no es válida; null si está bien. */
    private String validarMedicion(SignoVitalRequest r) {
        Integer sis = r.getPresionSistolica();
        Integer dia = r.getPresionDiastolica();

        if (sis == null && dia == null && r.getFrecuenciaCardiaca() == null
                && r.getTemperatura() == null && r.getSaturacionOxigeno() == null
                && r.getFrecuenciaRespiratoria() == null && r.getPeso() == null) {
            return "Ingresa al menos un signo vital.";
        }

        if ((sis == null) != (dia == null)) {
            return "La presión arterial se registra completa: sistólica y diastólica.";
        }

        String error = fueraDeLimite("Presión sistólica", sis, SISTOLICA, "mmHg");
        if (error == null) error = fueraDeLimite("Presión diastólica", dia, DIASTOLICA, "mmHg");
        if (error == null) error = fueraDeLimite("Frecuencia cardíaca", r.getFrecuenciaCardiaca(), PULSO, "lpm");
        if (error == null) error = fueraDeLimite("Temperatura", r.getTemperatura(), TEMPERATURA, "°C");
        if (error == null) error = fueraDeLimite("Saturación de oxígeno", r.getSaturacionOxigeno(), OXIGENO, "%");
        if (error == null) error = fueraDeLimite("Frecuencia respiratoria", r.getFrecuenciaRespiratoria(), RESPIRACION, "rpm");
        if (error == null) error = fueraDeLimite("Peso", r.getPeso(), PESO, "kg");
        if (error != null) {
            return error;
        }

        if (sis != null && sis <= dia) {
            return "La presión sistólica debe ser mayor que la diastólica.";
        }

        return null;
    }

    private String fueraDeLimite(String nombre, Number valor, double[] limite, String unidad) {
        if (valor == null) {
            return null;
        }
        double v = valor.doubleValue();
        if (v < limite[0] || v > limite[1]) {
            return String.format("%s: %s %s no es un valor posible (debe estar entre %s y %s).",
                    nombre, valor, unidad, formatear(limite[0]), formatear(limite[1]));
        }
        return null;
    }

    private String formatear(double valor) {
        return valor == Math.floor(valor) ? String.valueOf((long) valor) : String.valueOf(valor);
    }

    private ResponseEntity<?> validarAcceso(
            Integer idUsuarioOrganizacion,
            Integer idPersonaMayor
    ) {
        Integer idOrganizacion = usuarioLookupRepository
                .findById(idUsuarioOrganizacion)
                .map(UsuarioLookup::getIdOrganizacion)
                .orElse(null);

        if (idOrganizacion == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El usuario no tiene una organización asociada");
        }

        PersonaMayorOrganizacion relacion =
                personaMayorOrganizacionRepository
                        .findById_IdPersonaMayorAndId_IdOrganizacionAndEstado(
                                idPersonaMayor,
                                idOrganizacion,
                                "ACEPTADA"
                        )
                        .orElse(null);

        if (relacion == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("La persona mayor no está asociada a esta organización");
        }

        return null;
    }

    private SignoVitalResponse aRespuesta(SignoVital signoVital) {

        DateTimeFormatter formato =
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        return new SignoVitalResponse(
                signoVital.getIdSignoVital(),
                signoVital.getFechaHora() != null
                        ? signoVital.getFechaHora().format(formato)
                        : null,
                signoVital.getPresionSistolica(),
                signoVital.getPresionDiastolica(),
                signoVital.getFrecuenciaCardiaca(),
                signoVital.getTemperatura(),
                signoVital.getSaturacionOxigeno(),
                signoVital.getFrecuenciaRespiratoria(),
                signoVital.getPeso(),
                signoVital.getObservaciones()
        );
    }
}