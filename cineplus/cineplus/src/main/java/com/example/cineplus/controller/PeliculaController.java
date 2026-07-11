package com.example.cineplus.controller;

import com.example.cineplus.model.Pelicula;
import com.example.cineplus.services.PeliculaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/peliculas")
public class PeliculaController {

    @Autowired
    private PeliculaService peliculaService;

    // lista todas las peliculas
    @GetMapping
    public ResponseEntity<?> listarPeliculas() {
        List<Pelicula> peliculas = peliculaService.getPeliculas();
        if (peliculas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<EntityModel<Pelicula>> peliculasModel = peliculas.stream()
                .map(this::ensamblarModelo)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Pelicula>> collectionModel = CollectionModel.of(
                peliculasModel,
                linkTo(methodOn(PeliculaController.class).listarPeliculas()).withSelfRel()
        );

        return ResponseEntity.ok(collectionModel);
    }

    // Busca una pelicula por id
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPelicula(@PathVariable int id) {
        try {
            Pelicula pelicula = peliculaService.getPeliculaId(id);
            return ResponseEntity.ok(ensamblarModelo(pelicula));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Crea una nueva pelicula
    @PostMapping
    public ResponseEntity<?> agregarPelicula(@Valid @RequestBody Pelicula pelicula) {
        try {
            Pelicula guardada = peliculaService.savePelicula(pelicula);
            return ResponseEntity
                    .created(linkTo(methodOn(PeliculaController.class).buscarPelicula(guardada.getId())).toUri())
                    .body(ensamblarModelo(guardada));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    // Actualiza una pelicula existente
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarPelicula(@PathVariable int id, @Valid @RequestBody Pelicula pelicula) {
        try {
            Pelicula actualizada = peliculaService.updatePelicula(id, pelicula);
            return ResponseEntity.ok(ensamblarModelo(actualizada));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    // Elimina una pelicula
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarPelicula(@PathVariable int id) {
        try {
            peliculaService.deletePelicula(id);

            EntityModel<String> respuesta = EntityModel.of("Película eliminada",
                    linkTo(methodOn(PeliculaController.class).listarPeliculas()).withRel("peliculas")
            );

            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    // Método auxiliar para construir el HATEOAS
    private EntityModel<Pelicula> ensamblarModelo(Pelicula pelicula) {
        return EntityModel.of(pelicula,
                linkTo(methodOn(PeliculaController.class).buscarPelicula(pelicula.getId())).withSelfRel(),
                linkTo(methodOn(PeliculaController.class).listarPeliculas()).withRel("peliculas")
        );
    }
}