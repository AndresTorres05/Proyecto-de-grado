package com.proyectogrado.backend.security;

import com.proyectogrado.backend.model.Medicamento;
import com.proyectogrado.backend.model.PersonaMayor;
import com.proyectogrado.backend.model.PersonaMayorAcompanante;
import com.proyectogrado.backend.repository.MedicamentoRepository;
import com.proyectogrado.backend.repository.PersonaMayorAcompananteRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Component
public class MedicamentoReminderScheduler {

    private static final long MINUTOS_ENTRE_REINTENTOS = 15;
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    // Un identificador único por cada vez que arranca el backend.
    // Si ves dos IDs distintos en el log al mismo tiempo, tienes DOS backends corriendo.
    private final String instanciaId = UUID.randomUUID().toString().substring(0, 8);

    private final MedicamentoRepository medicamentoRepository;
    private final PersonaMayorAcompananteRepository personaMayorAcompananteRepository;
    private final TextBeeOtpService textBeeOtpService;

    public MedicamentoReminderScheduler(MedicamentoRepository medicamentoRepository,
                                         PersonaMayorAcompananteRepository personaMayorAcompananteRepository,
                                         TextBeeOtpService textBeeOtpService) {
        this.medicamentoRepository = medicamentoRepository;
        this.personaMayorAcompananteRepository = personaMayorAcompananteRepository;
        this.textBeeOtpService = textBeeOtpService;
    }

    @PostConstruct
    public void alArrancar() {
        System.out.println("[SCHEDULER " + instanciaId + "] Instancia creada al arrancar el backend.");
    }

    @Scheduled(cron = "0 * * * * *") // cada minuto, en el segundo 0
    @Transactional
    public void revisarRecordatorios() {
        LocalDateTime ahora = LocalDateTime.now();

        System.out.println("[SCHEDULER " + instanciaId + "] Ejecutando revisarRecordatorios a las "
                + ahora.format(FORMATO));

        LocalDate hoy = LocalDate.now();

        List<Medicamento> vencidos = medicamentoRepository.findByActivoTrueAndProximaTomaLessThanEqual(ahora);

        System.out.println("[SCHEDULER " + instanciaId + "] Medicamentos vencidos encontrados: " + vencidos.size());

        for (Medicamento medicamento : vencidos) {
            if (medicamento.getFechaFin() != null && hoy.isAfter(medicamento.getFechaFin())) {
                continue;
            }

            LocalDateTime ultimoEnviado = medicamento.getUltimoRecordatorioEnviado();
            boolean debeReenviar = ultimoEnviado == null
                    || ultimoEnviado.isBefore(ahora.minusMinutes(MINUTOS_ENTRE_REINTENTOS));

            System.out.println("[SCHEDULER " + instanciaId + "] Medicamento id=" + medicamento.getIdMedicamento()
                    + " proximaToma=" + medicamento.getProximaToma()
                    + " ultimoRecordatorioEnviado=" + ultimoEnviado
                    + " debeReenviar=" + debeReenviar);

            if (debeReenviar) {
                enviarRecordatorio(medicamento);
                medicamento.setUltimoRecordatorioEnviado(ahora);
                medicamentoRepository.save(medicamento);
            }
        }

        System.out.println("[SCHEDULER " + instanciaId + "] Fin de la ejecución a las "
                + LocalDateTime.now().format(FORMATO));
    }

    private void enviarRecordatorio(Medicamento medicamento) {
        PersonaMayor personaMayor = medicamento.getPersonaMayor();
        String nombrePersona = personaMayor.getUsuario().getNombreUsuario();

        String telefonoPersona = personaMayor.getUsuario().getTelefono();
        if (telefonoPersona != null) {
            String mensaje = "Recordatorio: es hora de tomar " + medicamento.getNombre()
                    + (medicamento.getDosis() != null ? " (" + medicamento.getDosis() + ")" : "") + ".";
            intentarEnviar(telefonoPersona, mensaje);
        }

        List<PersonaMayorAcompanante> vinculos =
                personaMayorAcompananteRepository.findById_IdPersonaMayor(personaMayor.getIdUsuario());

        for (PersonaMayorAcompanante vinculo : vinculos) {
            String telefonoAcompanante = vinculo.getAcompanante().getUsuario().getTelefono();
            if (telefonoAcompanante != null) {
                String mensaje = "Recordatorio para " + nombrePersona + ": aún no ha tomado "
                        + medicamento.getNombre()
                        + (medicamento.getDosis() != null ? " (" + medicamento.getDosis() + ")" : "") + ".";
                intentarEnviar(telefonoAcompanante, mensaje);
            }
        }
    }

    private void intentarEnviar(String telefono, String mensaje) {
        System.out.println("[SCHEDULER " + instanciaId + "] Enviando SMS a " + telefono + " a las "
                + LocalDateTime.now().format(FORMATO));
        try {
            textBeeOtpService.enviarMensaje(telefono, mensaje);
            System.out.println("[SCHEDULER " + instanciaId + "] SMS enviado OK a " + telefono);
        } catch (Exception e) {
            System.out.println("No se pudo enviar recordatorio a " + telefono + ": " + e.getMessage());
        }
    }
}