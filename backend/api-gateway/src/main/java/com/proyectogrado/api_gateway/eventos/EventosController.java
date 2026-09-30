package com.proyectogrado.api_gateway.eventos;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;

/**
 * Stream SSE con los avisos de cambio de datos (ver CambiosPublisher).
 *
 * Lo atiende el propio gateway (no hay microservicio detras). El CORS se
 * declara aqui porque globalcors del application.yml solo aplica a las
 * rutas del gateway, no a los controladores.
 */
@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class EventosController {

    // Mantiene viva la conexion a traves de proxies y permite detectar
    // clientes que se fueron.
    private static final Duration LATIDO = Duration.ofSeconds(25);

    private final CambiosPublisher publisher;
    private final SecretKey signingKey;

    public EventosController(CambiosPublisher publisher, @Value("${jwt.secret}") String jwtSecret) {
        this.publisher = publisher;
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    @GetMapping(value = "/api/eventos", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> eventos(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {

        Date expiracion = validarToken(authHeader);

        Flux<ServerSentEvent<String>> cambios = publisher.flujo()
                .map(recurso -> ServerSentEvent.<String>builder(recurso).event("cambio").build());

        Flux<ServerSentEvent<String>> latidos = Flux.interval(LATIDO)
                .map(n -> ServerSentEvent.<String>builder().comment("latido").build());

        Flux<ServerSentEvent<String>> flujo = Flux.merge(cambios, latidos);

        // Al vencer el token se cierra el stream; el frontend intentara
        // reconectarse y recibira 401.
        if (expiracion != null) {
            Duration restante = Duration.ofMillis(Math.max(0, expiracion.getTime() - System.currentTimeMillis()));
            flujo = flujo.takeUntilOther(Mono.delay(restante));
        }

        return flujo;
    }

    private Date validarToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(authHeader.substring(7))
                    .getPayload();

            if (claims.get("idUsuario", Number.class) == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
            }

            return claims.getExpiration();

        } catch (JwtException | IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }
}
