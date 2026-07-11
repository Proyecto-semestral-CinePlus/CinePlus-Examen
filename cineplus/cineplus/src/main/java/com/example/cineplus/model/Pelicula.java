package com.example.cineplus.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "peliculas")
public class Pelicula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "El título no puede estar vacío")
    @Column(nullable = false)
    private String titulo;

    @NotBlank(message = "El género no puede estar vacío")
    @Column(nullable = false)
    private String genero;

    @NotBlank(message = "La clasificación no puede estar vacía")
    @Column(nullable = false)
    private String clasificacion;

    @Positive(message = "La duración debe ser mayor a 0")
    @Column(nullable = false)
    private int duracion; // en minutos

    @NotBlank(message = "La sinopsis no puede estar vacía")
    @Column(nullable = false)
    private String sinopsis;
}