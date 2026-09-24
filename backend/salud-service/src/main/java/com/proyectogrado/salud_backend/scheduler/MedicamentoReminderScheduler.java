package com.proyectogrado.salud_backend.scheduler;

import com.proyectogrado.salud_backend.client.MessagingClient;
import com.proyectogrado.salud_backend.model.Medicamento;
import com.proyectogrado.salud_backend.model.RelacionAcompananteLookup;
import com.proyectogrado.salud_backend.model.UsuarioLookup;
import com.proyectogrado.salud_backend.repository.MedicamentoRepository;
import com.proyectogrado.salud_backend.repository.RelacionAcompananteLookupRepository;
import com.proyectogrado.salud_backend.repository.UsuarioLookupRepository;

import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Portado del monolito (MedicamentoReminderScheduler), con un ajuste:
 * el original avisaba a TODOS los vinculos de PersonaMayorAcompanante sin
 * mirar el estado (incluia solicitudes pendientes/rechazadas). Aqui solo
 * se avisa a los ACEPTADOS.
 */
@Component
public class MedicamentoReminderScheduler {

    private static final long MINUTOS_ENTRE_REINTENTOS = 15;
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    // Identificador unico por arranque, util para detectar si hay dos
    // instancias de este backend corriendo a la vez (dos schedulers).
    private final String instanciaId = UUID.randomUUID().toString().substring(0, 8);

    private final MedicamentoRepository medicamentoRepository;
    private final RelacionAcompananteLookupRepository relacionAcompananteLookupRepository;
    private final UsuarioLookupRepository usuarioLookupRepository;
    private final MessagingClient messagingClient;

    public MedicamentoReminderScheduler(
            MedicamentoRepository medicamentoRepository,
            RelacionAcompananteLookupRepository relacionAcompananteLookupRepository,
            UsuarioLookupRepository usuarioLookupRepository,
            MessagingClient messagingClient
    ) {
        this.medicamentoRepository = medicamentoRepository;
        this.relacionAcompananteLookupRepository = relacionAcompananteLookupRepository;
        this.usuarioLookupRepository = usuarioLookupRepository;
        this.messagingClient = messagingClient;
    }

    @PostConstruct
    public void alArrancar() {
        System.out.println("[SCHEDULER " + instanciaId + "] Instancia creada al arrancar salud-backend.");
    }

    @Scheduled(cron = "0 * * * * *") // cada minuto, en el segundo 0
    @Transactional
    public void revisarRecordatorios() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDate hoy = LocalDate.now();

        System.out.println("[SCHEDULER " + instanciaId + "] Ejecutando revisarRecordatorios a las "
                + ahora.format(FORMATO));

        List<Medicamento> vencidos = medicamentoRepository.findByActivoTrueAndProximaTomaLessThanEqual(ahora);

        System.out.println("[SCHEDULER " + instanciaId + "] Medicamentos vencidos encontrados: " + vencidos.size());

        for (Medicamento medicamento : vencidos) {

            if (medicamento.getFechaFin() != null && hoy.isAfter(medicamento.getFechaFin())) {
                continue;
            }

            LocalDateTime ultimoEnviado = medicamento.getUltimoRecordatorioEnviado();
            boolean debeReenviar = ultimoEnviado == null
                    || ultimoEnviado.isBefore(ahora.minusMinutes(MINUTOS_ENTRE_REINTENTOS));

            if (debeReenviar) {
                enviarRecordatorio(medicamento);
                medicamento.setUltimoRecordatorioEnviado(ahora);
                medicamentoRepository.save(medicamento);
            }
        }
    }

    private void enviarRecordatorio(Medicamento medicamento) {

        UsuarioLookup personaMayor = usuarioLookupRepository
                .findById(medicamento.getIdPersonaMayor())
                .orElse(null);

        String nombrePersona = personaMayor != null ? personaMayor.getNombreUsuario() : "la persona mayor";

        if (personaMayor != null && personaMayor.getCelular() != null) {
            String mensaje = "Recordatorio: es hora de tomar " + medicamento.getNombre()
                    + (medicamento.getDosis() != null ? " (" + medicamento.getDosis() + ")" : "") + ".";
            intentarEnviar(personaMayor.getCelular(), mensaje);
        }

        List<RelacionAcompananteLookup> vinculos = relacionAcompananteLookupRepository
                .findById_IdPersonaMayorAndEstado(medicamento.getIdPersonaMayor(), "ACEPTADA");

        for (RelacionAcompananteLookup vinculo : vinculos) {

            Integer idAcompanante = vinculo.getId().getIdAcompanante();

            String celularAcompanante = usuarioLookupRepository.findById(idAcompanante)
                    .map(UsuarioLookup::getCelular)
                    .orElse(null);

            if (celularAcompanante != null) {
                String mensaje = "Recordatorio para " + nombrePersona + ": aún no ha tomado "
                        + medicamento.getNombre()
                        + (medicamento.getDosis() != null ? " (" + medicamento.getDosis() + ")" : "") + ".";
                intentarEnviar(celularAcompanante, mensaje);
            }
        }
    }

    private void intentarEnviar(String celular, String mensaje) {
        boolean enviado = messagingClient.enviarMensaje(celular, mensaje);
        System.out.println("[SCHEDULER " + instanciaId + "] Envio a " + celular + ": "
                + (enviado ? "OK" : "FALLO"));
    }
}
