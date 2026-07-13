package com.example.suscripcion.controller;

import com.example.suscripcion.model.Suscripcion;
import com.example.suscripcion.services.SuscripcionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/suscripciones")
public class SuscripcionController {

    @Autowired
    private SuscripcionService suscripcionService;

    @GetMapping
    public ResponseEntity<List<Suscripcion>> listarSuscripciones() {
        List<Suscripcion> suscripciones = suscripcionService.getSuscripciones();
        if (suscripciones.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(suscripciones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Suscripcion> buscarSuscripcion(@PathVariable int id) {
        Suscripcion suscripcion = suscripcionService.getSuscripcionId(id);
        return ResponseEntity.ok(suscripcion);
    }

    @PostMapping
    public ResponseEntity<Suscripcion> agregarSuscripcion(@Valid @RequestBody Suscripcion suscripcion) {
        Suscripcion guardada = suscripcionService.saveSuscripcion(suscripcion);
        return ResponseEntity.status(201).body(guardada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Suscripcion> actualizarSuscripcion(@PathVariable int id, @Valid @RequestBody Suscripcion suscripcion) {
        Suscripcion actualizada = suscripcionService.updateSuscripcion(id, suscripcion);
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminarSuscripcion(@PathVariable int id) {
        suscripcionService.deleteSuscripcion(id);
        return ResponseEntity.ok(Map.of("mensaje", "Suscripción eliminada correctamente"));
    }
}