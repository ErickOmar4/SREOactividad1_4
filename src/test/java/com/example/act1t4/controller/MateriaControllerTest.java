package com.example.act1t4.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MateriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listarDevuelveUnaListaJsonDeMaterias() throws Exception {
        mockMvc.perform(get("/api/materias"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").isNotEmpty());
    }

    @Test
    void obtenerDevuelveLaMateriaSolicitada() throws Exception {
        mockMvc.perform(get("/api/materias/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.nombre").value("Bases de Datos"));
    }

    @Test
    void obtenerDevuelve404CuandoNoExiste() throws Exception {
        mockMvc.perform(get("/api/materias/99"))
                .andExpect(status().isNotFound());
    }
}
