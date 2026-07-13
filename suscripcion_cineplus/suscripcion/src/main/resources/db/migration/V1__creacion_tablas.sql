CREATE TABLE suscripciones (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    categoria   TEXT NOT NULL,
    mensualidad REAL NOT NULL,
    limite      INTEGER NOT NULL,
    descripcion TEXT NOT NULL
);