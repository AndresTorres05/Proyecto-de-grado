package com.proyectogrado.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generarToken(Integer idUsuario, String rol) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", rol);
        claims.put("idUsuario", idUsuario);

        return Jwts.builder()
                .claims(claims)
                .subject(String.valueOf(idUsuario))
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    public Integer extraerIdUsuario(String token) {
        return extraerClaim(
                token,
                claims -> claims.get("idUsuario", Integer.class)
        );
    }

    public String extraerRol(String token) {
        return extraerClaim(
                token,
                claims -> claims.get("rol", String.class)
        );
    }

    public boolean esTokenValido(String token, Integer idUsuarioEsperado) {
        Integer idUsuario = extraerIdUsuario(token);

        return idUsuario != null
                && idUsuario.equals(idUsuarioEsperado)
                && !esTokenExpirado(token);
    }

    private boolean esTokenExpirado(String token) {
        return extraerClaim(token, Claims::getExpiration)
                .before(new Date());
    }

    private <T> T extraerClaim(
            String token,
            Function<Claims, T> resolver
    ) {
        Claims claims = extraerTodosLosClaims(token);
        return resolver.apply(claims);
    }

    private Claims extraerTodosLosClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}