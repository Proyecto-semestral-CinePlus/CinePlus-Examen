-- Script de migración Flyway para SQLite
 
CREATE TABLE usuarios (
    id       INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre   TEXT NOT NULL,
    apellido TEXT NOT NULL,
    correo   TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL
);
 
CREATE TABLE peliculas (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    titulo        TEXT NOT NULL,
    genero        TEXT NOT NULL,
    clasificacion TEXT NOT NULL,
    duracion      INTEGER NOT NULL,
    sinopsis      TEXT NOT NULL
);
 
CREATE TABLE entradas (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    id_pelicula INTEGER NOT NULL,
    sala        TEXT NOT NULL,
    asiento     TEXT NOT NULL,
    horario     TEXT NOT NULL,
    precio      REAL NOT NULL
);
 