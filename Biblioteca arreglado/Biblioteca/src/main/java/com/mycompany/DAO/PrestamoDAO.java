/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.DAO;
import com.mycompany.model.Multa;
import com.mycompany.model.Prestamo;
import java.util.List;
/**
 *
 * @author KelvinCandray
 */
public interface PrestamoDAO {

    // Operaciones principales
    void solicitarPrestamo(int idUsuario, int idEjemplar) throws Exception;
    void aprobarPrestamo(int idPrestamo, int idEmpleado) throws Exception;
    void rechazarPrestamo(int idPrestamo) throws Exception;
    void registrarDevolucion(int idPrestamo, String estadoEjemplar) throws Exception;
    void registrarPago(int idMulta, double monto) throws Exception;
    void modificarMulta(int idMulta, double nuevoMonto, String nuevaFechaMaximaPagar) throws Exception;
    void exonerarMulta(int idMulta) throws Exception;

    // Auditoría automática (reemplaza los cursores de MySQL)
    void revisarPrestamosVencidos() throws Exception;
    void revisarMultasVencidas() throws Exception;

    // Consultas
    List<Prestamo> obtenerSolicitudesPendientes() throws Exception;
    List<Prestamo> obtenerPrestamosActivos() throws Exception;
    List<Prestamo> obtenerPrestamosDeUsuario(int idUsuario) throws Exception;
    List<Prestamo> obtenerHistorialCompleto() throws Exception;
    List<Multa>    obtenerMultasDeUsuario(int idUsuario) throws Exception;
    List<Multa>    obtenerTodasLasMultas() throws Exception;

    // Validaciones
    int     contarPrestamosActivos(int idUsuario) throws Exception;
    boolean tieneRetrasados(int idUsuario) throws Exception;
    boolean tieneMultasPendientes(int idUsuario) throws Exception;
} 