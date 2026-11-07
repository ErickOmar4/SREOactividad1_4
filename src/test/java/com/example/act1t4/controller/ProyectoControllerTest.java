package com.example.act1t4.controller;

import com.example.act1t4.config.ProyectoProperties;
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
class ProyectoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProyectoProperties propiedades;

    @Test
    void nombreDevuelveElNombreConfiguradoComoTexto() throws Exception {
        mockMvc.perform(get("/api/proyecto/nombre"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string(propiedades.nombre()));
    }

    @Test
    void estadoDevuelveUnObjetoJson() throws Exception {
        mockMvc.perform(get("/api/proyecto/estado"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.estado").value("OK"))
                .andExpect(jsonPath("$.proyecto").value(propiedades.nombre()))
                .andExpect(jsonPath("$.javaVersion").isNotEmpty())
                .andExpect(jsonPath("$.puerto").isNotEmpty());
    }
}
