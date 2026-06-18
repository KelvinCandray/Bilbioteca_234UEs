/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.model;

/**
 *
 * @author KelvinCandray
 */
public class Categoria {
    private int idCategoria;
    private String nombreCategoria;
    private int edadMinima;

    public Categoria() {}

    public Categoria(int idCategoria, String nombreCategoria, int edadMinima) {
        this.idCategoria     = idCategoria;
        this.nombreCategoria = nombreCategoria;
        this.edadMinima      = edadMinima;
    }

    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }

    public String getNombreCategoria() { return nombreCategoria; }
    public void setNombreCategoria(String nombreCategoria) { this.nombreCategoria = nombreCategoria; }

    public int getEdadMinima() { return edadMinima; }
    public void setEdadMinima(int edadMinima) { this.edadMinima = edadMinima; }

    @Override
    public String toString() { return nombreCategoria + " (mín. " + edadMinima + " años)"; }
}