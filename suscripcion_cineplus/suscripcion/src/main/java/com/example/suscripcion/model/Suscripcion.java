package com.example.suscripcion.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "suscripciones")
public class Suscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "La categoría no puede estar vacía")
    @Column(nullable = false)
    private String categoria; // Ej: "Plata", "Oro"

    @Positive(message = "La mensualidad debe ser mayor a 0")
    @Column(nullable = false)
    private double mensualidad; // El valor del plan

    @Positive(message = "El límite debe ser mayor a 0")
    @Column(nullable = false)
    private int limite; // Cantidad de perfiles o pantallas

    @NotBlank(message = "La descripción no puede estar vacía")
    @Column(nullable = false)
    private String descripcion; // "Acceso a todo el catálogo"
}