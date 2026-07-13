package com.example.cineplus.services;

import com.example.cineplus.model.Entrada;
import com.example.cineplus.repository.EntradaRepository;
import com.example.cineplus.util.RecursoNoEncontradoException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Transactional
public class EntradaService {

    @Autowired
    private EntradaRepository entradaRepository;

    public List<Entrada> getEntradas() {
        log.info("Obteniendo todas las entradas");
        return entradaRepository.findAll();
    }

    public Entrada getEntradaId(int id) {
        log.info("Buscando entrada con id: {}", id);
        return entradaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Entrada no encontrada con id: {}", id);
                    return new RecursoNoEncontradoException("Entrada no encontrada con id: " + id);
                });
    }

    public Entrada saveEntrada(Entrada entrada) {
        log.info("Guardando nueva entrada para película id: {}", entrada.getIdPelicula());
        return entradaRepository.save(entrada);
    }

    public Entrada updateEntrada(int id, Entrada entradaNueva) {
        log.info("Actualizando entrada con id: {}", id);
        Entrada existente = entradaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Entrada no encontrada con id: {}", id);
                    return new RecursoNoEncontradoException("Entrada no encontrada con id: " + id);
                });

        existente.setIdPelicula(entradaNueva.getIdPelicula());
        existente.setSala(entradaNueva.getSala());
        existente.setAsiento(entradaNueva.getAsiento());
        existente.setHorario(entradaNueva.getHorario());
        existente.setPrecio(entradaNueva.getPrecio());

        return entradaRepository.save(existente);
    }

    public void deleteEntrada(int id) {
        log.info("Eliminando entrada con id: {}", id);
        if (!entradaRepository.existsById(id)) {
            log.warn("Entrada no encontrada con id: {}", id);
            throw new RecursoNoEncontradoException("Entrada no encontrada con id: " + id);
        }
        entradaRepository.deleteById(id);
    }
}