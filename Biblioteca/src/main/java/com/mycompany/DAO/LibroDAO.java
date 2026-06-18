package com.mycompany.DAO;
import com.mycompany.model.Autor;
import com.mycompany.model.Categoria;
import com.mycompany.model.Ejemplar;
import com.mycompany.model.Libro;
import java.util.List;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author KelvinCandray
 */
public interface LibroDAO {

    // Registro
    void registrarLibro(Libro libro, int cantidadEjemplares) throws Exception;
    void registrarAutor(Autor autor) throws Exception;
    void registrarCategoria(Categoria categoria) throws Exception;
    void relacionarLibroAutor(String isbn, int idAutor) throws Exception;
    void relacionarLibroCategoria(String isbn, int idCategoria) throws Exception;

    // Consultas catálogo
    List<Libro>     obtenerLibros() throws Exception;
    List<Autor>     obtenerAutores() throws Exception;
    List<Categoria> obtenerCategorias() throws Exception;
    List<Ejemplar>  obtenerEjemplares(String isbn) throws Exception;

    // Búsqueda
    Ejemplar buscarEjemplarDisponible(String isbn) throws Exception;
    int      obtenerEdadMinima(int idEjemplar) throws Exception;
}