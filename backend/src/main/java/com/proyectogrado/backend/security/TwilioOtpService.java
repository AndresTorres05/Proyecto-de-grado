package com.proyectogrado.backend.security;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TwilioOtpService {

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.phone-number}")
    private String numeroTwilio;

    private static final long MINUTOS_EXPIRACION = 5; // el template trial de Twilio dice "expira en 5 minutos"
    private static final Pattern PATRON_CODIGO = Pattern.compile("\\d{6}");

    private final Map<String, CodigoOtp> codigosPendientes = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
    }

    public void enviarCodigo(String telefono) {
        Message message = Message.creator(
                new PhoneNumber(telefono),
                new PhoneNumber(numeroTwilio),
                "sms_2fa"
        ).create();

        String codigo = extraerCodigo(message.getBody());
        codigosPendientes.put(telefono, new CodigoOtp(codigo, Instant.now().plusSeconds(MINUTOS_EXPIRACION * 60)));
    }

    public boolean verificarCodigo(String telefono, String codigo) {
        CodigoOtp guardado = codigosPendientes.get(telefono);

        if (guardado == null) {
            return false;
        }

        if (Instant.now().isAfter(guardado.expiracion())) {
            codigosPendientes.remove(telefono);
            return false;
        }

        boolean coincide = guardado.codigo().equals(codigo);
        if (coincide) {
            codigosPendientes.remove(telefono);
        }

        return coincide;
    }

    private String extraerCodigo(String body) {
        Matcher matcher = PATRON_CODIGO.matcher(body);
        if (matcher.find()) {
            return matcher.group();
        }
        throw new IllegalStateException("No se pudo leer el código del mensaje de Twilio");
    }

    private record CodigoOtp(String codigo, Instant expiracion) {
    }
}