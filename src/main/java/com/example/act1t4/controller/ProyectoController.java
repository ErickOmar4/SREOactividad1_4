package com.example.act1t4.controller;

import com.example.act1t4.config.ProyectoProperties;
import com.example.act1t4.model.EstadoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

/**
 * Controlador de identificación del proyecto.
 *
 * <p>Endpoints:</p>
 * <ul>
 *   <li>{@code GET /api/proyecto/nombre}: respuesta de texto plano.</li>
 *   <li>{@code GET /api/proyecto/estado}: respuesta JSON (objeto simple).</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/proyecto")
public class ProyectoController {

    private final ProyectoProperties propiedades;
    private final String puerto;

    // Inyección por constructor: Spring entrega los valores al crear el controlador.
    public ProyectoController(ProyectoProperties propiedades,
                              @Value("${server.port}") String puerto) {
        this.propiedades = propiedades;
        this.puerto = puerto;
    }

    /** Devuelve el nombre del proyecto para identificar la instancia. */
    @GetMapping(value = "/nombre", produces = MediaType.TEXT_PLAIN_VALUE)
    public String nombre() {
        return propiedades.nombre();
    }

    /** Devuelve un objeto JSON con el estado de la aplicación. */
    @GetMapping("/estado")
    public EstadoResponse estado() {
        return new EstadoResponse(
                "OK",
                propiedades.nombre(),
                propiedades.autor(),
                System.getProperty("java.version"),
                puerto,
                OffsetDateTime.now());
    }
}
