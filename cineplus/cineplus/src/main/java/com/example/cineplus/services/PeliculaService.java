package com.example.cineplus.services;

import com.example.cineplus.model.Pelicula;
import com.example.cineplus.repository.PeliculaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class PeliculaService {

    @Autowired
    private PeliculaRepository peliculaRepository;

    // metodo: obtener todas las peliculas
    public List<Pelicula> getPeliculas() {
        return peliculaRepository.findAll();
    }

    // metodo: obtener una pelicula por id
    public Pelicula getPeliculaId(int id) {
        return peliculaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Película no encontrada"));
    }

    // metodo: crear una pelicula
    public Pelicula savePelicula(Pelicula pelicula) {
        return peliculaRepository.save(pelicula);
    }

    // metodo: actualizar una pelicula
    public Pelicula updatePelicula(int id, Pelicula peliculaNueva) {
        Pelicula peliculaExistente = peliculaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Película no encontrada"));

        peliculaExistente.setTitulo(peliculaNueva.getTitulo());
        peliculaExistente.setGenero(peliculaNueva.getGenero());
        peliculaExistente.setClasificacion(peliculaNueva.getClasificacion());
        peliculaExistente.setDuracion(peliculaNueva.getDuracion());
        peliculaExistente.setSinopsis(peliculaNueva.getSinopsis());

        return peliculaRepository.save(peliculaExistente);
    }

    // metodo: eliminar una pelicula
    public void deletePelicula(int id) {
        if (!peliculaRepository.existsById(id)) {
            throw new RuntimeException("Película no encontrada");
        }
        peliculaRepository.deleteById(id);
    }
}