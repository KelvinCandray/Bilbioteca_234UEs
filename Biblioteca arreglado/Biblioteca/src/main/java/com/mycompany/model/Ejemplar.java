/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.model;

/**
 *
 * @author KelvinCandray
 */
public class Ejemplar {
    private int idEjemplar;
    private String isbn;
    private String estado; // 'Disponible', 'Prestado', 'Dañado', 'Perdido'

    // Campo extra para mostrar en listados (viene de un JOIN con libros)
    private String tituloLibro;

    public Ejemplar() {}

    public Ejemplar(int idEjemplar, String isbn, String estado) {
        this.idEjemplar = idEjemplar;
        this.isbn       = isbn;
        this.estado     = estado;
    }

    public int getIdEjemplar() { return idEjemplar; }
    public void setIdEjemplar(int idEjemplar) { this.idEjemplar = idEjemplar; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getTituloLibro() { return tituloLibro; }
    public void setTituloLibro(String tituloLibro) { this.tituloLibro = tituloLibro; }
}