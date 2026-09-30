package com.proyectogrado.api_gateway.eventos;

import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * Canal en memoria de avisos de cambio de datos.
 *
 * Cada aviso es solo el nombre del recurso que cambio ("actividades",
 * "medicamentos", ...), nunca los datos: los clientes, al recibirlo,
 * vuelven a consultar sus endpoints de siempre, que ya validan permisos.
 */
@Component
public class CambiosPublisher {

    // Sin buffer: quien no esta conectado no necesita los avisos viejos
    // (al reconectarse el frontend recarga todo).
    private final Sinks.Many<String> sink = Sinks.many().multicast().directBestEffort();

    // synchronized: el sink no admite emisiones concurrentes y las
    // respuestas llegan desde varios hilos de Netty.
    public synchronized void publicar(String recurso) {
        sink.tryEmitNext(recurso);
    }

    public Flux<String> flujo() {
        return sink.asFlux();
    }
}
