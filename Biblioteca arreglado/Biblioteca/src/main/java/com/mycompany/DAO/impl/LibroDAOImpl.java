/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.DAO.impl;

/**
 *
 * @author KelvinCandray
 */

import com.mycompany.DAO.ConexionBD;
import com.mycompany.DAO.LibroDAO;
import com.mycompany.model.Autor;
import com.mycompany.model.Categoria;
import com.mycompany.model.Ejemplar;
import com.mycompany.model.Libro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDAOImpl implements LibroDAO {

    private Connection con() throws SQLException { return ConexionBD.getConexion(); }

    // ─── REGISTRO ─────────────────────────────────────────────────────────────

    @Override
    public void registrarLibro(Libro libro, int cantidadEjemplares) throws Exception {
        ConexionBD.ejecutarEnTransaccion(() -> {
            // Si el libro no existe, lo inserta
            String check = "SELECT COUNT(*) FROM libros WHERE ISBN = ?";
            try (PreparedStatement ps = con().prepareStatement(check)) {
                ps.setString(1, libro.getIsbn());
                ResultSet rs = ps.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    String sql = "INSERT INTO libros (ISBN, titulo, editorial, anio, tipo_edicion) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement ins = con().prepareStatement(sql)) {
                        ins.setString(1, libro.getIsbn());
                        ins.setString(2, libro.getTitulo());
                        ins.setString(3, libro.getEditorial());
                        ins.setInt(4, libro.getAnio());
                        ins.setString(5, libro.getTipoEdicion());
                        ins.executeUpdate();
                    }
                }
            }
            insertarEjemplares(libro.getIsbn(), cantidadEjemplares);
        });
    }

    @Override
    public void agregarEjemplares(String isbn, int cantidad) throws Exception {
        String check = "SELECT COUNT(*) FROM libros WHERE ISBN = ?";
        try (PreparedStatement ps = con().prepareStatement(check)) {
            ps.setString(1, isbn);
            ResultSet rs = ps.executeQuery();
            if (!rs.next() || rs.getInt(1) == 0)
                throw new Exception("No existe ningún libro con ese ISBN.");
        }
        if (cantidad <= 0)
            throw new Exception("La cantidad de ejemplares a agregar debe ser mayor a cero.");
        insertarEjemplares(isbn, cantidad);
    }

    /** Inserta N ejemplares nuevos en estado 'Disponible' para un ISBN ya existente. */
    private void insertarEjemplares(String isbn, int cantidad) throws Exception {
        String sqlEj = "INSERT INTO ejemplares (ISBN, estado) VALUES (?, 'Disponible')";
        try (PreparedStatement ps = con().prepareStatement(sqlEj)) {
            for (int i = 0; i < cantidad; i++) {
                ps.setString(1, isbn);
                ps.executeUpdate();
            }
        }
    }

    @Override
    public void registrarAutor(Autor autor) throws Exception {
        String check = "SELECT COUNT(*) FROM autores WHERE nombre = ? AND apellido = ?";
        try (PreparedStatement ps = con().prepareStatement(check)) {
            ps.setString(1, autor.getNombre());
            ps.setString(2, autor.getApellido());
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0)
                throw new Exception("El autor ya está registrado.");
        }
        String sql = "INSERT INTO autores (nombre, apellido, nacionalidad) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, autor.getNombre());
            ps.setString(2, autor.getApellido());
            ps.setString(3, autor.getNacionalidad());
            ps.executeUpdate();
        }
    }

    @Override
    public void registrarCategoria(Categoria categoria) throws Exception {
        String sql = "INSERT INTO categorias (nombre_categoria, edad_minima) VALUES (?, ?)";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, categoria.getNombreCategoria());
            ps.setInt(2, categoria.getEdadMinima());
            ps.executeUpdate();
        }
    }

    @Override
    public void relacionarLibroAutor(String isbn, int idAutor) throws Exception {
        String check = "SELECT COUNT(*) FROM libro_autor WHERE ISBN = ? AND id_autor = ?";
        try (PreparedStatement ps = con().prepareStatement(check)) {
            ps.setString(1, isbn); ps.setInt(2, idAutor);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0)
                throw new Exception("Esta relación libro-autor ya existe.");
        }
        String sql = "INSERT INTO libro_autor (ISBN, id_autor) VALUES (?, ?)";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, isbn); ps.setInt(2, idAutor);
            ps.executeUpdate();
        }
    }

    @Override
    public void relacionarLibroCategoria(String isbn, int idCategoria) throws Exception {
        String check = "SELECT COUNT(*) FROM libro_categoria WHERE ISBN = ? AND id_categoria = ?";
        try (PreparedStatement ps = con().prepareStatement(check)) {
            ps.setString(1, isbn); ps.setInt(2, idCategoria);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0)
                throw new Exception("Esta relación libro-categoría ya existe.");
        }
        String sql = "INSERT INTO libro_categoria (ISBN, id_categoria) VALUES (?, ?)";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, isbn); ps.setInt(2, idCategoria);
            ps.executeUpdate();
        }
    }

    // ─── EDICIÓN / ELIMINACIÓN ──────────────────────────────────────────────

    @Override
    public void actualizarAutor(Autor autor) throws Exception {
        String check = "SELECT COUNT(*) FROM autores WHERE nombre = ? AND apellido = ? AND id_autor != ?";
        try (PreparedStatement ps = con().prepareStatement(check)) {
            ps.setString(1, autor.getNombre());
            ps.setString(2, autor.getApellido());
            ps.setInt(3, autor.getIdAutor());
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0)
                throw new Exception("Ya existe otro autor registrado con ese nombre y apellido.");
        }
        String sql = "UPDATE autores SET nombre = ?, apellido = ?, nacionalidad = ? WHERE id_autor = ?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, autor.getNombre());
            ps.setString(2, autor.getApellido());
            ps.setString(3, autor.getNacionalidad());
            ps.setInt(4, autor.getIdAutor());
            int filas = ps.executeUpdate();
            if (filas == 0) throw new Exception("El autor que intenta editar ya no existe.");
        }
    }

    @Override
    public void eliminarAutor(int idAutor) throws Exception {
        // libro_autor tiene ON DELETE CASCADE: al borrar el autor se limpian sus relaciones,
        // pero los libros en sí no se eliminan (siguen existiendo con sus otros autores, si tienen).
        String sql = "DELETE FROM autores WHERE id_autor = ?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, idAutor);
            int filas = ps.executeUpdate();
            if (filas == 0) throw new Exception("El autor que intenta eliminar ya no existe.");
        }
    }

    @Override
    public void actualizarCategoria(Categoria categoria) throws Exception {
        String check = "SELECT COUNT(*) FROM categorias WHERE nombre_categoria = ? AND id_categoria != ?";
        try (PreparedStatement ps = con().prepareStatement(check)) {
            ps.setString(1, categoria.getNombreCategoria());
            ps.setInt(2, categoria.getIdCategoria());
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0)
                throw new Exception("Ya existe otra categoría con ese nombre.");
        }
        if (categoria.getEdadMinima() < 0)
            throw new Exception("La edad mínima no puede ser negativa.");

        String sql = "UPDATE categorias SET nombre_categoria = ?, edad_minima = ? WHERE id_categoria = ?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, categoria.getNombreCategoria());
            ps.setInt(2, categoria.getEdadMinima());
            ps.setInt(3, categoria.getIdCategoria());
            int filas = ps.executeUpdate();
            if (filas == 0) throw new Exception("La categoría que intenta editar ya no existe.");
        }
    }

    @Override
    public void eliminarCategoria(int idCategoria) throws Exception {
        // libro_categoria tiene ON DELETE CASCADE: se limpian las relaciones, los libros persisten.
        String sql = "DELETE FROM categorias WHERE id_categoria = ?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, idCategoria);
            int filas = ps.executeUpdate();
            if (filas == 0) throw new Exception("La categoría que intenta eliminar ya no existe.");
        }
    }

    @Override
    public void actualizarLibro(Libro libro) throws Exception {
        String sql = "UPDATE libros SET titulo = ?, editorial = ?, anio = ?, tipo_edicion = ? WHERE ISBN = ?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getEditorial());
            ps.setInt(3, libro.getAnio());
            ps.setString(4, libro.getTipoEdicion());
            ps.setString(5, libro.getIsbn());
            int filas = ps.executeUpdate();
            if (filas == 0) throw new Exception("El libro que intenta editar ya no existe.");
        }
    }

    @Override
    public void eliminarLibro(String isbn) throws Exception {
        // 1) No se puede eliminar si hay préstamos activos o solicitudes pendientes sobre sus ejemplares
        String sqlActivos = """
            SELECT COUNT(*) FROM prestamos p
            INNER JOIN ejemplares e ON p.id_ejemplar = e.id_ejemplar
            WHERE e.ISBN = ? AND p.estado IN ('Solicitado', 'Prestado', 'Retrasado')
            """;
        try (PreparedStatement ps = con().prepareStatement(sqlActivos)) {
            ps.setString(1, isbn);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0)
                throw new Exception("No se puede eliminar: hay préstamos activos o solicitudes pendientes sobre ejemplares de este libro.");
        }

        // 2) Si algún ejemplar tiene historial de préstamos (aunque ya estén devueltos), la base de datos
        //    impedirá borrarlo por la restricción de llave foránea. Lo detectamos antes para dar un mensaje claro
        //    en vez de dejar a medias la eliminación de ejemplares.
        String sqlHistorial = """
            SELECT COUNT(*) FROM prestamos p
            INNER JOIN ejemplares e ON p.id_ejemplar = e.id_ejemplar
            WHERE e.ISBN = ?
            """;
        try (PreparedStatement ps = con().prepareStatement(sqlHistorial)) {
            ps.setString(1, isbn);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0)
                throw new Exception("No se puede eliminar este libro: sus ejemplares tienen historial de préstamos. "
                        + "El sistema conserva ese historial; si lo necesita, marque los ejemplares como 'Perdido' en vez de eliminarlos.");
        }

        // 3) Camino libre: borrar ejemplares y luego el libro (libro_autor/libro_categoria se limpian por CASCADE)
        ConexionBD.ejecutarEnTransaccion(() -> {
            String sqlEjemplares = "DELETE FROM ejemplares WHERE ISBN = ?";
            try (PreparedStatement ps = con().prepareStatement(sqlEjemplares)) {
                ps.setString(1, isbn);
                ps.executeUpdate();
            }
            String sqlLibro = "DELETE FROM libros WHERE ISBN = ?";
            try (PreparedStatement ps = con().prepareStatement(sqlLibro)) {
                ps.setString(1, isbn);
                int filas = ps.executeUpdate();
                if (filas == 0) throw new Exception("El libro que intenta eliminar ya no existe.");
            }
        });
    }

    @Override
    public void actualizarEjemplar(Ejemplar ejemplar) throws Exception {
        String estadoActual;
        String sqlGet = "SELECT estado FROM ejemplares WHERE id_ejemplar = ?";
        try (PreparedStatement ps = con().prepareStatement(sqlGet)) {
            ps.setInt(1, ejemplar.getIdEjemplar());
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) throw new Exception("El ejemplar que intenta editar ya no existe.");
            estadoActual = rs.getString("estado");
        }

        // Evita romper la sincronía con la tabla de préstamos: un ejemplar "Prestado" solo debe
        // liberarse a través del flujo de devolución (PrestamoDAO.registrarDevolucion), y "Prestado"
        // nunca debe asignarse a mano (quedaría sin un préstamo real que lo respalde).
        if ("Prestado".equals(estadoActual))
            throw new Exception("Este ejemplar está actualmente prestado. Registre la devolución desde el módulo de Préstamos antes de cambiar su estado.");
        if ("Prestado".equals(ejemplar.getEstado()))
            throw new Exception("El estado 'Prestado' se asigna automáticamente al aprobar un préstamo; no puede asignarse manualmente.");

        String sql = "UPDATE ejemplares SET estado = ? WHERE id_ejemplar = ?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, ejemplar.getEstado());
            ps.setInt(2, ejemplar.getIdEjemplar());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminarEjemplar(int idEjemplar) throws Exception {
        String estadoActual;
        String sqlGet = "SELECT estado FROM ejemplares WHERE id_ejemplar = ?";
        try (PreparedStatement ps = con().prepareStatement(sqlGet)) {
            ps.setInt(1, idEjemplar);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) throw new Exception("El ejemplar que intenta eliminar ya no existe.");
            estadoActual = rs.getString("estado");
        }
        if ("Prestado".equals(estadoActual))
            throw new Exception("No se puede eliminar: este ejemplar está actualmente prestado.");

        String sqlHistorial = "SELECT COUNT(*) FROM prestamos WHERE id_ejemplar = ?";
        try (PreparedStatement ps = con().prepareStatement(sqlHistorial)) {
            ps.setInt(1, idEjemplar);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0)
                throw new Exception("No se puede eliminar: este ejemplar tiene historial de préstamos. Márquelo como 'Perdido' en vez de eliminarlo.");
        }

        String sql = "DELETE FROM ejemplares WHERE id_ejemplar = ?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, idEjemplar);
            ps.executeUpdate();
        }
    }

    // ─── RELACIÓN LIBRO <-> AUTORES / CATEGORÍAS ───────────────────────────────

    @Override
    public List<Autor> obtenerAutoresDeLibro(String isbn) throws Exception {
        List<Autor> lista = new ArrayList<>();
        String sql = """
            SELECT a.id_autor, a.nombre, a.apellido, a.nacionalidad
            FROM autores a INNER JOIN libro_autor la ON a.id_autor = la.id_autor
            WHERE la.ISBN = ? ORDER BY a.apellido
            """;
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, isbn);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Autor(rs.getInt("id_autor"), rs.getString("nombre"), rs.getString("apellido"), rs.getString("nacionalidad")));
            }
        }
        return lista;
    }

    @Override
    public List<Categoria> obtenerCategoriasDeLibro(String isbn) throws Exception {
        List<Categoria> lista = new ArrayList<>();
        String sql = """
            SELECT c.id_categoria, c.nombre_categoria, c.edad_minima
            FROM categorias c INNER JOIN libro_categoria lc ON c.id_categoria = lc.id_categoria
            WHERE lc.ISBN = ? ORDER BY c.nombre_categoria
            """;
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, isbn);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Categoria(rs.getInt("id_categoria"), rs.getString("nombre_categoria"), rs.getInt("edad_minima")));
            }
        }
        return lista;
    }

    @Override
    public void actualizarAutoresDeLibro(String isbn, List<Integer> idsAutores) throws Exception {
        ConexionBD.ejecutarEnTransaccion(() -> {
            String borrar = "DELETE FROM libro_autor WHERE ISBN = ?";
            try (PreparedStatement ps = con().prepareStatement(borrar)) {
                ps.setString(1, isbn);
                ps.executeUpdate();
            }
            String insertar = "INSERT INTO libro_autor (ISBN, id_autor) VALUES (?, ?)";
            try (PreparedStatement ps = con().prepareStatement(insertar)) {
                for (int idAutor : idsAutores) {
                    ps.setString(1, isbn);
                    ps.setInt(2, idAutor);
                    ps.executeUpdate();
                }
            }
        });
    }

    @Override
    public void actualizarCategoriasDeLibro(String isbn, List<Integer> idsCategorias) throws Exception {
        ConexionBD.ejecutarEnTransaccion(() -> {
            String borrar = "DELETE FROM libro_categoria WHERE ISBN = ?";
            try (PreparedStatement ps = con().prepareStatement(borrar)) {
                ps.setString(1, isbn);
                ps.executeUpdate();
            }
            String insertar = "INSERT INTO libro_categoria (ISBN, id_categoria) VALUES (?, ?)";
            try (PreparedStatement ps = con().prepareStatement(insertar)) {
                for (int idCategoria : idsCategorias) {
                    ps.setString(1, isbn);
                    ps.setInt(2, idCategoria);
                    ps.executeUpdate();
                }
            }
        });
    }

    // ─── CONSULTAS ────────────────────────────────────────────────────────────

    @Override
    public List<Libro> obtenerLibros() throws Exception {
        List<Libro> lista = new ArrayList<>();
        String sql = """
            SELECT l.ISBN, l.titulo, l.editorial, l.anio, l.tipo_edicion,
                GROUP_CONCAT(DISTINCT a.nombre || ' ' || a.apellido) AS autores,
                GROUP_CONCAT(DISTINCT c.nombre_categoria) AS categorias,
                SUM(CASE WHEN e.estado = 'Disponible' THEN 1 ELSE 0 END) AS disponibles,
                SUM(CASE WHEN e.estado = 'Prestado'   THEN 1 ELSE 0 END) AS prestados,
                SUM(CASE WHEN e.estado = 'Dañado'     THEN 1 ELSE 0 END) AS daniados,
                SUM(CASE WHEN e.estado = 'Perdido'    THEN 1 ELSE 0 END) AS perdidos
            FROM libros l
            LEFT JOIN libro_autor la ON l.ISBN = la.ISBN
            LEFT JOIN autores a ON la.id_autor = a.id_autor
            LEFT JOIN libro_categoria lc ON l.ISBN = lc.ISBN
            LEFT JOIN categorias c ON lc.id_categoria = c.id_categoria
            LEFT JOIN ejemplares e ON l.ISBN = e.ISBN
            GROUP BY l.ISBN
            ORDER BY l.titulo
            """;
        try (Statement st = con().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Libro l = new Libro();
                l.setIsbn(rs.getString("ISBN"));
                l.setTitulo(rs.getString("titulo"));
                l.setEditorial(rs.getString("editorial"));
                l.setAnio(rs.getInt("anio"));
                l.setTipoEdicion(rs.getString("tipo_edicion"));
                l.setAutores(rs.getString("autores"));
                l.setCategorias(rs.getString("categorias"));
                l.setDisponibles(rs.getInt("disponibles"));
                l.setPrestados(rs.getInt("prestados"));
                l.setDaniados(rs.getInt("daniados"));
                l.setPerdidos(rs.getInt("perdidos"));
                lista.add(l);
            }
        }
        return lista;
    }

    @Override
    public List<Autor> obtenerAutores() throws Exception {
        List<Autor> lista = new ArrayList<>();
        String sql = "SELECT id_autor, nombre, apellido, nacionalidad FROM autores ORDER BY apellido";
        try (Statement st = con().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Autor(
                    rs.getInt("id_autor"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("nacionalidad")
                ));
            }
        }
        return lista;
    }

    @Override
    public List<Categoria> obtenerCategorias() throws Exception {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT id_categoria, nombre_categoria, edad_minima FROM categorias ORDER BY nombre_categoria";
        try (Statement st = con().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Categoria(
                    rs.getInt("id_categoria"),
                    rs.getString("nombre_categoria"),
                    rs.getInt("edad_minima")
                ));
            }
        }
        return lista;
    }

    @Override
    public List<Ejemplar> obtenerEjemplares(String isbn) throws Exception {
        List<Ejemplar> lista = new ArrayList<>();
        String sql = "SELECT id_ejemplar, ISBN, estado FROM ejemplares WHERE ISBN = ? ORDER BY id_ejemplar";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, isbn);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Ejemplar(
                    rs.getInt("id_ejemplar"),
                    rs.getString("ISBN"),
                    rs.getString("estado")
                ));
            }
        }
        return lista;
    }

    @Override
    public List<Ejemplar> obtenerTodosLosEjemplares() throws Exception {
        List<Ejemplar> lista = new ArrayList<>();
        String sql = """
            SELECT e.id_ejemplar, e.ISBN, e.estado, l.titulo
            FROM ejemplares e INNER JOIN libros l ON e.ISBN = l.ISBN
            ORDER BY l.titulo, e.id_ejemplar
            """;
        try (Statement st = con().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Ejemplar ej = new Ejemplar(rs.getInt("id_ejemplar"), rs.getString("ISBN"), rs.getString("estado"));
                ej.setTituloLibro(rs.getString("titulo"));
                lista.add(ej);
            }
        }
        return lista;
    }

    @Override
    public Ejemplar buscarEjemplarDisponible(String isbn) throws Exception {
        String sql = "SELECT id_ejemplar, ISBN, estado FROM ejemplares WHERE ISBN = ? AND estado = 'Disponible' LIMIT 1";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, isbn);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Ejemplar(rs.getInt("id_ejemplar"), rs.getString("ISBN"), rs.getString("estado"));
            }
        }
        return null;
    }

    @Override
    public int obtenerEdadMinima(int idEjemplar) throws Exception {
        String sql = """
            SELECT MAX(c.edad_minima) AS edad_minima
            FROM ejemplares e
            INNER JOIN libro_categoria lc ON e.ISBN = lc.ISBN
            INNER JOIN categorias c ON lc.id_categoria = c.id_categoria
            WHERE e.id_ejemplar = ?
            """;
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, idEjemplar);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("edad_minima");
        }
        return 0;
    }
}