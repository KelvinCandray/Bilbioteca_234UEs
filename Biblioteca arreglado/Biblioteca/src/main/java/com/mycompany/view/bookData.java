/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.view;

import com.mycompany.model.Autor;
import com.mycompany.model.Categoria;
import com.mycompany.model.Libro;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

/**
 * Formulario de alta/edición de un Libro. A diferencia de la versión original,
 * los campos "Autores" y "Categorías" son listas de selección múltiple sobre
 * el catálogo real (no texto libre), y los contadores de ejemplares por estado
 * ya no se muestran como editables aquí: son datos calculados que se gestionan
 * desde el módulo de Ejemplares. En su lugar, al crear un libro nuevo se pide
 * la cantidad inicial de copias a generar.
 *
 * @author betuel
 */
public class bookData extends JPanel {

    // Componentes del formulario (campos propios de la tabla libros)
    public JTextField txtIsbn;
    public JTextField txtTitulo;
    public JTextField txtEditorial;
    public JTextField txtAnio;
    public JTextField txtTipoEdicion;

    // Selección múltiple sobre el catálogo de autores/categorías existentes
    public JList<Autor> listAutores;
    public JList<Categoria> listCategorias;

    // Solo aplica al crear un libro nuevo (oculto al editar)
    public JLabel lblCantidadEjemplares;
    public JTextField txtCantidadEjemplares;

    // Botones
    public JButton btnCancelar;
    public JButton btnGuardar;
    public JButton btnVolver;

    private final JLabel lblTitulo = new JLabel("Registrar nuevo libro");

    public bookData() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 30));

        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        formPanel.setBackground(Color.WHITE);

        Font formFont = new Font("Comic Sans MS", Font.PLAIN, 14);

        // --- Columna izquierda: datos propios del libro ---
        JPanel izquierda = new JPanel(new GridLayout(6, 1, 0, 12));
        izquierda.setBackground(Color.WHITE);

        txtIsbn = new JTextField();
        txtTitulo = new JTextField();
        txtEditorial = new JTextField();
        txtAnio = new JTextField();
        txtTipoEdicion = new JTextField();
        txtCantidadEjemplares = new JTextField("1");
        lblCantidadEjemplares = new JLabel("Cantidad de ejemplares iniciales");

        izquierda.add(crearCampoPanel("ISBN", txtIsbn, formFont));
        izquierda.add(crearCampoPanel("Título", txtTitulo, formFont));
        izquierda.add(crearCampoPanel("Editorial", txtEditorial, formFont));
        izquierda.add(crearCampoPanel("Año de publicación", txtAnio, formFont));
        izquierda.add(crearCampoPanel("Tipo de edición", txtTipoEdicion, formFont));
        izquierda.add(crearCampoPanel(lblCantidadEjemplares.getText(), txtCantidadEjemplares, formFont));

        // --- Columna derecha: relaciones con autores y categorías (selección múltiple) ---
        JPanel derecha = new JPanel(new GridLayout(2, 1, 0, 15));
        derecha.setBackground(Color.WHITE);

        listAutores = new JList<>(new DefaultListModel<>());
        listAutores.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listAutores.setFont(formFont);

        listCategorias = new JList<>(new DefaultListModel<>());
        listCategorias.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listCategorias.setFont(formFont);

        derecha.add(crearListaPanel("Autores (Ctrl/Shift + clic para varios)", listAutores, formFont));
        derecha.add(crearListaPanel("Categorías (Ctrl/Shift + clic para varios)", listCategorias, formFont));

        formPanel.add(izquierda);
        formPanel.add(derecha);

        add(formPanel, BorderLayout.CENTER);

        // --- BOTONES INFERIORES ---
        JPanel panelBotonesContainer = new JPanel(new BorderLayout());
        panelBotonesContainer.setBackground(Color.WHITE);
        panelBotonesContainer.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JPanel panelIzquierdo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelIzquierdo.setBackground(Color.WHITE);

        btnVolver = new JButton("⬅ Volver");
        btnVolver.setFont(formFont);
        btnVolver.setBackground(new Color(200, 200, 200));
        btnVolver.setFocusPainted(false);
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.setVisible(false);
        panelIzquierdo.add(btnVolver);

        JPanel panelDerecho = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelDerecho.setBackground(Color.WHITE);

        btnCancelar = new JButton("Cancelar edición");
        btnCancelar.setFont(formFont);
        btnCancelar.setBackground(new Color(230, 230, 230));
        btnCancelar.setFocusPainted(false);
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnGuardar = new JButton("💾 Guardar cambios");
        btnGuardar.setFont(formFont);
        btnGuardar.setBackground(new Color(42, 157, 143));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        panelDerecho.add(btnCancelar);
        panelDerecho.add(btnGuardar);

        panelBotonesContainer.add(panelIzquierdo, BorderLayout.WEST);
        panelBotonesContainer.add(panelDerecho, BorderLayout.EAST);

        add(panelBotonesContainer, BorderLayout.SOUTH);
    }

    private JPanel crearCampoPanel(String textoLabel, JTextField campoTexto, Font fuente) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setBackground(Color.WHITE);
        JLabel etiqueta = new JLabel(textoLabel);
        etiqueta.setFont(fuente);
        campoTexto.setFont(fuente);
        campoTexto.setPreferredSize(new java.awt.Dimension(campoTexto.getPreferredSize().width, 30));
        panel.add(etiqueta, BorderLayout.NORTH);
        panel.add(campoTexto, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearListaPanel(String textoLabel, JList<?> lista, Font fuente) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setBackground(Color.WHITE);
        JLabel etiqueta = new JLabel(textoLabel);
        etiqueta.setFont(fuente);
        JScrollPane scroll = new JScrollPane(lista);
        panel.add(etiqueta, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    /** Llama a este método desde tu Controlador para mostrar u ocultar el botón Volver. */
    public void setVolverVisible(boolean visible) {
        btnVolver.setVisible(visible);
        revalidate();
        repaint();
    }

    /** Catálogo completo de autores disponibles para elegir. */
    public void setListaAutoresDisponibles(List<Autor> autores) {
        DefaultListModel<Autor> modelo = new DefaultListModel<>();
        for (Autor a : autores) modelo.addElement(a);
        listAutores.setModel(modelo);
    }

    /** Catálogo completo de categorías disponibles para elegir. */
    public void setListaCategoriasDisponibles(List<Categoria> categorias) {
        DefaultListModel<Categoria> modelo = new DefaultListModel<>();
        for (Categoria c : categorias) modelo.addElement(c);
        listCategorias.setModel(modelo);
    }

    /** Marca como seleccionados los autores que ya están asociados al libro (modo edición). */
    public void setAutoresSeleccionados(List<Autor> seleccionados) {
        seleccionarEnLista(listAutores, seleccionados, Autor::getIdAutor);
    }

    /** Marca como seleccionadas las categorías que ya están asociadas al libro (modo edición). */
    public void setCategoriasSeleccionadas(List<Categoria> seleccionadas) {
        seleccionarEnLista(listCategorias, seleccionadas, Categoria::getIdCategoria);
    }

    private <T> void seleccionarEnLista(JList<T> lista, List<T> seleccionados, java.util.function.Function<T, Integer> idExtractor) {
        if (seleccionados == null || seleccionados.isEmpty()) return;
        List<Integer> idsSeleccionados = new ArrayList<>();
        for (T s : seleccionados) idsSeleccionados.add(idExtractor.apply(s));

        DefaultListModel<T> modelo = (DefaultListModel<T>) lista.getModel();
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < modelo.size(); i++) {
            if (idsSeleccionados.contains(idExtractor.apply(modelo.get(i)))) indices.add(i);
        }
        int[] arr = indices.stream().mapToInt(Integer::intValue).toArray();
        lista.setSelectedIndices(arr);
    }

    /** IDs de los autores actualmente seleccionados en la lista. */
    public List<Integer> getIdsAutoresSeleccionados() {
        List<Integer> ids = new ArrayList<>();
        for (Autor a : listAutores.getSelectedValuesList()) ids.add(a.getIdAutor());
        return ids;
    }

    /** IDs de las categorías actualmente seleccionadas en la lista. */
    public List<Integer> getIdsCategoriasSeleccionadas() {
        List<Integer> ids = new ArrayList<>();
        for (Categoria c : listCategorias.getSelectedValuesList()) ids.add(c.getIdCategoria());
        return ids;
    }

    /**
     * Activa el modo "Editar": bloquea el ISBN (es la llave del libro, no debe cambiar)
     * y oculta la cantidad inicial de ejemplares (esos se gestionan en el módulo de Ejemplares).
     */
    public void activarModoEdicion() {
        txtIsbn.setEditable(false);
        lblTitulo.setText("Editar datos del libro");
        lblCantidadEjemplares.setVisible(false);
        txtCantidadEjemplares.setVisible(false);
    }

    public void setLibro(Libro libro) {
        if (libro != null) {
            txtIsbn.setText(libro.getIsbn());
            txtTitulo.setText(libro.getTitulo());
            txtEditorial.setText(libro.getEditorial());
            txtAnio.setText(String.valueOf(libro.getAnio()));
            txtTipoEdicion.setText(libro.getTipoEdicion());
        }
    }

    /** Construye un Libro a partir de los campos de texto. Lanza NumberFormatException si "Año" no es válido. */
    public Libro getLibroFromFields() {
        Libro l = new Libro();
        l.setIsbn(txtIsbn.getText().trim());
        l.setTitulo(txtTitulo.getText().trim());
        l.setEditorial(txtEditorial.getText().trim());
        l.setTipoEdicion(txtTipoEdicion.getText().trim());
        l.setAnio(Integer.parseInt(txtAnio.getText().trim()));
        return l;
    }

    /** Cantidad inicial de ejemplares a crear (solo relevante en modo "Nuevo"). */
    public int getCantidadEjemplares() {
        String txt = txtCantidadEjemplares.getText().trim();
        return txt.isEmpty() ? 0 : Integer.parseInt(txt);
    }
}