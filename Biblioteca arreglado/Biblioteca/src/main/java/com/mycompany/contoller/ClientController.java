package com.mycompany.contoller;

import com.mycompany.model.Usuario;
import com.mycompany.view.DataViewer;
import com.mycompany.view.MainInterface;
import javax.swing.JPanel;

/**
 * Controlador para el rol de Usuario (cliente/lector).
 *
 * @author betuel
 */
public class ClientController {

    private final Usuario usuario;
    private MainInterface view;

    public ClientController(Usuario usuario) {
        this.usuario = usuario;
        view = new MainInterface(usuario.toString(), 0);
        view.setVisible(true);

        buttonActions();
    }

    private void buttonActions() {
        view.logOut.addActionListener(e -> {
            view.dispose();
            new LogInController();
        });

        view.jPanelLeft.boton1.addActionListener(e -> {
            DataViewer panelCatalogo = new DataViewer(
                null, "Catalogo de libros", "Nombre", "ISBN", 
                "Enviar solicitud", "Añadir al prestamo", "Cancelar selección"
            );
            cambiarPanelPrincipal(panelCatalogo);
        });

        view.jPanelLeft.boton2.addActionListener(e -> {
            DataViewer panelPrestamos = new DataViewer(
                null, "Mis prestamos", "", "", 
                "", "Ver detalles", ""
            );
            cambiarPanelPrincipal(panelPrestamos);
        });

        view.jPanelLeft.boton3.addActionListener(e -> {
            DataViewer panelMultas = new DataViewer(
                null, "Multas Pendientes", "", "", 
                "", "Ver detalles", ""
            );
            cambiarPanelPrincipal(panelMultas);
        });
    }

    private void cambiarPanelPrincipal(JPanel nuevoPanel) {
        view.remove(view.jPanelPrincipal); 
        view.jPanelPrincipal = nuevoPanel;  
        view.add(view.jPanelPrincipal, java.awt.BorderLayout.CENTER); 

        view.revalidate();
        view.repaint();
    }
}