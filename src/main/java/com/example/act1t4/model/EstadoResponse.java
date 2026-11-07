package com.example.act1t4.model;

import java.time.OffsetDateTime;

/**
 * Respuesta del endpoint de estado: sirve para comprobar rápidamente de quién es
 * la instancia, en qué puerto corre y con qué versión de Java.
 *
 * @param estado      "OK" cuando la aplicación responde
 * @param proyecto    nombre del proyecto
 * @param autor       nombre de la persona autora
 * @param javaVersion versión de Java en la que se ejecuta
 * @param puerto      puerto configurado para el servidor embebido
 * @param fecha       fecha y hora del servidor al responder (ISO-8601)
 */
public record EstadoResponse(
        String estado,
        String proyecto,
        String autor,
        String javaVersion,
        String puerto,
        OffsetDateTime fecha) {
}
