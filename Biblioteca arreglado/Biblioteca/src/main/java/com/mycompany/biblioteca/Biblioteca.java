package com.mycompany.biblioteca;

import com.mycompany.contoller.LogInController;
import com.mycompany.util.AuditoriaPrestamos;


/**
 *
 * @author betuel
 */
public class Biblioteca {

    public static void main(String[] args) {
        // Refresca préstamos retrasados y multas vencidas antes de mostrar cualquier pantalla
        AuditoriaPrestamos.ejecutar();
        LogInController controlador = new LogInController();
    }
}