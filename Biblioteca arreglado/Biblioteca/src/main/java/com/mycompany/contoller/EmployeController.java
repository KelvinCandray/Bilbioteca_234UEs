package com.mycompany.contoller;

import com.mycompany.model.Empleado;
import com.mycompany.view.DataViewer;
import com.mycompany.view.MainInterface;
import javax.swing.JPanel;

/**
 * Controlador para el rol de Empleado (bibliotecario o vigilante).
 *
 * @author betuel
 */
public class EmployeController {

    private final Empleado empleado;
    private MainInterface view;

    public EmployeController(Empleado empleado) {
        this.empleado = empleado;
        view = new MainInterface(empleado.toString(), 1); // userType 1 = Empleado
        view.setVisible(true);

        buttonActions();
    }

    private void buttonActions() {
        view.logOut.addActionListener(e -> {
            view.dispose();
            new LogInController();
        });

        view.jPanelLeft.boton1.addActionListener(e -> {
            DataViewer panelSolicitudes = new DataViewer(null, "Prestamos sin aprobar", "ID_Cliente",
                    "Ejemplar", "", "Aprobar", "Rechazar");
            cambiarPanelPrincipal(panelSolicitudes);
        });

        view.jPanelLeft.boton2.addActionListener(e -> {
            DataViewer panelMultas = new DataViewer(null, "Multas activas", "Id_cliente",
                    "Multa", "Ver detalles", "", "");
            cambiarPanelPrincipal(panelMultas);
        });

        view.jPanelLeft.boton3.addActionListener(e -> {
            DataViewer panelPrestamosActivos = new DataViewer(null, "Prestamos activos", "ID",
                    "Nombre_cliente", "Ver detalles", "", "");
            cambiarPanelPrincipal(panelPrestamosActivos);
        });

        view.jPanelLeft.boton4.addActionListener(e -> {
            DataViewer panelClientes = new DataViewer(null, "Clientes", "Nombre",
                    "ID", "Ver detalles", "", "");
            cambiarPanelPrincipal(panelClientes);
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
