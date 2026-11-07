package com.example.act1t4.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Pruebas de los filtros opcionales de GET /api/materias (los usa la interfaz web). */
@SpringBootTest
@AutoConfigureMockMvc
class MateriaFiltrosTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void sinFiltrosDevuelveTodas() throws Exception {
        mockMvc.perform(get("/api/materias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5));
    }

    @Test
    void filtraPorNombreSinImportarMayusculasNiAcentos() throws Exception {
        mockMvc.perform(get("/api/materias").param("nombre", "PROGRAMACION"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void filtraPorCreditos() throws Exception {
        mockMvc.perform(get("/api/materias").param("creditos", "4"))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Redes de Computadoras"));
    }

    @Test
    void filtraPorSemestre() throws Exception {
        mockMvc.perform(get("/api/materias").param("semestre", "4"))
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void combinaFiltros() throws Exception {
        mockMvc.perform(get("/api/materias").param("creditos", "5").param("semestre", "4"))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Bases de Datos"));
    }

    @Test
    void sinCoincidenciasDevuelveListaVacia() throws Exception {
        mockMvc.perform(get("/api/materias").param("nombre", "inexistente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
