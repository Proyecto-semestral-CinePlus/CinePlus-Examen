package com.example.suscripcion.services;

import com.example.suscripcion.model.Suscripcion;
import com.example.suscripcion.repository.SuscripcionRepository;
import com.example.suscripcion.util.RecursoNoEncontradoException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class SuscripcionService {

    @Autowired
    private SuscripcionRepository suscripcionRepository;

    public List<Suscripcion> getSuscripciones() {
        return suscripcionRepository.findAll();
    }

    public Suscripcion getSuscripcionId(int id) {
        return suscripcionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Suscripción no encontrada con id: " + id));
    }

    public Suscripcion saveSuscripcion(Suscripcion suscripcion) {
        return suscripcionRepository.save(suscripcion);
    }

    public Suscripcion updateSuscripcion(int id, Suscripcion suscripcionNueva) {
        Suscripcion existente = suscripcionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Suscripción no encontrada con id: " + id));

        existente.setCategoria(suscripcionNueva.getCategoria());
        existente.setMensualidad(suscripcionNueva.getMensualidad());
        existente.setLimite(suscripcionNueva.getLimite());
        existente.setDescripcion(suscripcionNueva.getDescripcion());

        return suscripcionRepository.save(existente);
    }

    public void deleteSuscripcion(int id) {
        if (!suscripcionRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Suscripción no encontrada con id: " + id);
        }
        suscripcionRepository.deleteById(id);
    }
}