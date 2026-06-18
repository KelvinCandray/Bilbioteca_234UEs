/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.model;

/**
 *
 * @author KelvinCandray
 */
public class Libro {
    private String isbn;
    private String titulo;
    private String editorial;
    private int anio;
    private String tipoEdicion;

    // Campos adicionales que vienen de joins con otras tablas
    private String autores;
    private String categorias;
    private int disponibles;
    private int prestados;
    private int daniados;
    private int perdidos;

    public Libro() {}

    public Libro(String isbn, String titulo, String editorial, int anio, String tipoEdicion) {
        this.isbn        = isbn;
        this.titulo      = titulo;
        this.editorial   = editorial;
        this.anio        = anio;
        this.tipoEdicion = tipoEdicion;
    }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getEditorial() { return editorial; }
    public void setEditorial(String editorial) { this.editorial = editorial; }

    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }

    public String getTipoEdicion() { return tipoEdicion; }
    public void setTipoEdicion(String tipoEdicion) { this.tipoEdicion = tipoEdicion; }

    public String getAutores() { return autores; }
    public void setAutores(String autores) { this.autores = autores; }

    public String getCategorias() { return categorias; }
    public void setCategorias(String categorias) { this.categorias = categorias; }

    public int getDisponibles() { return disponibles; }
    public void setDisponibles(int disponibles) { this.disponibles = disponibles; }

    public int getPrestados() { return prestados; }
    public void setPrestados(int prestados) { this.prestados = prestados; }

    public int getDaniados() { return daniados; }
    public void setDaniados(int daniados) { this.daniados = daniados; }

    public int getPerdidos() { return perdidos; }
    public void setPerdidos(int perdidos) { this.perdidos = perdidos; }

    @Override
    public String toString() { return titulo + " (" + isbn + ")"; }
}