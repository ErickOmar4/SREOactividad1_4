package com.example.act1t4.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Propiedades propias del proyecto, leídas de {@code application.properties}
 * con el prefijo {@code proyecto.*}.
 *
 * <p>Al ser un {@code record} con un único constructor, Spring Boot enlaza
 * ("bind") cada propiedad con el componente del mismo nombre:
 * {@code proyecto.nombre} -> {@link #nombre()}.</p>
 *
 * @param nombre      nombre/identificador del proyecto (p. ej. INICIALESact1_t4)
 * @param autor       nombre de la persona autora
 * @param descripcion breve descripción del proyecto
 */
@ConfigurationProperties(prefix = "proyecto")
public record ProyectoProperties(String nombre, String autor, String descripcion) {
}
