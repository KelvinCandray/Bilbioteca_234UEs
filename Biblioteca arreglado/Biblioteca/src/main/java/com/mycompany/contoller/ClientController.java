package com.mycompany.contoller;

import com.mycompany.DAO.LibroDAO;
import com.mycompany.DAO.PersonaDAO;
import com.mycompany.DAO.PrestamoDAO;
import com.mycompany.DAO.impl.LibroDAOImpl;
import com.mycompany.DAO.impl.PersonaDAOImpl;
import com.mycompany.DAO.impl.PrestamoDAOImpl;
import com.mycompany.model.Ejemplar;
import com.mycompany.model.Libro;
import com.mycompany.model.Multa;
import com.mycompany.model.Persona;
import com.mycompany.model.Prestamo;
import com.mycompany.model.Usuario;
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
 * Controlador para el rol de Usuario (cliente/lector).
 *
 * @author betuel
 */
public class ClientController {

    private final Usuario usuario;
    private MainInterface view;
    
    // Conexiones con la base de datos (DAOs)
    private final PrestamoDAO prestamoDAO;
    private final LibroDAO libroDAO;
    private final PersonaDAO personaDAO;

    public ClientController(Usuario usuario) {
        this.usuario = usuario;
        this.prestamoDAO = new PrestamoDAOImpl();
        this.libroDAO = new LibroDAOImpl();
        this.personaDAO = new PersonaDAOImpl();
        
        view = new MainInterface(usuario.toString(), 0);
        view.setVisible(true);

        AuditoriaPrestamos.ejecutar();
        buttonActions();
        
        // Carga el catálogo por defecto al iniciar como lo hacía el origen
        mostrarCatalogoLibros();
    }

    private void buttonActions() {
        view.logOut.addActionListener(e -> {
            view.dispose();
            new LogInController();
        });

        // Botón 1: Catálogo de libros
        view.jPanelLeft.boton1.addActionListener(e -> {
            mostrarCatalogoLibros();
        });

        // Botón 2: Mis préstamos
        view.jPanelLeft.boton2.addActionListener(e -> {
            mostrarMisPrestamos();
        });

        // Botón 3: Multas Pendientes
        view.jPanelLeft.boton3.addActionListener(e -> {
            mostrarMisMultas();
        });
        
        // Mi perfil
        view.jPanelLeft.miPerfil.addActionListener(e -> mostrarMiPerfil());
    }

    private void cambiarPanelPrincipal(JPanel nuevoPanel) {
        view.remove(view.jPanelPrincipal); 
        view.jPanelPrincipal = nuevoPanel;  
        view.add(view.jPanelPrincipal, java.awt.BorderLayout.CENTER); 

        view.revalidate();
        view.repaint();
    }

    // ─── LÓGICA DE TABLAS Y CONEXIÓN A BASE DE DATOS ─────────────────────────

    private void mostrarCatalogoLibros() {
        try {
            List<Libro> libros = libroDAO.obtenerLibros();
            List<List<Object>> data = new ArrayList<>();
            
            // Encabezados de la tabla
            data.add(List.of("ISBN", "Título", "Editorial", "Año", "Edición", "Autores", "Categorías", "Disponibles"));
            
            for (Libro l : libros) {
                data.add(List.of(
                    l.getIsbn(), l.getTitulo(), l.getEditorial(), l.getAnio(), l.getTipoEdicion(),
                    l.getAutores() != null ? l.getAutores() : "",
                    l.getCategorias() != null ? l.getCategorias() : "",
                    l.getDisponibles()
                ));
            }
            
            // Mantiene los textos de los botones solicitados en el destino
            DataViewer panelCatalogo = new DataViewer(
                data, "Catalogo de libros", "Título", "ISBN", 
                "Enviar solicitud", "", ""
            );
            
            // Acción para el botón principal "Enviar solicitud" (btnReadCreate)
            panelCatalogo.btnReadCreate.addActionListener(e -> {
                int filaSeleccionada = panelCatalogo.table.getSelectedRow();
                if (filaSeleccionada >= 0) {
                    String isbn = panelCatalogo.table.getValueAt(filaSeleccionada, 0).toString();
                    String titulo = panelCatalogo.table.getValueAt(filaSeleccionada, 1).toString();
                    procesarSolicitudPrestamo(isbn, titulo);
                } else {
                    JOptionPane.showMessageDialog(view, "Por favor, selecciona un libro de la tabla primero.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            });
            
            cambiarPanelPrincipal(panelCatalogo);
        } catch (StringIndexOutOfBoundsException ex) {
            // Captura por si la tabla viene vacía en cabeceras o inicialización
            showError("Error de formato en la tabla de catálogo", ex);
        } catch (Exception ex) {
            showError("Error al cargar el catálogo", ex);
        }
    }

    private void mostrarMisPrestamos() {
        try {
            List<Prestamo> misPrestamos = prestamoDAO.obtenerPrestamosDeUsuario(usuario.getIdPersona());
            List<List<Object>> data = new ArrayList<>();
            
            // Encabezados de la tabla
            data.add(List.of("ID Préstamo", "Libro", "Ejemplar", "Fecha Retiro", "Fecha Límite", "Devolución Real", "Estado"));
            
            for (Prestamo p : misPrestamos) {
                data.add(List.of(
                    p.getIdPrestamo(),
                    p.getTituloLibro() != null ? p.getTituloLibro() : "ISBN: " + p.getIsbn(),
                    p.getIdEjemplar(),
                    p.getFechaRetiro() != null ? p.getFechaRetiro() : "Pendiente de aprobación",
                    p.getFechaIdealRegreso() != null ? p.getFechaIdealRegreso() : "Pendiente de aprobación",
                    p.getFechaRealRegreso() != null ? p.getFechaRealRegreso() : "Pendiente",
                    p.getEstado()
                ));
            }
            
            // Mantiene la firma visual del destino
            DataViewer panelPrestamos = new DataViewer(
                data, "Mis prestamos", "", "", 
                "", "Ver detalles", ""
            );
            
            cambiarPanelPrincipal(panelPrestamos);
        } catch (Exception ex) {
            showError("Error al obtener tus préstamos", ex);
        }
    }

    private void mostrarMisMultas() {
        try {
            List<Multa> misMultas = prestamoDAO.obtenerMultasDeUsuario(usuario.getIdPersona());
            List<List<Object>> data = new ArrayList<>();
            
            // Encabezados de la tabla
            data.add(List.of("ID Multa", "ID Préstamo", "Monto de Deuda ($)", "Estado de Multa"));
            
            for (Multa m : misMultas) {
                data.add(List.of(m.getIdMulta(), m.getIdPrestamo(), m.getMonto(), m.getEstado()));
            }
            
            // Mantiene la firma visual del destino
            DataViewer panelMultas = new DataViewer(
                data, "Multas Pendientes", "", "", 
                "", "Ver detalles", ""
            );
            
            cambiarPanelPrincipal(panelMultas);
        } catch (Exception ex) {
            showError("Error al obtener tus multas", ex);
        }
    }

    // ─── ACCIONES DE NEGOCIO Y TRANSACCIONES ─────────────────────────────────

    private void procesarSolicitudPrestamo(String isbn, String titulo) {
        int confirmacion = JOptionPane.showConfirmDialog(view, 
                "¿Deseas solicitar un ejemplar de: \"" + titulo + "\"?", 
                "Confirmar Solicitud", JOptionPane.YES_NO_OPTION);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                solicitarPrestamoPorIsbn(isbn);
                JOptionPane.showMessageDialog(view, "¡Solicitud procesada con éxito! Revisa su estado en 'Mis prestamos'.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                mostrarCatalogoLibros(); 
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view, ex.getMessage(), "Error de Solicitud", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void solicitarPrestamoPorIsbn(String isbn) throws Exception {
        Ejemplar ej = libroDAO.buscarEjemplarDisponible(isbn);
        if (ej == null) throw new Exception("No hay ejemplares disponibles para este libro en este momento.");
        prestamoDAO.solicitarPrestamo(usuario.getIdPersona(), ej.getIdEjemplar());
    }

    /** "Mi perfil": el lector puede editar sus datos de contacto y credenciales. */
    private void mostrarMiPerfil() {
        PersonaData form = new PersonaData();
        form.setPersona(usuario);
        form.setUsuarioLogin(usuario.getUsuario());
        form.configurarParaMiPerfil(false);
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
                personaDAO.actualizarUsuario(usuario.getIdPersona(), usuarioLogin, nuevaContrasena);
                JOptionPane.showMessageDialog(view, "Perfil actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                mostrarCatalogoLibros();
            } catch (Exception ex) { showError("Error al actualizar el perfil", ex); }
        });

        form.btnCancelar.addActionListener(e -> mostrarCatalogoLibros());
        form.btnVolver.addActionListener(e -> mostrarCatalogoLibros());

        cambiarPanelPrincipal(form);
    }

    private void showError(String mensaje, Exception ex) {
        JOptionPane.showMessageDialog(view, mensaje + ": " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}