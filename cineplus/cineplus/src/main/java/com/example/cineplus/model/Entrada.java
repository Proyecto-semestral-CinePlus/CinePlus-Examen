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
@Table(name = "entradas")
public class Entrada {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
 
    @Positive(message = "El id de película debe ser mayor a 0")
    @Column(nullable = false)
    private int idPelicula;
 
    @NotBlank(message = "La sala no puede estar vacía")
    @Column(nullable = false)
    private String sala;
 
    @NotBlank(message = "El asiento no puede estar vacío")
    @Column(nullable = false)
    private String asiento;
 
    @NotBlank(message = "El horario no puede estar vacío")
    @Column(nullable = false)
    private String horario;
 
    @Positive(message = "El precio debe ser mayor a 0")
    @Column(nullable = false)
    private double precio;
}
 