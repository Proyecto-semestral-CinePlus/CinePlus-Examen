package com.example.cineplus.services;

import com.example.cineplus.model.Entrada;
import com.example.cineplus.repository.EntradaRepository;
import com.example.cineplus.util.RecursoNoEncontradoException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class EntradaService {

    @Autowired
    private EntradaRepository entradaRepository;

    public List<Entrada> getEntradas() {
        return entradaRepository.findAll();
    }

    public Entrada getEntradaId(int id) {
        return entradaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Entrada no encontrada con id: " + id));
    }

    public Entrada saveEntrada(Entrada entrada) {
        return entradaRepository.save(entrada);
    }

    public Entrada updateEntrada(int id, Entrada entradaNueva) {
        Entrada existente = entradaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Entrada no encontrada con id: " + id));

        existente.setIdPelicula(entradaNueva.getIdPelicula());
        existente.setSala(entradaNueva.getSala());
        existente.setAsiento(entradaNueva.getAsiento());
        existente.setHorario(entradaNueva.getHorario());
        existente.setPrecio(entradaNueva.getPrecio());

        return entradaRepository.save(existente);
    }

    public void deleteEntrada(int id) {
        if (!entradaRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Entrada no encontrada con id: " + id);
        }
        entradaRepository.deleteById(id);
    }
}