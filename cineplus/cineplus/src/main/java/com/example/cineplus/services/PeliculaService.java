package com.example.cineplus.services;

import com.example.cineplus.model.Pelicula;
import com.example.cineplus.repository.PeliculaRepository;
import com.example.cineplus.util.RecursoNoEncontradoException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Transactional
public class PeliculaService {

    @Autowired
    private PeliculaRepository peliculaRepository;

    public List<Pelicula> getPeliculas() {
        log.info("Obteniendo todas las películas");
        return peliculaRepository.findAll();
    }

    public Pelicula getPeliculaId(int id) {
        log.info("Buscando película con id: {}", id);
        return peliculaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Película no encontrada con id: {}", id);
                    return new RecursoNoEncontradoException("Película no encontrada con id: " + id);
                });
    }

    public Pelicula savePelicula(Pelicula pelicula) {
        log.info("Guardando nueva película: {}", pelicula.getTitulo());
        return peliculaRepository.save(pelicula);
    }

    public Pelicula updatePelicula(int id, Pelicula peliculaNueva) {
        log.info("Actualizando película con id: {}", id);
        Pelicula existente = peliculaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Película no encontrada con id: {}", id);
                    return new RecursoNoEncontradoException("Película no encontrada con id: " + id);
                });

        existente.setTitulo(peliculaNueva.getTitulo());
        existente.setGenero(peliculaNueva.getGenero());
        existente.setClasificacion(peliculaNueva.getClasificacion());
        existente.setDuracion(peliculaNueva.getDuracion());
        existente.setSinopsis(peliculaNueva.getSinopsis());

        return peliculaRepository.save(existente);
    }

    public void deletePelicula(int id) {
        log.info("Eliminando película con id: {}", id);
        if (!peliculaRepository.existsById(id)) {
            log.warn("Película no encontrada con id: {}", id);
            throw new RecursoNoEncontradoException("Película no encontrada con id: " + id);
        }
        peliculaRepository.deleteById(id);
    }
}