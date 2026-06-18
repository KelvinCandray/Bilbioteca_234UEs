/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.model;

/**
 *
 * @author KelvinCandray
 */
public class Prestamo {
    private int idPrestamo;
    private int idUsuario;
    private int idEmpleado;
    private int idEjemplar;
    private String isbn;
    private String fechaSolicitud;
    private String fechaRetiro;
    private String fechaIdealRegreso;
    private String fechaRealRegreso;
    private String estado; // 'Solicitado', 'Prestado', 'Retrasado', 'Devuelto'

    // Campos extra para mostrar en tablas
    private String nombreUsuario;
    private String tituloLibro;

    public Prestamo() {}

    public int getIdPrestamo() { return idPrestamo; }
    public void setIdPrestamo(int idPrestamo) { this.idPrestamo = idPrestamo; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public int getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(int idEmpleado) { this.idEmpleado = idEmpleado; }

    public int getIdEjemplar() { return idEjemplar; }
    public void setIdEjemplar(int idEjemplar) { this.idEjemplar = idEjemplar; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(String fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }

    public String getFechaRetiro() { return fechaRetiro; }
    public void setFechaRetiro(String fechaRetiro) { this.fechaRetiro = fechaRetiro; }

    public String getFechaIdealRegreso() { return fechaIdealRegreso; }
    public void setFechaIdealRegreso(String fechaIdealRegreso) { this.fechaIdealRegreso = fechaIdealRegreso; }

    public String getFechaRealRegreso() { return fechaRealRegreso; }
    public void setFechaRealRegreso(String fechaRealRegreso) { this.fechaRealRegreso = fechaRealRegreso; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getTituloLibro() { return tituloLibro; }
    public void setTituloLibro(String tituloLibro) { this.tituloLibro = tituloLibro; }
}