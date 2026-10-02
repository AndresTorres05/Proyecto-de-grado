package com.proyectogrado.persona_mayor_service.scheduler;

import com.proyectogrado.persona_mayor_service.client.MessagingClient;
import com.proyectogrado.persona_mayor_service.config.ZonaHoraria;
import com.proyectogrado.persona_mayor_service.model.PersonaMayorAcompanante;
import com.proyectogrado.persona_mayor_service.model.PersonaMayorLookup;
import com.proyectogrado.persona_mayor_service.model.PersonaMayorOrganizacion;
import com.proyectogrado.persona_mayor_service.model.UsuarioLookup;
import com.proyectogrado.persona_mayor_service.repository.PersonaMayorAcompananteRepository;
import com.proyectogrado.persona_mayor_service.repository.PersonaMayorLookupRepository;
import com.proyectogrado.persona_mayor_service.repository.PersonaMayorOrganizacionRepository;
import com.proyectogrado.persona_mayor_service.repository.UsuarioLookupRepository;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Month;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Todos los dias a las 8 a.m. (hora de Colombia), por cada persona mayor
 * que cumple años:
 * - VITA+ la felicita por SMS (que tambien queda en la campanita).
 * - Se les recuerda a sus acompañantes y organizaciones con relacion
 *   ACEPTADA para que la feliciten.
 *
 * Los nacidos un 29 de febrero reciben los mensajes el 28 en años no bisiestos.
 */
@Component
public class CumpleanosScheduler {

    private final PersonaMayorLookupRepository personaMayorLookupRepository;
    private final PersonaMayorAcompananteRepository relacionAcompananteRepository;
    private final PersonaMayorOrganizacionRepository relacionOrganizacionRepository;
    private final UsuarioLookupRepository usuarioLookupRepository;
    private final MessagingClient messagingClient;

    public CumpleanosScheduler(
            PersonaMayorLookupRepository personaMayorLookupRepository,
            PersonaMayorAcompananteRepository relacionAcompananteRepository,
            PersonaMayorOrganizacionRepository relacionOrganizacionRepository,
            UsuarioLookupRepository usuarioLookupRepository,
            MessagingClient messagingClient
    ) {
        this.personaMayorLookupRepository = personaMayorLookupRepository;
        this.relacionAcompananteRepository = relacionAcompananteRepository;
        this.relacionOrganizacionRepository = relacionOrganizacionRepository;
        this.usuarioLookupRepository = usuarioLookupRepository;
        this.messagingClient = messagingClient;
    }

    @Scheduled(cron = "0 0 8 * * *", zone = "America/Bogota")
    public void revisarCumpleanos() {
        revisarCumpleanos(ZonaHoraria.hoy());
    }

    // Separado para poder probar el flujo con cualquier fecha.
    void revisarCumpleanos(LocalDate hoy) {
        List<Integer> dias = (hoy.getMonth() == Month.FEBRUARY && hoy.getDayOfMonth() == 28 && !hoy.isLeapYear())
                ? List.of(28, 29)
                : List.of(hoy.getDayOfMonth());

        for (PersonaMayorLookup personaMayor
                : personaMayorLookupRepository.findCumpleanos(hoy.getMonthValue(), dias)) {
            try {
                avisar(personaMayor, hoy);
            } catch (Exception e) {
                System.out.println("[CUMPLEAÑOS] Error con persona mayor " + personaMayor.getIdUsuario()
                        + ": " + e.getMessage());
            }
        }
    }

    private void avisar(PersonaMayorLookup personaMayor, LocalDate hoy) {
        Integer idPersonaMayor = personaMayor.getIdUsuario();

        Optional<UsuarioLookup> usuario = usuarioLookupRepository.findById(idPersonaMayor);

        String nombre = usuario
                .map(UsuarioLookup::getNombreUsuario)
                .orElse("Una persona mayor que acompañas");

        // Felicitacion de VITA+ a la persona mayor
        usuario.map(UsuarioLookup::getCelular)
                .filter(celular -> !celular.isBlank())
                .ifPresent(celular -> enviar(idPersonaMayor, celular,
                        "¡Feliz cumpleaños, " + nombre
                                + "! Todo el equipo de VITA+ te desea un día lleno de alegría y salud."));

        // La fecha del perfil manda; si no la edito, se usa la del registro
        LocalDate fechaNacimiento = personaMayor.getFechaNacimiento() != null
                ? personaMayor.getFechaNacimiento()
                : usuario.map(UsuarioLookup::getFechaNacimiento).orElse(null);

        String recordatorio = fechaNacimiento != null
                ? "Hoy es el cumpleaños de " + nombre + ": cumple "
                        + (hoy.getYear() - fechaNacimiento.getYear())
                        + " años. ¡No olvides felicitarle desde VITA+!"
                : "Hoy es el cumpleaños de " + nombre + ". ¡No olvides felicitarle desde VITA+!";

        // Un Set por si el mismo celular aparece dos veces
        Set<String> celulares = new LinkedHashSet<>();

        for (PersonaMayorAcompanante relacion
                : relacionAcompananteRepository.findById_IdPersonaMayorAndEstado(idPersonaMayor, "ACEPTADA")) {

            usuarioLookupRepository.findById(relacion.getId().getIdAcompanante())
                    .map(UsuarioLookup::getCelular)
                    .filter(celular -> !celular.isBlank())
                    .ifPresent(celulares::add);
        }

        // Igual que la alerta de emergencia: el celular del primer usuario de la organizacion
        for (PersonaMayorOrganizacion relacion
                : relacionOrganizacionRepository.findById_IdPersonaMayorAndEstado(idPersonaMayor, "ACEPTADA")) {

            usuarioLookupRepository.findByIdOrganizacion(relacion.getId().getIdOrganizacion())
                    .stream()
                    .findFirst()
                    .map(UsuarioLookup::getCelular)
                    .filter(celular -> celular != null && !celular.isBlank())
                    .ifPresent(celulares::add);
        }

        for (String celular : celulares) {
            enviar(idPersonaMayor, celular, recordatorio);
        }
    }

    private void enviar(Integer idPersonaMayor, String celular, String mensaje) {
        boolean enviado = messagingClient.enviarMensaje(celular, mensaje);
        System.out.println("[CUMPLEAÑOS] Persona mayor " + idPersonaMayor + " -> " + celular
                + ": " + (enviado ? "OK" : "FALLO"));
    }
}
