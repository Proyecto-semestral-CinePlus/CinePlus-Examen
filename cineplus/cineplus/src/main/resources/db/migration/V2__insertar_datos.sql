-- Datos de prueba para la tabla usuarios (passwords encriptadas con BCrypt)
-- la password de todos es "1234"
INSERT INTO usuarios (nombre, apellido, correo, password) VALUES ('Juan', 'Pérez', 'juan.perez@mail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');
INSERT INTO usuarios (nombre, apellido, correo, password) VALUES ('María', 'García', 'maria.garcia@mail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');
INSERT INTO usuarios (nombre, apellido, correo, password) VALUES ('Carlos', 'Rodríguez', 'carlos.rodriguez@mail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');
INSERT INTO usuarios (nombre, apellido, correo, password) VALUES ('Ana', 'Martínez', 'ana.martinez@mail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');
INSERT INTO usuarios (nombre, apellido, correo, password) VALUES ('Pedro', 'López', 'pedro.lopez@mail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');

-- Datos de prueba para la tabla peliculas
INSERT INTO peliculas (titulo, genero, clasificacion, duracion, sinopsis) VALUES ('Inception', 'Ciencia Ficción', 'PG-13', 148, 'Un ladrón que roba secretos corporativos a través de la tecnología de sueños compartidos.');
INSERT INTO peliculas (titulo, genero, clasificacion, duracion, sinopsis) VALUES ('El Rey León', 'Animación', 'G', 88, 'Un joven príncipe león huye de su reino tras la muerte de su padre.');
INSERT INTO peliculas (titulo, genero, clasificacion, duracion, sinopsis) VALUES ('John Wick', 'Acción', 'R', 101, 'Un ex asesino a sueldo busca venganza contra los gángsters que mataron a su perro.');
INSERT INTO peliculas (titulo, genero, clasificacion, duracion, sinopsis) VALUES ('Interstellar', 'Ciencia Ficción', 'PG-13', 169, 'Un equipo de exploradores viaja a través de un agujero de gusano en busca de un nuevo hogar para la humanidad.');
INSERT INTO peliculas (titulo, genero, clasificacion, duracion, sinopsis) VALUES ('Coco', 'Animación', 'PG', 105, 'Un niño es transportado al reino de los muertos donde busca a su tatarabuelo músico.');

-- Datos de prueba para la tabla entradas
INSERT INTO entradas (id_pelicula, sala, asiento, horario, precio) VALUES (1, 'Sala 1', 'A5', '18:00', 4500);
INSERT INTO entradas (id_pelicula, sala, asiento, horario, precio) VALUES (1, 'Sala 1', 'A6', '18:00', 4500);
INSERT INTO entradas (id_pelicula, sala, asiento, horario, precio) VALUES (2, 'Sala 2', 'B3', '20:00', 3900);
INSERT INTO entradas (id_pelicula, sala, asiento, horario, precio) VALUES (3, 'Sala 3', 'C7', '21:30', 5000);
INSERT INTO entradas (id_pelicula, sala, asiento, horario, precio) VALUES (4, 'Sala 1', 'D2', '15:00', 4500);
INSERT INTO entradas (id_pelicula, sala, asiento, horario, precio) VALUES (5, 'Sala 2', 'E1', '17:00', 3900);