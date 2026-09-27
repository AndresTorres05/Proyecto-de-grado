
package com.proyectogrado.auth_backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UsuarioDetailsService usuarioDetailsService;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            UsuarioDetailsService usuarioDetailsService
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.usuarioDetailsService = usuarioDetailsService;
    }

    // =========================================================
    // PASSWORD ENCODER
    // =========================================================

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // =========================================================
    // AUTHENTICATION PROVIDER
    // =========================================================

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(usuarioDetailsService);

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    // =========================================================
    // AUTHENTICATION MANAGER
    // =========================================================

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {

        return config.getAuthenticationManager();
    }

    // =========================================================
    // SECURITY FILTER CHAIN
    // =========================================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

                // -------------------------------------------------
                // CSRF
                // -------------------------------------------------

                .csrf(csrf -> csrf.disable())

                // -------------------------------------------------
                // SESIONES
                // -------------------------------------------------

                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // -------------------------------------------------
                // AUTORIZACIÓN
                // -------------------------------------------------

                .authorizeHttpRequests(auth -> auth

                        // Login, registro y OTP: públicos.
                        // Son la puerta para conseguir el token,
                        // no lo requieren.

                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/login-otp",
                                "/api/auth/registro",
                                "/api/auth/celular-existe",
                                "/api/auth/restablecer-contrasena",
                                "/api/auth/validar-correo",
                                "/error"
                        )
                        .permitAll()

                        // Cualquier otra cosa bajo /api/auth
                        // requiere autenticación.

                        .requestMatchers("/api/auth/**")
                        .authenticated()

                        // Todo lo relacionado con persona mayor.

                        .requestMatchers("/api/persona-mayor/**")
                        .authenticated()

                        // Todo lo relacionado con organización.

                        .requestMatchers("/api/organizacion/**")
                        .authenticated()

                        // Todo lo relacionado con acompañante.

                        .requestMatchers("/api/acompanante/**")
                        .authenticated()

                        // Todo lo demás necesita autenticación.

                        .anyRequest()
                        .authenticated()
                )

                // -------------------------------------------------
                // AUTHENTICATION PROVIDER
                // -------------------------------------------------

                .authenticationProvider(authenticationProvider())

                // -------------------------------------------------
                // JWT FILTER
                // -------------------------------------------------

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}