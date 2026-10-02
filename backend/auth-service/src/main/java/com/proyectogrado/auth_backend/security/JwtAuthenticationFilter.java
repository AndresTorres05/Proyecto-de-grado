package com.proyectogrado.auth_backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Lee el token de cada petición que llega a auth-service y, si es válido,
 * deja autenticado al usuario durante esa petición. Si no hay token o no
 * sirve, la petición sigue sin autenticar y SecurityConfig decide si pasa.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioDetailsService usuarioDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UsuarioDetailsService usuarioDetailsService
    ) {
        this.jwtService = jwtService;
        this.usuarioDetailsService = usuarioDetailsService;
    }


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // El preflight de CORS (OPTIONS) nunca trae token.
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {

            filterChain.doFilter(request, response);

            return;
        }

        final String authHeader =
                request.getHeader("Authorization");


        if (authHeader == null
                || !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);

            return;
        }

        // Quita el prefijo "Bearer ".
        final String token =
                authHeader.substring(7);


        try {

            Integer idUsuario =
                    jwtService.extraerIdUsuario(token);


            if (idUsuario == null) {

                System.out.println(
                        "JWT rechazado: no se encontró idUsuario"
                );

                filterChain.doFilter(request, response);

                return;
            }


            // Solo si nadie autenticó ya al usuario en esta petición.
            if (SecurityContextHolder
                    .getContext()
                    .getAuthentication() == null) {

                UserDetails userDetails =
                        usuarioDetailsService
                                .loadUserById(idUsuario);

                if (jwtService.esTokenValido(
                        token,
                        idUsuario
                )) {


                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );


                    authToken.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );


                    SecurityContextHolder
        .getContext()
        .setAuthentication(authToken);

System.out.println(
        "JWT autenticado correctamente. Usuario: "
                + idUsuario
);

System.out.println(
        "ROLES DEL USUARIO: "
                + userDetails.getAuthorities()
);

System.out.println(
        "RUTA SOLICITADA: "
                + request.getRequestURI()
);


                    System.out.println(
                            "JWT autenticado correctamente. Usuario: "
                                    + idUsuario
                    );

                } else {

                    System.out.println(
                            "JWT inválido o expirado"
                    );
                }
            }


        } catch (Exception e) {

            // Firma inválida, token vencido o usuario inexistente: la
            // petición sigue sin autenticar.
            System.out.println(
                    "Error procesando JWT: "
                            + e.getMessage()
            );
        }

        filterChain.doFilter(request, response);
    }
}