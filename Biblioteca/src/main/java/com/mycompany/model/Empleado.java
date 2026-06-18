/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.model;

/**
 *
 * @author KelvinCandray
 */
public class Empleado extends Persona {
    private double salario;
    private String tipoEmpleado; // 'Gerente', 'Bibliotecario', 'Vigilante'
    private String usuario;      // nombre de usuario para login
    private String contrasena;

    public Empleado() {}

    public double getSalario() { return salario; }
    public void setSalario(double salario) { this.salario = salario; }

    public String getTipoEmpleado() { return tipoEmpleado; }
    public void setTipoEmpleado(String tipoEmpleado) { this.tipoEmpleado = tipoEmpleado; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
}
