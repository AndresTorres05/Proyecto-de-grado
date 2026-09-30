package com.proyectogrado.api_gateway.eventos;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Detecta cada escritura exitosa (POST/PUT/DELETE/PATCH con respuesta 2xx)
 * que pasa por el gateway y avisa que recurso cambio, para que los demas
 * usuarios vean el cambio sin recargar la pagina.
 *
 * Como todo el trafico del frontend pasa por aqui, los microservicios no
 * tienen que publicar nada. Si se agrega un endpoint nuevo que modifica
 * datos compartidos, basta con agregar su ruta en RECURSOS_POR_RUTA.
 */
@Component
public class PublicarCambiosGlobalFilter implements GlobalFilter, Ordered {

    private static final Set<HttpMethod> METODOS_DE_ESCRITURA =
            Set.of(HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.PATCH);

    // Se usa la primera coincidencia. Las rutas que no aparecen (login,
    // OTP, contrasena, emergencia) no cambian datos que vean otros.
    // "*" = cambia de todo (al borrar una cuenta se borran sus relaciones).
    private static final Map<String, String> RECURSOS_POR_RUTA = new LinkedHashMap<>();

    static {
        RECURSOS_POR_RUTA.put("/api/auth/cuenta", "*");
        RECURSOS_POR_RUTA.put("/api/auth/registro", "usuarios");
        RECURSOS_POR_RUTA.put("/api/auth/informacion", "usuarios");
        RECURSOS_POR_RUTA.put("/api/persona-mayor/perfil", "usuarios");
        RECURSOS_POR_RUTA.put("/api/organizacion/informacion", "usuarios");
        RECURSOS_POR_RUTA.put("/api/voluntario/**", "usuarios");

        RECURSOS_POR_RUTA.put("/api/actividades/**", "actividades");

        RECURSOS_POR_RUTA.put("/api/persona-mayor/medicamentos/**", "medicamentos");

        RECURSOS_POR_RUTA.put("/api/persona-mayor/signos-vitales/**", "signos-vitales");
        RECURSOS_POR_RUTA.put("/api/organizacion/signos-vitales/**", "signos-vitales");

        RECURSOS_POR_RUTA.put("/api/persona-mayor/acompanantes/**", "acompanamientos");
        RECURSOS_POR_RUTA.put("/api/acompanante/**", "acompanamientos");

        RECURSOS_POR_RUTA.put("/api/persona-mayor/organizaciones/**", "organizaciones");
        RECURSOS_POR_RUTA.put("/api/organizacion/personas-mayores/**", "organizaciones");

        RECURSOS_POR_RUTA.put("/api/gustos/**", "gustos");
        RECURSOS_POR_RUTA.put("/api/persona-mayor/*/gustos", "gustos");
    }

    private final AntPathMatcher matcher = new AntPathMatcher();
    private final CambiosPublisher publisher;

    public PublicarCambiosGlobalFilter(CambiosPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!METODOS_DE_ESCRITURA.contains(exchange.getRequest().getMethod())) {
            return chain.filter(exchange);
        }

        String recurso = resolverRecurso(exchange.getRequest().getPath().value());
        if (recurso == null) {
            return chain.filter(exchange);
        }

        // Se avisa despues de que el servicio respondio (el cambio ya
        // quedo guardado) y solo si la operacion fue exitosa.
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            HttpStatusCode estado = exchange.getResponse().getStatusCode();
            if (estado != null && estado.is2xxSuccessful()) {
                publisher.publicar(recurso);
            }
        }));
    }

    private String resolverRecurso(String ruta) {
        for (Map.Entry<String, String> regla : RECURSOS_POR_RUTA.entrySet()) {
            if (matcher.match(regla.getKey(), ruta)) {
                return regla.getValue();
            }
        }
        return null;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
