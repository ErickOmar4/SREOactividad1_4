package com.example.act1t4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Punto de entrada de la aplicación.
 *
 * <ul>
 *   <li>{@code @SpringBootApplication}: activa la autoconfiguración, el escaneo de
 *       componentes del paquete {@code com.example.act1t4} y sus subpaquetes.</li>
 *   <li>{@code @ConfigurationPropertiesScan}: registra automáticamente las clases
 *       anotadas con {@code @ConfigurationProperties} (ver {@code ProyectoProperties}).</li>
 * </ul>
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class Act1T4Application {

    public static void main(String[] args) {
        SpringApplication.run(Act1T4Application.class, args);
    }
}
