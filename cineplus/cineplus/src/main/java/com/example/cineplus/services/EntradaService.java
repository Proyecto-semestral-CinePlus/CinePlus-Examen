package com.example.cineplus.services;

import com.example.cineplus.model.Entrada;
import com.example.cineplus.repository.EntradaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class EntradaService {

    @Autowired
    private EntradaRepository entradaRepository;

    // metodo para obtener todas las entradas
    public List<Entrada> getEntradas() {
        return entradaRepository.findAll();
    }

    // metodo para obtener una entrada por id
    public Entrada getEntradaId(int id) {
        return entradaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entrada no encontrada"));
    }

    // metodo para crear una entrada
    public Entrada saveEntrada(Entrada entrada) {
        return entradaRepository.save(entrada);
    }

    // metodo para actualizar una entrada
    public Entrada updateEntrada(int id, Entrada entradaNueva) {
        Entrada entradaExistente = entradaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entrada no encontrada"));

        entradaExistente.setIdPelicula(entradaNueva.getIdPelicula());
        entradaExistente.setSala(entradaNueva.getSala());
        entradaExistente.setAsiento(entradaNueva.getAsiento());
        entradaExistente.setHorario(entradaNueva.getHorario());
        entradaExistente.setPrecio(entradaNueva.getPrecio());

        return entradaRepository.save(entradaExistente);
    }

    // metodo para eliminar una entrada
    public void deleteEntrada(int id) {
        if (!entradaRepository.existsById(id)) {
            throw new RuntimeException("Entrada no encontrada");
        }
        entradaRepository.deleteById(id);
    }
}