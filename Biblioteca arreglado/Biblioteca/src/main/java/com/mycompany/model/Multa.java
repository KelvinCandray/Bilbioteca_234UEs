/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.model;

/**
 *
 * @author KelvinCandray
 */
public class Multa {
    private int idMulta;
    private int idPrestamo;
    private String tipoMulta;   // 'Retraso', 'Daño'
    private String fechaGeneracion;
    private String fechaMaximaPagar;
    private String fechaRealPago;
    private double monto;
    private String estado;      // 'Pendiente', 'Retrasada', 'Pagada'
    private String pagoATiempo; // 'Si', 'No', 'Pendiente'

    // Campo extra para mostrar en tabla
    private int idUsuario;

    public Multa() {}

    public int getIdMulta() { return idMulta; }
    public void setIdMulta(int idMulta) { this.idMulta = idMulta; }

    public int getIdPrestamo() { return idPrestamo; }
    public void setIdPrestamo(int idPrestamo) { this.idPrestamo = idPrestamo; }

    public String getTipoMulta() { return tipoMulta; }
    public void setTipoMulta(String tipoMulta) { this.tipoMulta = tipoMulta; }

    public String getFechaGeneracion() { return fechaGeneracion; }
    public void setFechaGeneracion(String fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }

    public String getFechaMaximaPagar() { return fechaMaximaPagar; }
    public void setFechaMaximaPagar(String fechaMaximaPagar) { this.fechaMaximaPagar = fechaMaximaPagar; }

    public String getFechaRealPago() { return fechaRealPago; }
    public void setFechaRealPago(String fechaRealPago) { this.fechaRealPago = fechaRealPago; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getPagoATiempo() { return pagoATiempo; }
    public void setPagoATiempo(String pagoATiempo) { this.pagoATiempo = pagoATiempo; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
}