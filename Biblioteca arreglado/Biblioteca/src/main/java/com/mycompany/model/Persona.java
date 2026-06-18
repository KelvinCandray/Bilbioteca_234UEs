/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.model;

/**
 *
 * @author KelvinCandray
 */

public class Persona {
    private int idPersona;
    private String primerNombre;
    private String apellido;
    private String correo;
    private String telefono;
    private String fechaNacimiento;
    private String pasaje;
    private String numeroCasa;
    private String colonia;
    private String municipio;
    private String departamento;

    public Persona() {}

    public Persona(int idPersona, String primerNombre, String apellido, String correo,
                   String telefono, String fechaNacimiento, String pasaje, String numeroCasa,
                   String colonia, String municipio, String departamento) {
        this.idPersona      = idPersona;
        this.primerNombre   = primerNombre;
        this.apellido       = apellido;
        this.correo         = correo;
        this.telefono       = telefono;
        this.fechaNacimiento = fechaNacimiento;
        this.pasaje         = pasaje;
        this.numeroCasa     = numeroCasa;
        this.colonia        = colonia;
        this.municipio      = municipio;
        this.departamento   = departamento;
    }

    public int getIdPersona() { return idPersona; }
    public void setIdPersona(int idPersona) { this.idPersona = idPersona; }

    public String getPrimerNombre() { return primerNombre; }
    public void setPrimerNombre(String primerNombre) { this.primerNombre = primerNombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(String fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getPasaje() { return pasaje; }
    public void setPasaje(String pasaje) { this.pasaje = pasaje; }

    public String getNumeroCasa() { return numeroCasa; }
    public void setNumeroCasa(String numeroCasa) { this.numeroCasa = numeroCasa; }

    public String getColonia() { return colonia; }
    public void setColonia(String colonia) { this.colonia = colonia; }

    public String getMunicipio() { return municipio; }
    public void setMunicipio(String municipio) { this.municipio = municipio; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    @Override
    public String toString() {
        return primerNombre + " " + apellido;
    }
}