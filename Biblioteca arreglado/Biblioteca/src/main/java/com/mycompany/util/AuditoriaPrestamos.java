package com.mycompany.util;

import com.mycompany.DAO.PrestamoDAO;
import com.mycompany.DAO.impl.PrestamoDAOImpl;

/**
 * Punto único para disparar la auditoría automática de préstamos vencidos y
 * multas vencidas (equivalente a los antiguos procedimientos con cursores de
 * MySQL: {@code revisarPrestamosVencidos} y {@code revisarMultasVencidas}).
 * <p>
 * La aplicación no tiene un proceso en segundo plano corriendo todo el
 * tiempo, así que en su lugar se invoca este método al iniciar la app y al
 * entrar a cada pantalla principal (Admin, Empleado, Cliente). De esa forma,
 * cualquier préstamo u tasa que haya vencido desde la última vez que alguien
 * usó el sistema queda reflejado antes de mostrar cualquier tabla.
 *
 * @author betuel
 */
public final class AuditoriaPrestamos {

    private AuditoriaPrestamos() {}

    public static void ejecutar() {
        try {
            PrestamoDAO prestamoDAO = new PrestamoDAOImpl();
            prestamoDAO.revisarPrestamosVencidos();
            prestamoDAO.revisarMultasVencidas();
        } catch (Exception e) {
            // No bloqueamos la apertura de la pantalla por un fallo de auditoría;
            // simplemente se registra para diagnóstico.
            System.err.println("No se pudo ejecutar la auditoría de préstamos/multas: " + e.getMessage());
        }
    }
}
