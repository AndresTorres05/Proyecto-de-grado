package com.proyectogrado.api_gateway.eventos;

import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * Canal en memoria de avisos de cambio de datos.
 *
 * Cada aviso es solo el nombre del recurso que cambió ("actividades",
 * "medicamentos", ...), nunca los datos: al recibirlo, el frontend vuelve
 * a consultar sus endpoints de siempre, que ya validan los permisos.
 */
@Component
public class CambiosPublisher {

    /**
     * Sin buffer: quien no está conectado no necesita los avisos viejos,
     * porque al reconectarse el frontend recarga todo.
     */
    private final Sinks.Many<String> sink = Sinks.many().multicast().directBestEffort();

    /**
     * Avisa que cambió un recurso. Es synchronized porque el sink no admite
     * emisiones concurrentes y las respuestas llegan desde varios hilos de Netty.
     */
    public synchronized void publicar(String recurso) {
        sink.tryEmitNext(recurso);
    }

    /** Flujo de avisos al que se suscribe cada conexión de /api/eventos. */
    public Flux<String> flujo() {
        return sink.asFlux();
    }
}
