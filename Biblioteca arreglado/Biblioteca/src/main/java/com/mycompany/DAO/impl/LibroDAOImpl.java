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
        // Inserta los ejemplares
        String sqlEj = "INSERT INTO ejemplares (ISBN, estado) VALUES (?, 'Disponible')";
        try (PreparedStatement ps = con().prepareStatement(sqlEj)) {
            for (int i = 0; i < cantidadEjemplares; i++) {
                ps.setString(1, libro.getIsbn());
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
