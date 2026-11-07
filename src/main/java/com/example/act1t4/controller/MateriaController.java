package com.example.act1t4.controller;

import com.example.act1t4.model.Materia;
import com.example.act1t4.service.MateriaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

/**
 * Controlador de materias (datos simulados).
 *
 * <p>Endpoints:</p>
 * <ul>
 *   <li>{@code GET /api/materias}: lista de materias en JSON. Acepta filtros opcionales
 *       y combinables: {@code ?nombre=datos&creditos=5&semestre=4}.</li>
 *   <li>{@code GET /api/materias/{id}}: una materia, o 404 si no existe.</li>
 * </ul>
 *
 * <p>Sin parámetros se comporta igual que antes (devuelve todas las materias), por lo que
 * no rompe nada de lo ya construido. La interfaz web ({@code static/index.html}) usa estos filtros.</p>
 */
@RestController
@RequestMapping("/api/materias")
public class MateriaController {

    private final MateriaService servicio;

    public MateriaController(MateriaService servicio) {
        this.servicio = servicio;
    }

    /**
     * Lista las materias, con filtros opcionales.
     *
     * @param nombre   texto que debe contener el nombre (sin distinguir mayúsculas ni acentos)
     * @param creditos créditos exactos
     * @param semestre semestre exacto
     */
    @GetMapping
    public List<Materia> listar(
            @RequestParam(name = "nombre", required = false) String nombre,
            @RequestParam(name = "creditos", required = false) Integer creditos,
            @RequestParam(name = "semestre", required = false) Integer semestre) {

        String buscado = normalizar(nombre);
        return servicio.listar().stream()
                .filter(m -> buscado.isEmpty() || normalizar(m.nombre()).contains(buscado))
                .filter(m -> creditos == null || m.creditos() == creditos)
                .filter(m -> semestre == null || m.semestre() == semestre)
                .toList();
    }

    @GetMapping("/{id}")
    public Materia obtener(@PathVariable("id") int id) {
        return servicio.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No existe la materia con id " + id));
    }

    /** Pasa a minúsculas y quita acentos: "Programación" -> "programacion". */
    private static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .trim();
    }
}
