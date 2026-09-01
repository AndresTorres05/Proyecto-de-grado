package com.proyectogrado.backend.security;

import com.proyectogrado.backend.model.Medicamento;
import com.proyectogrado.backend.model.PersonaMayor;
import com.proyectogrado.backend.model.PersonaMayorAcompanante;
import com.proyectogrado.backend.repository.MedicamentoRepository;
import com.proyectogrado.backend.repository.PersonaMayorAcompananteRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class MedicamentoReminderScheduler {

    private static final long MINUTOS_ENTRE_REINTENTOS = 15;

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

    @Scheduled(cron = "0 * * * * *") // cada minuto
    @Transactional
    public void revisarRecordatorios() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDate hoy = LocalDate.now();

        List<Medicamento> vencidos = medicamentoRepository.findByActivoTrueAndProximaTomaLessThanEqual(ahora);

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
        try {
            textBeeOtpService.enviarMensaje(telefono, mensaje);
        } catch (Exception e) {
            System.out.println("No se pudo enviar recordatorio a " + telefono + ": " + e.getMessage());
        }
    }
}