package com.proyectogrado.actividad_service.scheduler;

import com.proyectogrado.actividad_service.client.MessagingClient;
import com.proyectogrado.actividad_service.model.Actividad;
import com.proyectogrado.actividad_service.model.Participacion;
import com.proyectogrado.actividad_service.model.UsuarioLookup;
import com.proyectogrado.actividad_service.repository.ActividadRepository;
import com.proyectogrado.actividad_service.repository.ParticipacionRepository;
import com.proyectogrado.actividad_service.repository.UsuarioLookupRepository;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Avisa por SMS a cada persona mayor inscrita en una actividad
 * 1 hora antes de que empiece.
 *
 * Los avisos ya enviados se recuerdan en memoria, con la fecha/hora de
 * la actividad en la clave: si la organizacion cambia la hora, se
 * vuelve a avisar para la nueva. Si el servicio se reinicia dentro de
 * esa hora, el aviso puede repetirse una vez.
 */
@Component
public class ActividadReminderScheduler {

    private static final long MINUTOS_ANTES = 60;

    private final ActividadRepository actividadRepository;
    private final ParticipacionRepository participacionRepository;
    private final UsuarioLookupRepository usuarioLookupRepository;
    private final MessagingClient messagingClient;

    // "idActividad|idPersonaMayor|fechaHoraActividad"
    private final Set<String> avisosEnviados = ConcurrentHashMap.newKeySet();

    public ActividadReminderScheduler(
            ActividadRepository actividadRepository,
            ParticipacionRepository participacionRepository,
            UsuarioLookupRepository usuarioLookupRepository,
            MessagingClient messagingClient
    ) {
        this.actividadRepository = actividadRepository;
        this.participacionRepository = participacionRepository;
        this.usuarioLookupRepository = usuarioLookupRepository;
        this.messagingClient = messagingClient;
    }

    @Scheduled(cron = "0 * * * * *") // cada minuto, en el segundo 0
    public void revisarRecordatorios() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime limite = ahora.plusMinutes(MINUTOS_ANTES);

        // hoy y mañana, por si la ventana cruza la medianoche
        List<Actividad> candidatas = actividadRepository.findByFechaBetween(
                ahora.toLocalDate(), limite.toLocalDate()
        );

        for (Actividad actividad : candidatas) {
            LocalDateTime inicio = inicioDe(actividad);

            // Solo las que empiezan dentro de la proxima hora
            if (inicio == null || !inicio.isAfter(ahora) || inicio.isAfter(limite)) {
                continue;
            }

            for (Participacion participacion
                    : participacionRepository.findById_IdActividad(actividad.getIdActividad())) {

                Integer idPersonaMayor = participacion.getId().getIdPersonaMayor();
                String clave = actividad.getIdActividad() + "|" + idPersonaMayor + "|" + inicio;

                if (!avisosEnviados.add(clave)) {
                    continue;
                }

                enviarRecordatorio(actividad, idPersonaMayor);
            }
        }

        // Olvidar avisos de actividades que ya empezaron
        avisosEnviados.removeIf(clave ->
                LocalDateTime.parse(clave.substring(clave.lastIndexOf('|') + 1)).isBefore(ahora));
    }

    private void enviarRecordatorio(Actividad actividad, Integer idPersonaMayor) {
        String celular = usuarioLookupRepository.findById(idPersonaMayor)
                .map(UsuarioLookup::getCelular)
                .orElse(null);

        if (celular == null || celular.isBlank()) {
            return;
        }

        String mensaje = "Recordatorio: tu actividad \"" + actividad.getNombre()
                + "\" empieza a las " + actividad.getHora()
                + (actividad.getLugar() != null && !actividad.getLugar().isBlank()
                        ? " en " + actividad.getLugar()
                        : "")
                + ".";

        boolean enviado = messagingClient.enviarMensaje(celular, mensaje);
        System.out.println("[RECORDATORIO ACTIVIDAD] Actividad " + actividad.getIdActividad()
                + " -> persona mayor " + idPersonaMayor + ": " + (enviado ? "OK" : "FALLO"));
    }

    // fecha + hora ("HH:mm" del formulario); null si falta o no se entiende
    private LocalDateTime inicioDe(Actividad actividad) {
        LocalDate fecha = actividad.getFecha();
        String hora = actividad.getHora();

        if (fecha == null || hora == null || hora.isBlank()) {
            return null;
        }

        try {
            return fecha.atTime(LocalTime.parse(hora.trim()));
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
