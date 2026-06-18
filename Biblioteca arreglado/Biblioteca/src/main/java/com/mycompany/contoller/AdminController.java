package com.mycompany.contoller;

import com.mycompany.model.Empleado;
import com.mycompany.view.DataViewer;
import com.mycompany.view.MainInterface;
import javax.swing.JPanel;

/**
 * Controlador para el rol de Administrador (empleado con tipo 'Gerente').
 *
 * @author betuel
 */
public class AdminController {

    private final Empleado administrador;
    private MainInterface view;

    public AdminController(Empleado administrador) {
        this.administrador = administrador;
        view = new MainInterface(administrador.toString(), 2); // userType 2 = Administrador
        view.setVisible(true);

        buttonActions();
    }

    private void buttonActions() {
        view.logOut.addActionListener(e -> {
            view.dispose();
            new LogInController();
        });

        view.jPanelLeft.boton1.addActionListener(e -> {
            DataViewer panelEjemplares = new DataViewer(null, "Ejemplares", "ID ejemplar", "Nombre_Libro", 
                "Agregar nuevo", "Editar registro", "Eliminar");
            cambiarPanelPrincipal(panelEjemplares);
        });

        view.jPanelLeft.boton2.addActionListener(e -> {
            DataViewer panelLibros = new DataViewer(null, "Catalogo de libros", "Nombre", "ISBN", 
                "Agregar nuevo", "Editar registro", "Eliminar");
            cambiarPanelPrincipal(panelLibros);
        });

        view.jPanelLeft.boton3.addActionListener(e -> {
            DataViewer panelCategorias = new DataViewer(null, "Categorias", "ID", "Nombre", 
                "Agregar nuevo", "Editar registro", "Eliminar");
            cambiarPanelPrincipal(panelCategorias);
        });

        view.jPanelLeft.boton4.addActionListener(e -> {
            DataViewer panelAutores = new DataViewer(null, "Autores", "ID autor", "Nombre del autor", 
                "Agregar nuevo", "Editar registro", "Eliminar");
            cambiarPanelPrincipal(panelAutores);
        });

        view.jPanelLeft.boton5.addActionListener(e -> {
            DataViewer panelPrestamos = new DataViewer(null, "Prestamos activos", "Nombre_cliente", "ID_ prestamo", 
                "Agregar nuevo", "Editar registro", "Eliminar");
            cambiarPanelPrincipal(panelPrestamos);
        });

        view.jPanelLeft.boton6.addActionListener(e -> {
            DataViewer panelMultas = new DataViewer(null, "Multas activas", "Id de cliente", "Multa", 
                "Agregar nuevo", "Editar registro", "Eliminar");
            cambiarPanelPrincipal(panelMultas);
        });

        view.jPanelLeft.boton7.addActionListener(e -> {
            DataViewer panelUsuarios = new DataViewer(null, "Usuarios", "Nombre", "ID", 
                "Agregar nuevo", "Editar registro", "Eliminar");
            cambiarPanelPrincipal(panelUsuarios);
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