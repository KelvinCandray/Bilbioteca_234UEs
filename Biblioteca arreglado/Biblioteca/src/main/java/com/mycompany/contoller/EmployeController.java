package com.mycompany.contoller;

import com.mycompany.DAO.PersonaDAO;
import com.mycompany.DAO.PrestamoDAO;
import com.mycompany.DAO.impl.PersonaDAOImpl;
import com.mycompany.DAO.impl.PrestamoDAOImpl;
import com.mycompany.model.Empleado;
import com.mycompany.model.Multa;
import com.mycompany.model.Persona;
import com.mycompany.model.Prestamo;
import com.mycompany.util.AuditoriaPrestamos;
import com.mycompany.util.Validations;
import com.mycompany.view.DataViewer;
import com.mycompany.view.MainInterface;
import com.mycompany.view.PersonaData;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Controlador para el rol de Empleado (bibliotecario o vigilante).
 * Integra la persistencia de base de datos respetando la interfaz del destino.
 * * @author betuel
 */
public class EmployeController {

    private final Empleado empleado;
    private MainInterface view;
    
    // Inyección de los DAOs para extraer los datos reales
    private final PrestamoDAO prestamoDAO;
    private final PersonaDAO personaDAO;

    public EmployeController(Empleado empleado) {
        this.empleado = empleado;
        
        this.prestamoDAO = new PrestamoDAOImpl();
        this.personaDAO = new PersonaDAOImpl();
        
        view = new MainInterface(empleado.toString(), 1); // userType 1 = Empleado
        view.setVisible(true);

        AuditoriaPrestamos.ejecutar();
        buttonActions();
        
        // Carga la primera vista por defecto usando datos reales
        cargarSolicitudesPendientes();
    }

    private void buttonActions() {
        view.logOut.addActionListener(e -> {
            view.dispose();
            new LogInController();
        });

        // BOTÓN 1: Préstamos sin aprobar
        view.jPanelLeft.boton1.addActionListener(e -> {
            cargarSolicitudesPendientes();
        });

        // BOTÓN 2: Multas activas
        view.jPanelLeft.boton2.addActionListener(e -> {
            cargarGestionMultas();
        });

        // BOTÓN 3: Préstamos activos
        view.jPanelLeft.boton3.addActionListener(e -> {
            cargarPrestamosActivos();
        });

        // BOTÓN 4: Clientes
        view.jPanelLeft.boton4.addActionListener(e -> {
            cargarClientesRegistrados();
        });

        view.jPanelLeft.miPerfil.addActionListener(e -> mostrarMiPerfil());
    }

    // ─── LÓGICA DE EXTRACCIÓN Y ENLACE DE BOTONES ORIGINALES DEL DESTINO ───

    private void cargarSolicitudesPendientes() {
        try {
            List<Prestamo> solicitudes = prestamoDAO.obtenerSolicitudesPendientes();
            List<List<Object>> data = new ArrayList<>();
            
            // Estructura de cabecera adaptada al set de datos
            data.add(List.of("ID Préstamo", "ID Cliente / Socio", "Ejemplar (Libro)", "Fecha Solicitud", "Estado"));
            
            for (Prestamo p : solicitudes) {
                data.add(List.of(
                    p.getIdPrestamo(),
                    p.getNombreUsuario() != null ? p.getNombreUsuario() : "ID: " + p.getIdUsuario(),
                    p.getTituloLibro() != null ? p.getTituloLibro() : "Ejemplar N° " + p.getIdEjemplar(),
                    p.getFechaSolicitud(),
                    p.getEstado()
                ));
            }
            
            // Constructor con los strings EXACTOS del destino
            DataViewer panelSolicitudes = new DataViewer(data, "Prestamos sin aprobar", "ID_Cliente",
                    "Ejemplar", "", "Aprobar", "Rechazar");
            
            // Botón "Aprobar" del destino (segundo botón -> btnUpdateEditar)
            panelSolicitudes.btnUpdate.addActionListener(e -> {
                int fila = panelSolicitudes.table.getSelectedRow();
                if (fila >= 0) {
                    int idPrestamo = Integer.parseInt(panelSolicitudes.table.getValueAt(fila, 0).toString());
                    try {
                        prestamoDAO.aprobarPrestamo(idPrestamo, empleado.getIdPersona());
                        JOptionPane.showMessageDialog(view, "Solicitud aprobada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        cargarSolicitudesPendientes(); 
                    } catch (Exception ex) {
                        showError("Error al aprobar", ex);
                    }
                } else {
                    JOptionPane.showMessageDialog(view, "Seleccione una fila primero.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            });

            // Botón "Rechazar" del destino (tercer botón -> btnDelete)
            panelSolicitudes.btnDelete.addActionListener(e -> {
                int fila = panelSolicitudes.table.getSelectedRow();
                if (fila >= 0) {
                    int idPrestamo = Integer.parseInt(panelSolicitudes.table.getValueAt(fila, 0).toString());
                    try {
                        prestamoDAO.rechazarPrestamo(idPrestamo);
                        JOptionPane.showMessageDialog(view, "Solicitud rechazada.", "Información", JOptionPane.INFORMATION_MESSAGE);
                        cargarSolicitudesPendientes(); 
                    } catch (Exception ex) {
                        showError("Error al rechazar", ex);
                    }
                } else {
                    JOptionPane.showMessageDialog(view, "Seleccione una fila primero.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            });

            cambiarPanelPrincipal(panelSolicitudes);
        } catch (Exception ex) {
            showError("Error al obtener solicitudes", ex);
        }
    }

    private void cargarGestionMultas() {
        try {
            List<Multa> multas = prestamoDAO.obtenerTodasLasMultas();
            List<List<Object>> data = new ArrayList<>();
            
            data.add(List.of("ID Multa", "ID Cliente / Socio", "Multa (Monto $)", "Estado"));
            
            for (Multa m : multas) {
                data.add(List.of(m.getIdMulta(), m.getIdUsuario(), m.getMonto(), m.getEstado()));
            }
            
            // Constructor con los strings EXACTOS del destino
            DataViewer panelMultas = new DataViewer(data, "Multas activas", "Id_cliente",
                    "Multa", "Ver detalles", "", "");
            
            // Botón "Ver detalles" del destino (primer botón -> btnReadCreate)
            panelMultas.btnReadCreate.addActionListener(e -> {
                int fila = panelMultas.table.getSelectedRow();
                if (fila >= 0) {
                    double monto = Double.parseDouble(panelMultas.table.getValueAt(fila, 2).toString());
                    String estado = panelMultas.table.getValueAt(fila, 3).toString();
                    int idMulta = Integer.parseInt(panelMultas.table.getValueAt(fila, 0).toString());
                    
                    if (estado.equalsIgnoreCase("Pagada")) {
                        JOptionPane.showMessageDialog(view, "Detalle: Esta multa ya se encuentra cancelada.", "Información", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        String pagoInput = JOptionPane.showInputDialog(view, "Detalle de deuda encontrada: $" + monto + "\nIngrese el monto para procesar el cobro:", monto);
                        if (pagoInput != null) {
                            try {
                                prestamoDAO.registrarPago(idMulta, Double.parseDouble(pagoInput));
                                JOptionPane.showMessageDialog(view, "Pago de multa registrado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                                cargarGestionMultas();
                            } catch (Exception ex) {
                                showError("Error al cobrar", ex);
                            }
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(view, "Seleccione una multa de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            });

            cambiarPanelPrincipal(panelMultas);
        } catch (Exception ex) {
            showError("Error al obtener multas", ex);
        }
    }

    private void cargarPrestamosActivos() {
        try {
            List<Prestamo> activos = prestamoDAO.obtenerPrestamosActivos();
            List<List<Object>> data = new ArrayList<>();
            
            data.add(List.of("ID", "Nombre_cliente", "Libro / Ejemplar", "Fecha Límite", "Estado"));
            
            for (Prestamo p : activos) {
                data.add(List.of(
                    p.getIdPrestamo(),
                    p.getNombreUsuario() != null ? p.getNombreUsuario() : "ID: " + p.getIdUsuario(),
                    p.getTituloLibro() != null ? p.getTituloLibro() : "Ejemplar ID: " + p.getIdEjemplar(),
                    p.getFechaIdealRegreso(),
                    p.getEstado()
                ));
            }
            
            // Constructor con los strings EXACTOS del destino
            DataViewer panelPrestamosActivos = new DataViewer(data, "Prestamos activos", "ID",
                    "Nombre_cliente", "Ver detalles", "", "");
            
            // Botón "Ver detalles" del destino (primer botón -> btnReadCreate)
            panelPrestamosActivos.btnReadCreate.addActionListener(e -> {
                int fila = panelPrestamosActivos.table.getSelectedRow();
                if (fila >= 0) {
                    int idPrestamo = Integer.parseInt(panelPrestamosActivos.table.getValueAt(fila, 0).toString());
                    String[] opciones = {"Disponible", "Dañado", "Perdido"};
                    
                    String seleccion = (String) JOptionPane.showInputDialog(view, 
                            "Detalles del préstamo. Para registrar la devolución, elija la condición de entrega:", 
                            "Procesar Devolución", JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
                    
                    if (seleccion != null) {
                        try {
                            prestamoDAO.registrarDevolucion(idPrestamo, seleccion);
                            JOptionPane.showMessageDialog(view, "Devolución asentada de forma correcta.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                            cargarPrestamosActivos();
                        } catch (Exception ex) {
                            showError("Error al devolver", ex);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(view, "Seleccione un registro en curso.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            });

            cambiarPanelPrincipal(panelPrestamosActivos);
        } catch (Exception ex) {
            showError("Error al obtener préstamos", ex);
        }
    }

    private void cargarClientesRegistrados() {
        try {
            List<Object[]> personas = personaDAO.obtenerPersonasCompletas();
            List<List<Object>> data = new ArrayList<>();
            
            data.add(List.of("ID (DUI)", "Nombre", "Teléfono", "Correo", "Rol"));
            
            for (Object[] p : personas) {
                // p[8] = "Sí"/"No" si la persona está registrada como usuario (lector).
                // (antes esto comparaba por error el teléfono contra "Usuario"/"Cliente", así que la lista siempre salía vacía)
                if ("Sí".equalsIgnoreCase(p[8].toString())) {
                    data.add(List.of(p[0], p[1] + " " + p[2], p[4], p[3], "Usuario (Lector)"));
                }
            }
            
            // Constructor con los strings EXACTOS del destino
            DataViewer panelClientes = new DataViewer(data, "Clientes", "Nombre",
                    "ID", "Ver detalles", "", "");
            
            // Botón "Ver detalles" del destino (primer botón -> btnReadCreate)
            panelClientes.btnReadCreate.addActionListener(e -> {
                int fila = panelClientes.table.getSelectedRow();
                if (fila >= 0) {
                    String nombre = panelClientes.table.getValueAt(fila, 1).toString();
                    String dui = panelClientes.table.getValueAt(fila, 0).toString();
                    String tel = panelClientes.table.getValueAt(fila, 2).toString();
                    String correo = panelClientes.table.getValueAt(fila, 3).toString();
                    
                    JOptionPane.showMessageDialog(view, 
                            "Ficha de Cliente:\n\nNombre: " + nombre + "\nIdentificación (DUI): " + dui + "\nTeléfono: " + tel + "\nCorreo: " + correo, 
                            "Detalles del Lector", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(view, "Seleccione un cliente para ver su ficha.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            });

            cambiarPanelPrincipal(panelClientes);
        } catch (Exception ex) {
            showError("Error al obtener clientes", ex);
        }
    }

    /** "Mi perfil": el empleado puede editar sus datos de contacto y credenciales, no su salario/cargo. */
    private void mostrarMiPerfil() {
        PersonaData form = new PersonaData();
        form.setPersona(empleado);
        form.setUsuarioLogin(empleado.getUsuario());
        form.setSalario(empleado.getSalario());
        form.setTipoEmpleado(empleado.getTipoEmpleado());
        form.configurarParaMiPerfil(true);
        form.setVolverVisible(true);

        form.btnGuardar.addActionListener(e -> {
            try {
                String errorCorreo = Validations.correoValidation(form.txtCorreo);
                if (!errorCorreo.isEmpty()) {
                    JOptionPane.showMessageDialog(view, errorCorreo, "Correo inválido", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String nuevaContrasena = form.getContrasena();
                if (!nuevaContrasena.isEmpty()) {
                    String errorPass = Validations.passwordValidation(form.txtContrasena);
                    if (!errorPass.isEmpty()) {
                        JOptionPane.showMessageDialog(view, errorPass, "Contraseña inválida", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
                String usuarioLogin = form.getUsuarioLogin();
                if (usuarioLogin.isEmpty()) {
                    JOptionPane.showMessageDialog(view, "El nombre de usuario es obligatorio.", "Datos incompletos", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Persona datos = form.getPersonaFromFields();
                personaDAO.actualizarPersona(datos);
                personaDAO.actualizarEmpleado(empleado.getIdPersona(), empleado.getSalario(), empleado.getTipoEmpleado(), usuarioLogin, nuevaContrasena);
                JOptionPane.showMessageDialog(view, "Perfil actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarSolicitudesPendientes();
            } catch (Exception ex) { showError("Error al actualizar el perfil", ex); }
        });

        form.btnCancelar.addActionListener(e -> cargarSolicitudesPendientes());
        form.btnVolver.addActionListener(e -> cargarSolicitudesPendientes());

        cambiarPanelPrincipal(form);
    }

    private void showError(String mensaje, Exception ex) {
        JOptionPane.showMessageDialog(view, mensaje + ": " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    // ─── PROCEDIMIENTO ESTABLECIDO POR EL DESTINO ───
    private void cambiarPanelPrincipal(JPanel nuevoPanel) {
        view.remove(view.jPanelPrincipal);
        view.jPanelPrincipal = nuevoPanel;
        view.add(view.jPanelPrincipal, java.awt.BorderLayout.CENTER);

        view.revalidate();
        view.repaint();
    }
}