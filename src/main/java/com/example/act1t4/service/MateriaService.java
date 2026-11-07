package com.example.act1t4.service;

import com.example.act1t4.model.Materia;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Capa de servicio: contiene la lógica de negocio y el acceso a los datos.
 * En esta actividad los datos están simulados en memoria; en un proyecto real
 * aquí se consultaría un repositorio (base de datos).
 */
@Service
public class MateriaService {

    private final List<Materia> materias = List.of(
            new Materia(1, "Programación Orientada a Objetos", 5, 3),
            new Materia(2, "Bases de Datos", 5, 4),
            new Materia(3, "Redes de Computadoras", 4, 4),
            new Materia(4, "Sistemas Operativos", 5, 5),
            new Materia(5, "Ingeniería de Software", 5, 6)
    );

    /** @return todas las materias (lista inmutable). */
    public List<Materia> listar() {
        return materias;
    }

    /**
     * @param id identificador buscado
     * @return la materia si existe, o {@link Optional#empty()} si no
     */
    public Optional<Materia> buscarPorId(int id) {
        return materias.stream()
                .filter(materia -> materia.id() == id)
                .findFirst();
    }
}
