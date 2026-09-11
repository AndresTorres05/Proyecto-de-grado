package com.proyectogrado.backend.security;

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


        // =========================================================
        // PETICIONES OPTIONS
        // =========================================================

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {

            filterChain.doFilter(request, response);

            return;
        }


        // =========================================================
        // OBTENER HEADER AUTHORIZATION
        // =========================================================

        final String authHeader =
                request.getHeader("Authorization");


        if (authHeader == null
                || !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);

            return;
        }


        // =========================================================
        // EXTRAER TOKEN
        // =========================================================

        final String token =
                authHeader.substring(7);


        try {

            // -----------------------------------------------------
            // Extraer ID del usuario
            // -----------------------------------------------------

            Integer idUsuario =
                    jwtService.extraerIdUsuario(token);


            if (idUsuario == null) {

                System.out.println(
                        "JWT rechazado: no se encontró idUsuario"
                );

                filterChain.doFilter(request, response);

                return;
            }


            // -----------------------------------------------------
            // Comprobar si ya existe autenticación
            // -----------------------------------------------------

            if (SecurityContextHolder
                    .getContext()
                    .getAuthentication() == null) {

                        


                // -------------------------------------------------
                // Buscar usuario
                // -------------------------------------------------

                UserDetails userDetails =
                        usuarioDetailsService
                                .loadUserById(idUsuario);


                // -------------------------------------------------
                // Validar JWT
                // -------------------------------------------------

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

            System.out.println(
                    "Error procesando JWT: "
                            + e.getMessage()
            );
        }


        // =========================================================
        // CONTINUAR CADENA
        // =========================================================

        filterChain.doFilter(request, response);
    }
}