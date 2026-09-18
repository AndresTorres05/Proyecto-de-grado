package com.proyectogrado.api_gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

/**
 * Filtro de gateway que valida el JWT emitido por auth-backend.
 *
 * auth-backend es el UNICO que genera tokens. Este filtro no genera
 * nada, solo verifica la firma/expiracion y, si es valido, reemplaza
 * el header Authorization por headers internos (X-User-Id, X-User-Rol)
 * para que los servicios de abajo no tengan que saber nada de JWT.
 *
 * Uso en application.yml (shorthand, quitando el sufijo
 * GatewayFilterFactory del nombre de la clase):
 *   filters:
 *     - JwtAuth
 */
@Component
public class JwtAuthGatewayFilterFactory extends AbstractGatewayFilterFactory<JwtAuthGatewayFilterFactory.Config> {

    private final SecretKey signingKey;

    public JwtAuthGatewayFilterFactory(@Value("${jwt.secret}") String jwtSecret) {
        super(Config.class);
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            try {
                Claims claims = Jwts.parser()
                        .verifyWith(signingKey)
                        .build()
                        .parseSignedClaims(authHeader.substring(7))
                        .getPayload();

                Integer idUsuario = claims.get("idUsuario", Number.class) != null
                        ? claims.get("idUsuario", Number.class).intValue()
                        : null;

                String rol = claims.get("rol", String.class);

                if (idUsuario == null) {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }

                ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                        .header("X-User-Id", String.valueOf(idUsuario))
                        .header("X-User-Rol", rol == null ? "" : rol)
                        .build();

                return chain.filter(exchange.mutate().request(mutatedRequest).build());

            } catch (JwtException | IllegalArgumentException e) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        };
    }

    public static class Config {
        // Sin parametros por ahora
    }
}
