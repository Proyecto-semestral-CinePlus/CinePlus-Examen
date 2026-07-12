package com.example.cineplus.services;

import com.example.cineplus.model.Pelicula;
import com.example.cineplus.repository.PeliculaRepository;
import com.example.cineplus.util.RecursoNoEncontradoException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class PeliculaService {

    @Autowired
    private PeliculaRepository peliculaRepository;

    public List<Pelicula> getPeliculas() {
        return peliculaRepository.findAll();
    }

    public Pelicula getPeliculaId(int id) {
        return peliculaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Película no encontrada con id: " + id));
    }

    public Pelicula savePelicula(Pelicula pelicula) {
        return peliculaRepository.save(pelicula);
    }

    public Pelicula updatePelicula(int id, Pelicula peliculaNueva) {
        Pelicula existente = peliculaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Película no encontrada con id: " + id));

        existente.setTitulo(peliculaNueva.getTitulo());
        existente.setGenero(peliculaNueva.getGenero());
        existente.setClasificacion(peliculaNueva.getClasificacion());
        existente.setDuracion(peliculaNueva.getDuracion());
        existente.setSinopsis(peliculaNueva.getSinopsis());

        return peliculaRepository.save(existente);
    }

    public void deletePelicula(int id) {
        if (!peliculaRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Película no encontrada con id: " + id);
        }
        peliculaRepository.deleteById(id);
    }
}