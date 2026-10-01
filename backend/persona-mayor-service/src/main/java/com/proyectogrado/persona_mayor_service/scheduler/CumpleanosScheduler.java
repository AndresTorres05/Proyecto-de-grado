package com.proyectogrado.persona_mayor_service.scheduler;

import com.proyectogrado.persona_mayor_service.client.MessagingClient;
import com.proyectogrado.persona_mayor_service.config.ZonaHoraria;
import com.proyectogrado.persona_mayor_service.model.PersonaMayorAcompanante;
import com.proyectogrado.persona_mayor_service.model.PersonaMayorLookup;
import com.proyectogrado.persona_mayor_service.model.PersonaMayorOrganizacion;
import com.proyectogrado.persona_mayor_service.model.UsuarioLookup;
import com.proyectogrado.persona_mayor_service.repository.AvisoCumpleanosRepository;
import com.proyectogrado.persona_mayor_service.repository.FelicitacionCumpleanosRepository;
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
 * El dia del cumpleaños:
 * - VITA+ felicita por SMS (que tambien queda en la campanita) a todo
 *   usuario activo con fecha de nacimiento registrada, sin importar su rol.
 * - Si es una persona mayor, ademas avisa a sus acompañantes y
 *   organizaciones con relacion ACEPTADA.
 *
 * Corre cada hora de 8 a.m. a 8 p.m.: lo normal es que los mensajes salgan
 * a las 8, y si el servicio estaba apagado a esa hora salen en la siguiente
 * revision. Cada envio se reserva antes ("aviso_cumpleanos" y
 * "felicitacion_cumpleanos"), asi sale una sola vez por año.
 *
 * Los nacidos un 29 de febrero reciben los mensajes el 28 en años no bisiestos.
 */
@Component
public class CumpleanosScheduler {

    private final PersonaMayorLookupRepository personaMayorLookupRepository;
    private final AvisoCumpleanosRepository avisoCumpleanosRepository;
    private final FelicitacionCumpleanosRepository felicitacionCumpleanosRepository;
    private final PersonaMayorAcompananteRepository relacionAcompananteRepository;
    private final PersonaMayorOrganizacionRepository relacionOrganizacionRepository;
    private final UsuarioLookupRepository usuarioLookupRepository;
    private final MessagingClient messagingClient;

    public CumpleanosScheduler(
            PersonaMayorLookupRepository personaMayorLookupRepository,
            AvisoCumpleanosRepository avisoCumpleanosRepository,
            FelicitacionCumpleanosRepository felicitacionCumpleanosRepository,
            PersonaMayorAcompananteRepository relacionAcompananteRepository,
            PersonaMayorOrganizacionRepository relacionOrganizacionRepository,
            UsuarioLookupRepository usuarioLookupRepository,
            MessagingClient messagingClient
    ) {
        this.personaMayorLookupRepository = personaMayorLookupRepository;
        this.avisoCumpleanosRepository = avisoCumpleanosRepository;
        this.felicitacionCumpleanosRepository = felicitacionCumpleanosRepository;
        this.relacionAcompananteRepository = relacionAcompananteRepository;
        this.relacionOrganizacionRepository = relacionOrganizacionRepository;
        this.usuarioLookupRepository = usuarioLookupRepository;
        this.messagingClient = messagingClient;
    }

    @Scheduled(cron = "0 0 8-20 * * *", zone = "America/Bogota")
    public void revisarCumpleanos() {
        revisarCumpleanos(ZonaHoraria.hoy());
    }

    // Separado para poder probar el flujo con cualquier fecha.
    void revisarCumpleanos(LocalDate hoy) {
        List<Integer> dias = (hoy.getMonth() == Month.FEBRUARY && hoy.getDayOfMonth() == 28 && !hoy.isLeapYear())
                ? List.of(28, 29)
                : List.of(hoy.getDayOfMonth());

        felicitarCumpleaneros(hoy, dias);
        avisarCumpleanosPersonasMayores(hoy, dias);
    }

    private void felicitarCumpleaneros(LocalDate hoy, List<Integer> dias) {
        for (UsuarioLookup usuario
                : usuarioLookupRepository.findCumpleanos(hoy.getMonthValue(), dias)) {

            Integer idUsuario = usuario.getIdUsuario();
            String celular = usuario.getCelular();

            if (celular == null || celular.isBlank()) {
                continue;
            }

            try {
                if (felicitacionCumpleanosRepository.reservar(idUsuario, hoy.getYear()) == 1) {
                    String mensaje = "¡Feliz cumpleaños, " + usuario.getNombreUsuario()
                            + "! Todo el equipo de VITA+ te desea un día lleno de alegría y salud.";

                    boolean enviado = messagingClient.enviarMensaje(celular, mensaje);
                    System.out.println("[CUMPLEAÑOS] Felicitacion a usuario " + idUsuario + " -> " + celular
                            + ": " + (enviado ? "OK" : "FALLO"));
                }
            } catch (Exception e) {
                System.out.println("[CUMPLEAÑOS] Error felicitando al usuario " + idUsuario
                        + ": " + e.getMessage());
            }
        }
    }

    private void avisarCumpleanosPersonasMayores(LocalDate hoy, List<Integer> dias) {
        for (PersonaMayorLookup personaMayor
                : personaMayorLookupRepository.findCumpleanos(hoy.getMonthValue(), dias)) {

            Integer idPersonaMayor = personaMayor.getIdUsuario();

            try {
                if (avisoCumpleanosRepository.reservar(idPersonaMayor, hoy.getYear()) == 1) {
                    avisar(personaMayor, hoy);
                }
            } catch (Exception e) {
                System.out.println("[CUMPLEAÑOS] Error con persona mayor " + idPersonaMayor
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

        // La fecha del perfil manda; si no la edito, se usa la del registro
        LocalDate fechaNacimiento = personaMayor.getFechaNacimiento() != null
                ? personaMayor.getFechaNacimiento()
                : usuario.map(UsuarioLookup::getFechaNacimiento).orElse(null);

        String mensaje = fechaNacimiento != null
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
            boolean enviado = messagingClient.enviarMensaje(celular, mensaje);
            System.out.println("[CUMPLEAÑOS] Persona mayor " + idPersonaMayor + " -> " + celular
                    + ": " + (enviado ? "OK" : "FALLO"));
        }
    }
}
