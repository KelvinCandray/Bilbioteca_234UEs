package com.mycompany.contoller;

import com.mycompany.DAO.*;
import com.mycompany.DAO.impl.*;
import com.mycompany.model.*;
import com.mycompany.view.AutorData;
import com.mycompany.view.bookData;
import com.mycompany.view.CategoriaData;
import com.mycompany.view.DataViewer;
import com.mycompany.view.EjemplarData;
import com.mycompany.view.MainInterface;
import com.mycompany.view.MultaData;
import com.mycompany.view.PersonaData;
import com.mycompany.view.PrestamoData;
import com.mycompany.util.AuditoriaPrestamos;
import com.mycompany.util.Validations;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Controlador de Administrador unificado.
 * Integra la navegación de la vista con la lógica de datos real.
 */
public class AdminController {

    private final Empleado administrador;
    private final MainInterface view;
    
    // DAOs para conexión
    private final PersonaDAO personaDAO = new PersonaDAOImpl();
    private final LibroDAO libroDAO = new LibroDAOImpl();
    private final PrestamoDAO prestamoDAO = new PrestamoDAOImpl();

    public AdminController(Empleado administrador) {
        this.administrador = administrador;
        this.view = new MainInterface(administrador.toString(), 2);
        this.view.setVisible(true);

        AuditoriaPrestamos.ejecutar();
        buttonActions();
        // Carga inicial
        mostrarLibros();
    }

    private void buttonActions() {
        view.logOut.addActionListener(e -> { view.dispose(); new LogInController(); });

        view.jPanelLeft.boton1.addActionListener(e -> mostrarEjemplares());
        view.jPanelLeft.boton2.addActionListener(e -> mostrarLibros());
        view.jPanelLeft.boton3.addActionListener(e -> mostrarCategorias());
        view.jPanelLeft.boton4.addActionListener(e -> mostrarAutores());
        view.jPanelLeft.boton5.addActionListener(e -> mostrarHistorialPrestamos());
        view.jPanelLeft.boton6.addActionListener(e -> mostrarMultas());
        view.jPanelLeft.boton7.addActionListener(e -> mostrarUsuariosYPersonal());
        view.jPanelLeft.miPerfil.addActionListener(e -> mostrarMiPerfil());
    }

    // --- LÓGICA DE RENDERIZADO (Respetando el destino) ---

    private void cambiarPanelPrincipal(JPanel nuevoPanel) {
        view.jPanelPrincipal.removeAll();
        view.jPanelPrincipal.setLayout(new BorderLayout());
        view.jPanelPrincipal.add(nuevoPanel, BorderLayout.CENTER);
        view.jPanelPrincipal.revalidate();
        view.jPanelPrincipal.repaint();
    }

    // --- MÉTODOS DE DATOS ---

    private void mostrarLibros() {
        try {
            List<Libro> libros = libroDAO.obtenerLibros();
            List<List<Object>> data = new ArrayList<>();
            data.add(List.of("ISBN", "Título", "Editorial", "Año", "Disp.", "Prest."));
            
            for (Libro l : libros) {
                data.add(List.of(l.getIsbn(), l.getTitulo(), l.getEditorial(), l.getAnio(), l.getDisponibles(), l.getPrestados()));
            }
            DataViewer panel = new DataViewer(data, "Catálogo de libros", "Título", "ISBN", "Registrar", "Editar", "Eliminar");

            panel.btnReadCreate.addActionListener(e -> mostrarFormularioLibro(null));

            panel.btnUpdate.addActionListener(e -> {
                int fila = panel.table.getSelectedRow();
                if (fila < 0) {
                    JOptionPane.showMessageDialog(view, "Seleccione un libro de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String isbn = panel.table.getValueAt(fila, 0).toString();
                Libro l = new Libro();
                l.setIsbn(isbn);
                l.setTitulo(panel.table.getValueAt(fila, 1).toString());
                l.setEditorial(panel.table.getValueAt(fila, 2).toString());
                l.setAnio(Integer.parseInt(panel.table.getValueAt(fila, 3).toString()));
                // El "Tipo de edición" no viaja en esta tabla resumida; lo completamos con una consulta puntual
                // reutilizando obtenerLibros() ya cargado en memoria para no golpear la BD dos veces.
                for (Libro original : libros) {
                    if (original.getIsbn().equals(isbn)) { l.setTipoEdicion(original.getTipoEdicion()); break; }
                }
                mostrarFormularioLibro(l);
            });

            panel.btnDelete.addActionListener(e -> {
                int fila = panel.table.getSelectedRow();
                if (fila < 0) {
                    JOptionPane.showMessageDialog(view, "Seleccione un libro de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String isbn = panel.table.getValueAt(fila, 0).toString();
                String titulo = panel.table.getValueAt(fila, 1).toString();
                int confirmacion = JOptionPane.showConfirmDialog(view,
                        "¿Eliminar el libro \"" + titulo + "\" y todos sus ejemplares sin historial?",
                        "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirmacion == JOptionPane.YES_OPTION) {
                    try {
                        libroDAO.eliminarLibro(isbn);
                        JOptionPane.showMessageDialog(view, "Libro eliminado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        mostrarLibros();
                    } catch (Exception ex) { showError(ex); }
                }
            });

            cambiarPanelPrincipal(panel);
        } catch (Exception ex) { showError(ex); }
    }

    /**
     * Muestra el formulario de alta/edición de un libro, junto con la selección
     * múltiple de autores y categorías asociadas.
     * libroExistente == null  -> modo "Nuevo" (pide cantidad inicial de ejemplares)
     * libroExistente != null  -> modo "Editar" (ISBN bloqueado, ejemplares se gestionan aparte)
     */
    private void mostrarFormularioLibro(Libro libroExistente) {
        try {
            bookData form = new bookData();
            form.setListaAutoresDisponibles(libroDAO.obtenerAutores());
            form.setListaCategoriasDisponibles(libroDAO.obtenerCategorias());

            if (libroExistente != null) {
                form.setLibro(libroExistente);
                form.activarModoEdicion();
                form.setAutoresSeleccionados(libroDAO.obtenerAutoresDeLibro(libroExistente.getIsbn()));
                form.setCategoriasSeleccionadas(libroDAO.obtenerCategoriasDeLibro(libroExistente.getIsbn()));
            }
            form.setVolverVisible(true);

            form.btnGuardar.addActionListener(e -> {
                try {
                    Libro libro;
                    try {
                        libro = form.getLibroFromFields();
                    } catch (NumberFormatException nfe) {
                        JOptionPane.showMessageDialog(view, "El año de publicación debe ser un número entero.", "Datos inválidos", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    if (libro.getIsbn().isEmpty() || libro.getTitulo().isEmpty()) {
                        JOptionPane.showMessageDialog(view, "ISBN y título son obligatorios.", "Datos incompletos", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    List<Integer> idsAutores = form.getIdsAutoresSeleccionados();
                    List<Integer> idsCategorias = form.getIdsCategoriasSeleccionadas();
                    if (idsAutores.isEmpty() || idsCategorias.isEmpty()) {
                        JOptionPane.showMessageDialog(view, "Seleccione al menos un autor y una categoría.", "Datos incompletos", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    if (libroExistente == null) {
                        int cantidad;
                        try {
                            cantidad = form.getCantidadEjemplares();
                        } catch (NumberFormatException nfe) {
                            JOptionPane.showMessageDialog(view, "La cantidad de ejemplares debe ser un número entero.", "Datos inválidos", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        libroDAO.registrarLibro(libro, cantidad);
                        libroDAO.actualizarAutoresDeLibro(libro.getIsbn(), idsAutores);
                        libroDAO.actualizarCategoriasDeLibro(libro.getIsbn(), idsCategorias);
                        JOptionPane.showMessageDialog(view, "Libro registrado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        libroDAO.actualizarLibro(libro);
                        libroDAO.actualizarAutoresDeLibro(libro.getIsbn(), idsAutores);
                        libroDAO.actualizarCategoriasDeLibro(libro.getIsbn(), idsCategorias);
                        JOptionPane.showMessageDialog(view, "Libro actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    }
                    mostrarLibros();
                } catch (Exception ex) { showError(ex); }
            });

            form.btnCancelar.addActionListener(e -> mostrarLibros());
            form.btnVolver.addActionListener(e -> mostrarLibros());

            cambiarPanelPrincipal(form);
        } catch (Exception ex) { showError(ex); }
    }

    private void mostrarCategorias() {
        try {
            List<Categoria> cats = libroDAO.obtenerCategorias();
            List<List<Object>> data = new ArrayList<>();
            data.add(List.of("ID", "Nombre", "Edad Min"));
            for (Categoria c : cats) { data.add(List.of(c.getIdCategoria(), c.getNombreCategoria(), c.getEdadMinima())); }
            DataViewer panel = new DataViewer(data, "Categorias", "Nombre", "ID", "Nuevo", "Editar", "Eliminar");

            panel.btnReadCreate.addActionListener(e -> mostrarFormularioCategoria(null));

            panel.btnUpdate.addActionListener(e -> {
                int fila = panel.table.getSelectedRow();
                if (fila < 0) {
                    JOptionPane.showMessageDialog(view, "Seleccione una categoría de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Categoria c = new Categoria();
                c.setIdCategoria(Integer.parseInt(panel.table.getValueAt(fila, 0).toString()));
                c.setNombreCategoria(panel.table.getValueAt(fila, 1).toString());
                c.setEdadMinima(Integer.parseInt(panel.table.getValueAt(fila, 2).toString()));
                mostrarFormularioCategoria(c);
            });

            panel.btnDelete.addActionListener(e -> {
                int fila = panel.table.getSelectedRow();
                if (fila < 0) {
                    JOptionPane.showMessageDialog(view, "Seleccione una categoría de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int idCategoria = Integer.parseInt(panel.table.getValueAt(fila, 0).toString());
                String nombre = panel.table.getValueAt(fila, 1).toString();
                int confirmacion = JOptionPane.showConfirmDialog(view,
                        "¿Eliminar la categoría \"" + nombre + "\"? Se quitará de todos los libros donde aparece.",
                        "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirmacion == JOptionPane.YES_OPTION) {
                    try {
                        libroDAO.eliminarCategoria(idCategoria);
                        JOptionPane.showMessageDialog(view, "Categoría eliminada.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        mostrarCategorias();
                    } catch (Exception ex) { showError(ex); }
                }
            });

            cambiarPanelPrincipal(panel);
        } catch (Exception ex) { showError(ex); }
    }

    /**
     * Muestra el formulario de alta/edición de categoría.
     * categoriaExistente == null  -> modo "Nuevo"
     * categoriaExistente != null  -> modo "Editar" (precarga los campos)
     */
    private void mostrarFormularioCategoria(Categoria categoriaExistente) {
        CategoriaData form = new CategoriaData();
        if (categoriaExistente != null) {
            form.setCategoria(categoriaExistente);
        }
        form.setVolverVisible(true);

        form.btnGuardar.addActionListener(e -> {
            try {
                String nombre = form.txtNombre.getText().trim();
                if (nombre.isEmpty()) {
                    JOptionPane.showMessageDialog(view, "El nombre de la categoría es obligatorio.", "Datos incompletos", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int edadMinima;
                try {
                    String txtEdad = form.txtEdadMinima.getText().trim();
                    edadMinima = txtEdad.isEmpty() ? 0 : Integer.parseInt(txtEdad);
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(view, "La edad mínima debe ser un número entero.", "Datos inválidos", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Categoria c = new Categoria();
                c.setNombreCategoria(nombre);
                c.setEdadMinima(edadMinima);

                if (categoriaExistente == null) {
                    libroDAO.registrarCategoria(c);
                    JOptionPane.showMessageDialog(view, "Categoría registrada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    c.setIdCategoria(categoriaExistente.getIdCategoria());
                    libroDAO.actualizarCategoria(c);
                    JOptionPane.showMessageDialog(view, "Categoría actualizada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                }
                mostrarCategorias();
            } catch (Exception ex) { showError(ex); }
        });

        form.btnCancelar.addActionListener(e -> mostrarCategorias());
        form.btnVolver.addActionListener(e -> mostrarCategorias());

        cambiarPanelPrincipal(form);
    }

    private void mostrarAutores() {
        try {
            List<Autor> auts = libroDAO.obtenerAutores();
            List<List<Object>> data = new ArrayList<>();
            data.add(List.of("ID", "Nombre", "Apellido", "Nacionalidad"));
            for (Autor a : auts) { data.add(List.of(a.getIdAutor(), a.getNombre(), a.getApellido(), a.getNacionalidad() != null ? a.getNacionalidad() : "")); }
            DataViewer panel = new DataViewer(data, "Autores", "Apellido", "Nacionalidad", "Nuevo", "Editar", "Eliminar");

            panel.btnReadCreate.addActionListener(e -> mostrarFormularioAutor(null));

            panel.btnUpdate.addActionListener(e -> {
                int fila = panel.table.getSelectedRow();
                if (fila < 0) {
                    JOptionPane.showMessageDialog(view, "Seleccione un autor de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Autor a = new Autor();
                a.setIdAutor(Integer.parseInt(panel.table.getValueAt(fila, 0).toString()));
                a.setNombre(panel.table.getValueAt(fila, 1).toString());
                a.setApellido(panel.table.getValueAt(fila, 2).toString());
                Object nacionalidad = panel.table.getValueAt(fila, 3);
                a.setNacionalidad(nacionalidad != null ? nacionalidad.toString() : "");
                mostrarFormularioAutor(a);
            });

            panel.btnDelete.addActionListener(e -> {
                int fila = panel.table.getSelectedRow();
                if (fila < 0) {
                    JOptionPane.showMessageDialog(view, "Seleccione un autor de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int idAutor = Integer.parseInt(panel.table.getValueAt(fila, 0).toString());
                String nombreCompleto = panel.table.getValueAt(fila, 1) + " " + panel.table.getValueAt(fila, 2);
                int confirmacion = JOptionPane.showConfirmDialog(view,
                        "¿Eliminar al autor \"" + nombreCompleto + "\"? Se quitará de todos los libros donde aparece.",
                        "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirmacion == JOptionPane.YES_OPTION) {
                    try {
                        libroDAO.eliminarAutor(idAutor);
                        JOptionPane.showMessageDialog(view, "Autor eliminado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        mostrarAutores();
                    } catch (Exception ex) { showError(ex); }
                }
            });

            cambiarPanelPrincipal(panel);
        } catch (Exception ex) { showError(ex); }
    }

    /**
     * Muestra el formulario de alta/edición de autor.
     * autorExistente == null  -> modo "Nuevo"
     * autorExistente != null  -> modo "Editar" (precarga los campos)
     */
    private void mostrarFormularioAutor(Autor autorExistente) {
        AutorData form = new AutorData();
        if (autorExistente != null) {
            form.setAutor(autorExistente);
        }
        form.setVolverVisible(true);

        form.btnGuardar.addActionListener(e -> {
            try {
                String nombre = form.txtNombre.getText().trim();
                String apellido = form.txtApellido.getText().trim();
                String nacionalidad = form.txtNacionalidad.getText().trim();

                if (nombre.isEmpty() || apellido.isEmpty()) {
                    JOptionPane.showMessageDialog(view, "Nombre y apellido son obligatorios.", "Datos incompletos", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Autor a = new Autor();
                a.setNombre(nombre);
                a.setApellido(apellido);
                a.setNacionalidad(nacionalidad.isEmpty() ? null : nacionalidad);

                if (autorExistente == null) {
                    libroDAO.registrarAutor(a);
                    JOptionPane.showMessageDialog(view, "Autor registrado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    a.setIdAutor(autorExistente.getIdAutor());
                    libroDAO.actualizarAutor(a);
                    JOptionPane.showMessageDialog(view, "Autor actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                }
                mostrarAutores();
            } catch (Exception ex) { showError(ex); }
        });

        form.btnCancelar.addActionListener(e -> mostrarAutores());
        form.btnVolver.addActionListener(e -> mostrarAutores());

        cambiarPanelPrincipal(form);
    }

    private void mostrarHistorialPrestamos() {
        try {
            List<Prestamo> historial = prestamoDAO.obtenerHistorialCompleto();
            List<List<Object>> data = new ArrayList<>();
            data.add(List.of("ID", "Usuario", "Libro", "Estado"));
            for (Prestamo p : historial) { data.add(List.of(p.getIdPrestamo(), p.getNombreUsuario(), p.getTituloLibro(), p.getEstado())); }
            // Solo lectura: mutar un préstamo a mano rompería la sincronía con el ejemplar y las
            // multas asociadas. Los cambios de estado reales se hacen desde el flujo de Empleado.
            DataViewer panel = new DataViewer(data, "Historial Préstamos", "Estado", "Usuario", "Ver detalles", "", "");

            panel.btnReadCreate.addActionListener(e -> {
                int fila = panel.table.getSelectedRow();
                if (fila < 0) {
                    JOptionPane.showMessageDialog(view, "Seleccione un préstamo de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int idPrestamo = Integer.parseInt(panel.table.getValueAt(fila, 0).toString());
                Prestamo seleccionado = historial.stream()
                        .filter(p -> p.getIdPrestamo() == idPrestamo)
                        .findFirst().orElse(null);
                if (seleccionado == null) return;

                PrestamoData detalle = new PrestamoData();
                detalle.setPrestamo(seleccionado);
                detalle.activarSoloLectura();
                detalle.setVolverVisible(true);
                detalle.btnCancelar.addActionListener(ev -> mostrarHistorialPrestamos());
                detalle.btnVolver.addActionListener(ev -> mostrarHistorialPrestamos());
                cambiarPanelPrincipal(detalle);
            });

            cambiarPanelPrincipal(panel);
        } catch (Exception ex) { showError(ex); }
    }

    private void mostrarMultas() {
        try {
            List<Multa> multas = prestamoDAO.obtenerTodasLasMultas();
            List<List<Object>> data = new ArrayList<>();
            data.add(List.of("ID", "Usuario", "Monto", "Estado"));
            for (Multa m : multas) { data.add(List.of(m.getIdMulta(), m.getIdUsuario(), m.getMonto(), m.getEstado())); }
            DataViewer panel = new DataViewer(data, "Multas", "Estado", "Usuario", "Pagar", "Modificar", "Exonerar");

            panel.btnReadCreate.addActionListener(e -> {
                int fila = panel.table.getSelectedRow();
                if (fila < 0) {
                    JOptionPane.showMessageDialog(view, "Seleccione una multa de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int idMulta = Integer.parseInt(panel.table.getValueAt(fila, 0).toString());
                double monto = Double.parseDouble(panel.table.getValueAt(fila, 2).toString());
                String estado = panel.table.getValueAt(fila, 3).toString();
                if ("Pagada".equalsIgnoreCase(estado)) {
                    JOptionPane.showMessageDialog(view, "Esta multa ya está pagada.", "Información", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                String pagoInput = JOptionPane.showInputDialog(view,
                        "Monto adeudado: $" + monto + "\nIngrese el monto a cobrar:", monto);
                if (pagoInput != null) {
                    try {
                        prestamoDAO.registrarPago(idMulta, Double.parseDouble(pagoInput));
                        JOptionPane.showMessageDialog(view, "Pago registrado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        mostrarMultas();
                    } catch (NumberFormatException nfe) {
                        JOptionPane.showMessageDialog(view, "El monto debe ser un número.", "Datos inválidos", JOptionPane.WARNING_MESSAGE);
                    } catch (Exception ex) { showError(ex); }
                }
            });

            panel.btnUpdate.addActionListener(e -> {
                int fila = panel.table.getSelectedRow();
                if (fila < 0) {
                    JOptionPane.showMessageDialog(view, "Seleccione una multa de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int idMulta = Integer.parseInt(panel.table.getValueAt(fila, 0).toString());
                Multa seleccionada = multas.stream().filter(m -> m.getIdMulta() == idMulta).findFirst().orElse(null);
                if (seleccionada == null) return;
                if ("Pagada".equalsIgnoreCase(seleccionada.getEstado())) {
                    JOptionPane.showMessageDialog(view, "No se puede modificar una multa ya pagada.", "Acción no permitida", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                mostrarFormularioMulta(seleccionada);
            });

            panel.btnDelete.addActionListener(e -> {
                int fila = panel.table.getSelectedRow();
                if (fila < 0) {
                    JOptionPane.showMessageDialog(view, "Seleccione una multa de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int idMulta = Integer.parseInt(panel.table.getValueAt(fila, 0).toString());
                String estado = panel.table.getValueAt(fila, 3).toString();
                if ("Pagada".equalsIgnoreCase(estado)) {
                    JOptionPane.showMessageDialog(view, "Esta multa ya está cerrada.", "Información", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                int confirmacion = JOptionPane.showConfirmDialog(view,
                        "¿Exonerar (perdonar) esta multa? Quedará cerrada con monto $0.",
                        "Confirmar exoneración", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirmacion == JOptionPane.YES_OPTION) {
                    try {
                        prestamoDAO.exonerarMulta(idMulta);
                        JOptionPane.showMessageDialog(view, "Multa exonerada.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        mostrarMultas();
                    } catch (Exception ex) { showError(ex); }
                }
            });

            cambiarPanelPrincipal(panel);
        } catch (Exception ex) { showError(ex); }
    }

    private void mostrarFormularioMulta(Multa multa) {
        MultaData form = new MultaData();
        form.setMulta(multa);
        form.setVolverVisible(true);

        form.btnGuardar.addActionListener(e -> {
            try {
                double nuevoMonto;
                try {
                    nuevoMonto = form.getMontoEditado();
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(view, "El monto debe ser un número.", "Datos inválidos", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String nuevaFecha = form.getFechaMaximaEditada();
                if (nuevaFecha.isEmpty()) {
                    JOptionPane.showMessageDialog(view, "La fecha máxima de pago es obligatoria.", "Datos incompletos", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                prestamoDAO.modificarMulta(multa.getIdMulta(), nuevoMonto, nuevaFecha);
                JOptionPane.showMessageDialog(view, "Multa actualizada.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                mostrarMultas();
            } catch (Exception ex) { showError(ex); }
        });

        form.btnCancelar.addActionListener(e -> mostrarMultas());
        form.btnVolver.addActionListener(e -> mostrarMultas());

        cambiarPanelPrincipal(form);
    }

    private void mostrarUsuariosYPersonal() {
        try {
            List<Object[]> pers = personaDAO.obtenerPersonasCompletas();
            List<List<Object>> data = new ArrayList<>();
            data.add(List.of("DUI", "Nombre", "Rol", "Usuario"));
            for (Object[] p : pers) {
                data.add(List.of(p[0], p[1] + " " + p[2], rolDeFila(p), p[14] != null ? p[14] : ""));
            }
            DataViewer panel = new DataViewer(data, "Usuarios", "Nombre", "DUI", "Registrar", "Editar", "Baja");

            panel.btnReadCreate.addActionListener(e -> mostrarFormularioPersona(null));

            panel.btnUpdate.addActionListener(e -> {
                int fila = panel.table.getSelectedRow();
                if (fila < 0) {
                    JOptionPane.showMessageDialog(view, "Seleccione una persona de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int idPersona = Integer.parseInt(panel.table.getValueAt(fila, 0).toString());
                Object[] filaCompleta = pers.stream().filter(p -> (int) p[0] == idPersona).findFirst().orElse(null);
                if (filaCompleta == null) return;
                mostrarFormularioPersona(filaCompleta);
            });

            panel.btnDelete.addActionListener(e -> {
                int fila = panel.table.getSelectedRow();
                if (fila < 0) {
                    JOptionPane.showMessageDialog(view, "Seleccione una persona de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int idPersona = Integer.parseInt(panel.table.getValueAt(fila, 0).toString());
                String nombre = panel.table.getValueAt(fila, 1).toString();
                int confirmacion = JOptionPane.showConfirmDialog(view,
                        "¿Dar de baja a \"" + nombre + "\"? Esta acción no se puede deshacer.",
                        "Confirmar baja", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirmacion == JOptionPane.YES_OPTION) {
                    try {
                        personaDAO.eliminarPersona(idPersona);
                        JOptionPane.showMessageDialog(view, "Persona dada de baja.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        mostrarUsuariosYPersonal();
                    } catch (Exception ex) { showError(ex); }
                }
            });

            cambiarPanelPrincipal(panel);
        } catch (Exception ex) { showError(ex); }
    }

    /** Etiqueta amigable de rol a partir de una fila de obtenerPersonasCompletas(). */
    private String rolDeFila(Object[] p) {
        String esEmpleado = (String) p[9];
        String esUsuario = (String) p[8];
        if (!"No".equals(esEmpleado)) return esEmpleado; // Gerente / Bibliotecario / Vigilante
        if ("Sí".equals(esUsuario)) return "Usuario (Lector)";
        return "Sin rol asignado";
    }

    /**
     * Muestra el formulario de alta/edición de una persona (Usuario o Empleado).
     * filaExistente == null  -> modo "Registrar" (elige el rol)
     * filaExistente != null  -> modo "Editar" (rol fijo al que ya tiene)
     */
    private void mostrarFormularioPersona(Object[] filaExistente) {
        PersonaData form = new PersonaData();
        boolean esEdicion = filaExistente != null;
        boolean esEmpleado = esEdicion && !"No".equals(filaExistente[9]);

        if (esEdicion) {
            Persona datosActuales = new Persona();
            datosActuales.setIdPersona((int) filaExistente[0]);
            datosActuales.setPrimerNombre((String) filaExistente[1]);
            datosActuales.setApellido((String) filaExistente[2]);
            datosActuales.setCorreo((String) filaExistente[3]);
            datosActuales.setTelefono((String) filaExistente[4]);
            datosActuales.setFechaNacimiento((String) filaExistente[5]);
            datosActuales.setDepartamento((String) filaExistente[6]);
            datosActuales.setMunicipio((String) filaExistente[7]);
            datosActuales.setPasaje((String) filaExistente[11]);
            datosActuales.setNumeroCasa((String) filaExistente[12]);
            datosActuales.setColonia((String) filaExistente[13]);
            form.setPersona(datosActuales);
            form.setUsuarioLogin(filaExistente[14] != null ? filaExistente[14].toString() : "");
            if (esEmpleado) {
                form.setTipoEmpleado((String) filaExistente[9]);
                Object salario = filaExistente[10];
                if (salario instanceof Double d) form.setSalario(d);
            }
            form.configurarParaEdicionAdmin(esEmpleado);
        } else {
            form.configurarParaRegistroAdmin();
        }
        form.setVolverVisible(true);

        form.btnGuardar.addActionListener(e -> {
            try {
                String errorCorreo = Validations.correoValidation(form.txtCorreo);
                if (!errorCorreo.isEmpty()) {
                    JOptionPane.showMessageDialog(view, errorCorreo, "Correo inválido", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String nuevaContrasena = form.getContrasena();
                // En alta siempre se exige contraseña; en edición solo se valida si se quiere cambiar.
                if (!esEdicion || !nuevaContrasena.isEmpty()) {
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

                Persona datos;
                try {
                    datos = form.getPersonaFromFields();
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(view, "El DUI / ID Persona debe ser un número entero.", "Datos inválidos", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (datos.getPrimerNombre().isEmpty() || datos.getApellido().isEmpty() || datos.getFechaNacimiento().isEmpty()) {
                    JOptionPane.showMessageDialog(view, "Nombre, apellido y fecha de nacimiento son obligatorios.", "Datos incompletos", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (!esEdicion) {
                    // ALTA: registrar como Usuario o como Empleado según el rol elegido
                    if (form.esRolEmpleado()) {
                        double salario;
                        try {
                            salario = form.getSalarioFromFields();
                        } catch (NumberFormatException nfe) {
                            JOptionPane.showMessageDialog(view, "El salario debe ser un número.", "Datos inválidos", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        Empleado empleado = new Empleado();
                        copiarDatosPersona(datos, empleado);
                        empleado.setSalario(salario);
                        empleado.setTipoEmpleado(form.getTipoEmpleadoSeleccionado());
                        empleado.setUsuario(usuarioLogin);
                        empleado.setContrasena(nuevaContrasena);
                        personaDAO.registrarEmpleado(empleado);
                    } else {
                        Usuario usuario = new Usuario();
                        copiarDatosPersona(datos, usuario);
                        usuario.setUsuario(usuarioLogin);
                        usuario.setContrasena(nuevaContrasena);
                        personaDAO.registrarUsuario(usuario);
                    }
                    JOptionPane.showMessageDialog(view, "Persona registrada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // EDICIÓN: actualizar datos base + credenciales/rol específico
                    personaDAO.actualizarPersona(datos);
                    if (esEmpleado) {
                        double salario;
                        try {
                            salario = form.getSalarioFromFields();
                        } catch (NumberFormatException nfe) {
                            JOptionPane.showMessageDialog(view, "El salario debe ser un número.", "Datos inválidos", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        personaDAO.actualizarEmpleado(datos.getIdPersona(), salario, form.getTipoEmpleadoSeleccionado(), usuarioLogin, nuevaContrasena);
                    } else {
                        personaDAO.actualizarUsuario(datos.getIdPersona(), usuarioLogin, nuevaContrasena);
                    }
                    JOptionPane.showMessageDialog(view, "Persona actualizada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                }
                mostrarUsuariosYPersonal();
            } catch (Exception ex) { showError(ex); }
        });

        form.btnCancelar.addActionListener(e -> mostrarUsuariosYPersonal());
        form.btnVolver.addActionListener(e -> mostrarUsuariosYPersonal());

        cambiarPanelPrincipal(form);
    }

    /** Copia los campos comunes de Persona hacia una subclase (Usuario o Empleado). */
    private void copiarDatosPersona(Persona origen, Persona destino) {
        destino.setIdPersona(origen.getIdPersona());
        destino.setPrimerNombre(origen.getPrimerNombre());
        destino.setApellido(origen.getApellido());
        destino.setCorreo(origen.getCorreo());
        destino.setTelefono(origen.getTelefono());
        destino.setFechaNacimiento(origen.getFechaNacimiento());
        destino.setPasaje(origen.getPasaje());
        destino.setNumeroCasa(origen.getNumeroCasa());
        destino.setColonia(origen.getColonia());
        destino.setMunicipio(origen.getMunicipio());
        destino.setDepartamento(origen.getDepartamento());
    }

    /** "Mi perfil" del propio administrador: puede editar sus datos de contacto y credenciales. */
    private void mostrarMiPerfil() {
        PersonaData form = new PersonaData();
        form.setPersona(administrador);
        form.setUsuarioLogin(administrador.getUsuario());
        form.setSalario(administrador.getSalario());
        form.setTipoEmpleado(administrador.getTipoEmpleado());
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
                personaDAO.actualizarEmpleado(administrador.getIdPersona(), administrador.getSalario(), administrador.getTipoEmpleado(), usuarioLogin, nuevaContrasena);
                JOptionPane.showMessageDialog(view, "Perfil actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                mostrarLibros();
            } catch (Exception ex) { showError(ex); }
        });

        form.btnCancelar.addActionListener(e -> mostrarLibros());
        form.btnVolver.addActionListener(e -> mostrarLibros());

        cambiarPanelPrincipal(form);
    }

    private void mostrarEjemplares() {
        try {
            List<Ejemplar> ejemplares = libroDAO.obtenerTodosLosEjemplares();
            List<List<Object>> data = new ArrayList<>();
            data.add(List.of("ID", "ISBN", "Título", "Estado"));
            for (Ejemplar ej : ejemplares) {
                data.add(List.of(ej.getIdEjemplar(), ej.getIsbn(), ej.getTituloLibro(), ej.getEstado()));
            }
            DataViewer panel = new DataViewer(data, "Ejemplares registrados", "Título", "ISBN", "Agregar ejemplares", "Editar estado", "Eliminar");

            // "Agregar ejemplares" no depende de una fila seleccionada: pide el ISBN y la cantidad
            panel.btnReadCreate.addActionListener(e -> mostrarDialogoAgregarEjemplares());

            panel.btnUpdate.addActionListener(e -> {
                int fila = panel.table.getSelectedRow();
                if (fila < 0) {
                    JOptionPane.showMessageDialog(view, "Seleccione un ejemplar de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String estadoActual = panel.table.getValueAt(fila, 3).toString();
                if ("Prestado".equals(estadoActual)) {
                    JOptionPane.showMessageDialog(view,
                            "Este ejemplar está actualmente prestado. Registre la devolución desde el módulo de Préstamos antes de cambiar su estado.",
                            "Acción no permitida", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Ejemplar ej = new Ejemplar();
                ej.setIdEjemplar(Integer.parseInt(panel.table.getValueAt(fila, 0).toString()));
                ej.setIsbn(panel.table.getValueAt(fila, 1).toString());
                ej.setEstado(estadoActual);
                mostrarFormularioEjemplar(ej);
            });

            panel.btnDelete.addActionListener(e -> {
                int fila = panel.table.getSelectedRow();
                if (fila < 0) {
                    JOptionPane.showMessageDialog(view, "Seleccione un ejemplar de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int idEjemplar = Integer.parseInt(panel.table.getValueAt(fila, 0).toString());
                int confirmacion = JOptionPane.showConfirmDialog(view,
                        "¿Eliminar el ejemplar #" + idEjemplar + "?",
                        "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirmacion == JOptionPane.YES_OPTION) {
                    try {
                        libroDAO.eliminarEjemplar(idEjemplar);
                        JOptionPane.showMessageDialog(view, "Ejemplar eliminado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        mostrarEjemplares();
                    } catch (Exception ex) { showError(ex); }
                }
            });

            cambiarPanelPrincipal(panel);
        } catch (Exception ex) { showError(ex); }
    }

    private void mostrarFormularioEjemplar(Ejemplar ejemplar) {
        EjemplarData form = new EjemplarData();
        form.setEjemplar(ejemplar);
        form.setVolverVisible(true);

        form.btnGuardar.addActionListener(e -> {
            try {
                libroDAO.actualizarEjemplar(form.getEjemplarFromFields());
                JOptionPane.showMessageDialog(view, "Estado del ejemplar actualizado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                mostrarEjemplares();
            } catch (Exception ex) { showError(ex); }
        });

        form.btnCancelar.addActionListener(e -> mostrarEjemplares());
        form.btnVolver.addActionListener(e -> mostrarEjemplares());

        cambiarPanelPrincipal(form);
    }

    /** Pequeño diálogo modal para agregar N copias nuevas a un libro existente por ISBN. */
    private void mostrarDialogoAgregarEjemplares() {
        JTextField txtIsbn = new JTextField();
        JTextField txtCantidad = new JTextField("1");
        Object[] mensaje = {
            "ISBN del libro:", txtIsbn,
            "Cantidad de ejemplares a agregar:", txtCantidad
        };
        int opcion = JOptionPane.showConfirmDialog(view, mensaje, "Agregar ejemplares", JOptionPane.OK_CANCEL_OPTION);
        if (opcion == JOptionPane.OK_OPTION) {
            try {
                String isbn = txtIsbn.getText().trim();
                int cantidad = Integer.parseInt(txtCantidad.getText().trim());
                libroDAO.agregarEjemplares(isbn, cantidad);
                JOptionPane.showMessageDialog(view, "Ejemplares agregados correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                mostrarEjemplares();
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(view, "La cantidad debe ser un número entero.", "Datos inválidos", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) { showError(ex); }
        }
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(view, "Error: " + ex.getMessage());
    }
}