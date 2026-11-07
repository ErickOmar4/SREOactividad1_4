package com.example.act1t4.model;

/**
 * Modelo de datos simulado de una materia. Al ser un {@code record}, Jackson lo
 * convierte a JSON automáticamente:
 * {@code {"id":1,"nombre":"...","creditos":5,"semestre":1}}.
 *
 * @param id       identificador único
 * @param nombre   nombre de la materia
 * @param creditos créditos de la materia
 * @param semestre semestre en el que se cursa
 */
public record Materia(int id, String nombre, int creditos, int semestre) {
}
