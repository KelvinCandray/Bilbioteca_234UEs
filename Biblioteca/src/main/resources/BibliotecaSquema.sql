-- DDL para biblioteca.db (SQLite)
-- Ejecutar este script para crear o recrear la base de datos

PRAGMA foreign_keys = ON;

-- ─── PERSONAS ────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS personas (
    id_persona      INTEGER PRIMARY KEY,
    primer_nombre   TEXT NOT NULL,
    apellido        TEXT NOT NULL,
    correo          TEXT NOT NULL UNIQUE,
    telefono        TEXT,
    fecha_nacimiento TEXT NOT NULL,
    pasaje          TEXT,
    numero_casa     TEXT,
    colonia         TEXT,
    municipio       TEXT,
    departamento    TEXT
);

-- ─── EMPLEADOS ───────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS empleados (
    id_persona      INTEGER PRIMARY KEY,
    salario         REAL NOT NULL CHECK (salario >= 408.80),
    tipo_empleado   TEXT NOT NULL CHECK (tipo_empleado IN ('Gerente', 'Bibliotecario', 'Vigilante')),
    usuario         TEXT NOT NULL UNIQUE,
    contrasena      TEXT NOT NULL,
    FOREIGN KEY (id_persona) REFERENCES personas(id_persona) ON DELETE CASCADE
);

-- ─── USUARIOS ────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS usuarios (
    id_persona  INTEGER PRIMARY KEY,
    usuario     TEXT NOT NULL UNIQUE,
    contrasena  TEXT NOT NULL,
    FOREIGN KEY (id_persona) REFERENCES personas(id_persona) ON DELETE RESTRICT
);

-- ─── LIBROS ──────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS libros (
    ISBN            TEXT PRIMARY KEY,
    titulo          TEXT NOT NULL,
    editorial       TEXT,
    anio            INTEGER,
    tipo_edicion    TEXT
);

-- ─── AUTORES ─────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS autores (
    id_autor    INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre      TEXT NOT NULL,
    apellido    TEXT NOT NULL,
    nacionalidad TEXT
);

-- ─── CATEGORIAS ──────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS categorias (
    id_categoria     INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre_categoria TEXT NOT NULL UNIQUE,
    edad_minima      INTEGER NOT NULL DEFAULT 0
);

-- ─── TABLAS INTERMEDIAS ──────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS libro_autor (
    ISBN        TEXT NOT NULL,
    id_autor    INTEGER NOT NULL,
    PRIMARY KEY (ISBN, id_autor),
    FOREIGN KEY (ISBN)      REFERENCES libros(ISBN)   ON DELETE CASCADE,
    FOREIGN KEY (id_autor)  REFERENCES autores(id_autor) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS libro_categoria (
    ISBN          TEXT NOT NULL,
    id_categoria  INTEGER NOT NULL,
    PRIMARY KEY (ISBN, id_categoria),
    FOREIGN KEY (ISBN)         REFERENCES libros(ISBN)      ON DELETE CASCADE,
    FOREIGN KEY (id_categoria) REFERENCES categorias(id_categoria) ON DELETE CASCADE
);

-- ─── EJEMPLARES ──────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS ejemplares (
    id_ejemplar INTEGER PRIMARY KEY AUTOINCREMENT,
    ISBN        TEXT NOT NULL,
    estado      TEXT NOT NULL DEFAULT 'Disponible'
                CHECK (estado IN ('Disponible', 'Prestado', 'Dañado', 'Perdido')),
    FOREIGN KEY (ISBN) REFERENCES libros(ISBN) ON DELETE RESTRICT
);

-- ─── PRÉSTAMOS ───────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS prestamos (
    id_prestamo         INTEGER PRIMARY KEY AUTOINCREMENT,
    id_usuario          INTEGER NOT NULL,
    id_empleado         INTEGER,
    id_ejemplar         INTEGER NOT NULL,
    estado              TEXT NOT NULL DEFAULT 'Solicitado'
                        CHECK (estado IN ('Solicitado', 'Prestado', 'Retrasado', 'Devuelto', 'Rechazado')),
    fecha_solicitud     TEXT NOT NULL DEFAULT (DATE('now')),
    fecha_retiro        TEXT,
    fecha_ideal_regreso TEXT,
    fecha_real_regreso  TEXT,
    FOREIGN KEY (id_usuario)  REFERENCES usuarios(id_persona)  ON DELETE RESTRICT,
    FOREIGN KEY (id_empleado) REFERENCES empleados(id_persona) ON DELETE RESTRICT,
    FOREIGN KEY (id_ejemplar) REFERENCES ejemplares(id_ejemplar) ON DELETE RESTRICT
);

-- ─── MULTAS ──────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS multas (
    id_multa            INTEGER PRIMARY KEY AUTOINCREMENT,
    id_prestamo         INTEGER NOT NULL,
    tipo_multa          TEXT NOT NULL CHECK (tipo_multa IN ('Retraso', 'Daño')),
    fecha_generacion    TEXT NOT NULL DEFAULT (DATE('now')),
    fecha_maxima_pagar  TEXT NOT NULL,
    fecha_real_pago     TEXT,
    monto               REAL NOT NULL CHECK (monto >= 0),
    estado              TEXT NOT NULL DEFAULT 'Pendiente'
                        CHECK (estado IN ('Pendiente', 'Retrasada', 'Pagada')),
    FOREIGN KEY (id_prestamo) REFERENCES prestamos(id_prestamo) ON DELETE RESTRICT
);

-- ─── DATOS INICIALES (gerente por defecto) ───────────────────────────────────
INSERT OR IGNORE INTO personas (id_persona, primer_nombre, apellido, correo, telefono, fecha_nacimiento)
VALUES (1, 'Admin', 'Sistema', 'admin@biblioteca.com', '00000000', '1990-01-01');

INSERT OR IGNORE INTO empleados (id_persona, salario, tipo_empleado, usuario, contrasena)
VALUES (1, 5000.00, 'Gerente', 'admin', 'admin123');

-- ─── MÁS PERSONAS DE PRUEBA ──────────────────────────────────────────────────
-- IDs del 2 al 5 serán Empleados, del 6 al 10 serán Usuarios (Lectores)
INSERT OR IGNORE INTO personas (id_persona, primer_nombre, apellido, correo, telefono, fecha_nacimiento, municipio, departamento) VALUES
(2, 'Carlos', 'Mendoza', 'carlos.mendoza@biblioteca.com', '71234567', '1988-05-12', 'San Salvador', 'San Salvador'),
(3, 'Elena', 'Palacios', 'elena.palacios@biblioteca.com', '72345678', '1995-11-23', 'Soyapango', 'San Salvador'),
(4, 'Jorge', 'Alvarado', 'jorge.alvarado@biblioteca.com', '61234567', '1982-02-14', 'Santa Tecla', 'La Libertad'),
(5, 'Mariana', 'Gómez', 'mariana.gomez@biblioteca.com', '60123456', '1997-08-30', 'Antiguo Cuscatlán', 'La Libertad'),
-- Personas que solo serán Usuarios (Lectores)
(6, 'Kevin', 'Zacarias', 'kevin.jair@gmail.com', '75432109', '2002-11-08', 'San Salvador', 'San Salvador'),
(7, 'Gabriela', 'Benítez', 'gaby.benitez@outlook.com', '78901234', '2001-03-15', 'Mejicanos', 'San Salvador'),
(8, 'Luis', 'Fernando', 'luisfer.99@gmail.com', '70896745', '1999-07-20', 'Santa Ana', 'Santa Ana'),
(9, 'Andrea', 'Rivas', 'andrea.rivas@hotmail.com', '61524376', '2003-12-05', 'San Miguel', 'San Miguel'),
(10, 'Diego', 'Martínez', 'diego.mtz@gmail.com', '74321098', '2000-01-25', 'Delgado', 'San Salvador');

-- ─── MÁS EMPLEADOS ───────────────────────────────────────────────────────────
-- (Recuerda que el id_persona debe existir en la tabla personas)
INSERT OR IGNORE INTO empleados (id_persona, salario, tipo_empleado, usuario, contrasena) VALUES
(2, 450.00, 'Bibliotecario', 'carlosM', 'biblio123'),
(3, 450.00, 'Bibliotecario', 'elenaP', 'read456'),
(4, 410.00, 'Vigilante', 'jorgeA', 'seguridad789'),
(5, 450.00, 'Bibliotecario', 'marianaG', 'books2026');

-- ─── MÁS USUARIOS (LECTORES) ─────────────────────────────────────────────────
INSERT OR IGNORE INTO usuarios (id_persona, usuario, contrasena) VALUES
(6, 'kevinZ', 'clavel789'),
(7, 'gabyB', 'pass2026'),
(8, 'luisF', 'user99*'),
(9, 'andreaR', 'andy123'),
(10, 'diegoM', 'password55');

-- ─── AUTORES ─────────────────────────────────────────────────────────────────
INSERT OR IGNORE INTO autores (id_autor, nombre, apellido, nacionalidad) VALUES
(1, 'Gabriel', 'García Márquez', 'Colombiana'),
(2, 'Miguel', 'de Cervantes', 'Española'),
(3, 'George', 'Orwell', 'Británica'),
(4, 'J.K.', 'Rowling', 'Británica'),
(5, 'Antoine', 'de Saint-Exupéry', 'Francesa'),
(6, 'Stephen', 'King', 'Estadounidense'),
(7, 'Manlio', 'Argueta', 'Salvadoreña'),
(8, 'Claudia', 'Lars', 'Salvadoreña');

-- ─── CATEGORIAS ──────────────────────────────────────────────────────────────
INSERT OR IGNORE INTO categorias (id_categoria, nombre_categoria, edad_minima) VALUES
(1, 'Novela Realismo Mágico', 12),
(2, 'Clásicos de la Literatura', 10),
(3, 'Distopía y Ciencia Ficción', 14),
(4, 'Fantasía y Aventura', 8),
(5, 'Infantil', 0),
(6, 'Terror y Suspenso', 18),
(7, 'Literatura Salvadoreña', 12),
(8, 'Poesía', 0);

-- ─── LIBROS ──────────────────────────────────────────────────────────────────
INSERT OR IGNORE INTO libros (ISBN, titulo, editorial, anio, tipo_edicion) VALUES
('978-0307474728', 'Cien años de soledad', 'Editorial Sudamericana', 1967, 'Tapa Blanda'),
('978-8467033472', 'Don Quijote de la Mancha', 'Real Academia Española', 1605, 'Tapa Dura'),
('978-0451524935', '1984', 'Secker & Warburg', 1949, 'Especial'),
('978-0747532699', 'Harry Potter y la piedra filosofal', 'Bloomsbury', 1997, 'Tapa Blanda'),
('978-0156012195', 'El Principito', 'Reynal & Hitchcock', 1943, 'Bolsillo'),
('978-1501142970', 'It (Eso)', 'Viking Press', 1986, 'Tapa Blanda'),
('978-9992301321', 'Un día en la vida', 'UCA Editores', 1980, 'Económica'),
('978-9992302106', 'Tierra de Infancia', 'Dirección de Publicaciones e Impresos', 1958, 'Ilustrada');

-- ─── ASOCIACIÓN LIBRO Y AUTOR (libro_autor) ──────────────────────────────────
INSERT OR IGNORE INTO libro_autor (ISBN, id_autor) VALUES
('978-0307474728', 1), -- Cien años de soledad -> García Márquez
('978-8467033472', 2), -- Don Quijote -> Cervantes
('978-0451524935', 3), -- 1984 -> Orwell
('978-0747532699', 4), -- Harry Potter -> Rowling
('978-0156012195', 5), -- El Principito -> Saint-Exupéry
('978-1501142970', 6), -- It -> King
('978-9992301321', 7), -- Un día en la vida -> Manlio Argueta
('978-9992302106', 8); -- Tierra de Infancia -> Claudia Lars

-- ─── ASOCIACIÓN LIBRO Y CATEGORIA (libro_categoria) ──────────────────────────
INSERT OR IGNORE INTO libro_categoria (ISBN, id_categoria) VALUES
('978-0307474728', 1), -- Cien años de soledad -> Realismo Mágico
('978-8467033472', 2), -- Don Quijote -> Clásicos
('978-0451524935', 3), -- 1984 -> Distopía
('978-0747532699', 4), -- Harry Potter -> Fantasía
('978-0156012195', 5), -- El Principito -> Infantil
('978-1501142970', 6), -- It -> Terror
('978-9992301321', 7), -- Un día en la vida -> Lit. Salvadoreña
('978-9992302106', 7), -- Tierra de Infancia -> Lit. Salvadoreña
('978-9992302106', 8); -- Tierra de Infancia -> Poesía (Tiene doble categoría)

-- ─── EJEMPLARES FÍSICOS DE LOS LIBROS ────────────────────────────────────────
-- Metamos un par de copias físicas de algunos libros para simular stock
INSERT OR IGNORE INTO ejemplares (id_ejemplar, ISBN, estado) VALUES
(1, '978-0307474728', 'Disponible'), -- Cien años de soledad (Copia 1)
(2, '978-0307474728', 'Disponible'), -- Cien años de soledad (Copia 2)
(3, '978-8467033472', 'Disponible'), -- Don Quijote
(4, '978-0451524935', 'Prestado'),    -- 1984 (Para probar vistas de libros prestados)
(5, '978-0747532699', 'Disponible'), -- Harry Potter
(6, '978-0156012195', 'Disponible'), -- El Principito
(7, '978-1501142970', 'Dañado'),     -- It (Buenisimo para pruebas de mantenimiento)
(8, '978-9992301321', 'Disponible'), -- Un día en la vida
(9, '978-9992302106', 'Disponible'); -- Tierra de Infancia