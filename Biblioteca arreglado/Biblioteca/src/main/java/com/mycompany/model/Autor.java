/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.model;

/**
 *
 * @author KelvinCandray
 */
public class Autor {
    private int idAutor;
    private String nombre;
    private String apellido;
    private String nacionalidad;

    public Autor() {}

    public Autor(int idAutor, String nombre, String apellido, String nacionalidad) {
        this.idAutor      = idAutor;
        this.nombre       = nombre;
        this.apellido     = apellido;
        this.nacionalidad = nacionalidad;
    }

    public int getIdAutor() { return idAutor; }
    public void setIdAutor(int idAutor) { this.idAutor = idAutor; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getNacionalidad() { return nacionalidad; }
    public void setNacionalidad(String nacionalidad) { this.nacionalidad = nacionalidad; }

    @Override
    public String toString() { return nombre + " " + apellido; }
}