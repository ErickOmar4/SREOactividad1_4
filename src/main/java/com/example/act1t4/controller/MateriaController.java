package com.example.act1t4.controller;

import com.example.act1t4.model.Materia;
import com.example.act1t4.service.MateriaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Controlador de materias (datos simulados).
 *
 * <p>Endpoints:</p>
 * <ul>
 *   <li>{@code GET /api/materias}: lista de materias en JSON.</li>
 *   <li>{@code GET /api/materias/{id}}: una materia, o 404 si no existe.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/materias")
public class MateriaController {

    private final MateriaService servicio;

    public MateriaController(MateriaService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<Materia> listar() {
        return servicio.listar();
    }

    @GetMapping("/{id}")
    public Materia obtener(@PathVariable("id") int id) {
        return servicio.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No existe la materia con id " + id));
    }
}
