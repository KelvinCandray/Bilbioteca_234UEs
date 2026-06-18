package com.mycompany.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author betuel
 */
public class DataViewer extends JPanel {
    DefaultTableModel dtm;
    public JTable table;
    public JButton btnReadCreate;
    public JButton btnUpdate;
    public JButton btnDelete;
    public JButton btnSearch;
    public JRadioButton radioCritery1;
    public JRadioButton radioCritery2;
    public ButtonGroup radios; 
    public JTextField txtBusqueda;
    public JLabel lblTableName;

    /** Copia de las filas originales (sin el encabezado), para poder filtrar y luego restaurar. */
    private final java.util.List<Object[]> filasOriginales = new java.util.ArrayList<>();

    public DataViewer(List<?> data, String tableName, String radio1Text, String radio2Text, String create, String update, String delete) {
        this.setBackground(Color.WHITE);
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(new EmptyBorder(15, 15, 15, 15)); 
        
        lblTableName = new JLabel(tableName);
        lblTableName.setFont(new Font("Arial", Font.BOLD, 12));
        
        txtBusqueda = new JTextField(20);
        txtBusqueda.setToolTipText("Escribe el criterio de búsqueda"); 
        
        radioCritery1 = new JRadioButton(radio1Text, true); 
        radioCritery2 = new JRadioButton(radio2Text);
        
        radios = new ButtonGroup();
        radios.add(radioCritery1);
        radios.add(radioCritery2);
        
        btnSearch = new JButton("🔍 Buscar");
        
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        
        searchPanel.add(txtBusqueda);
        searchPanel.add(radioCritery1);
        searchPanel.add(radioCritery2);
        searchPanel.add(btnSearch);
        
        topPanel.add(lblTableName, BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.EAST);
        
        configurarRadioBoton(radioCritery1, radio1Text);
        configurarRadioBoton(radioCritery2, radio2Text);
        
        if (!radioCritery1.isVisible() && !radioCritery2.isVisible()) {
            txtBusqueda.setVisible(false);
            btnSearch.setVisible(false);
            searchPanel.setVisible(false);
        }
        
        dtm = new DefaultTableModel();

        if (data != null && !data.isEmpty()) {
            List<?> columnas = (List<?>) data.get(0);
            for (Object col : columnas) {
                dtm.addColumn(col.toString());
            }

            for (int i = 1; i < data.size(); i++) {
                List<?> filaData = (List<?>) data.get(i);
                Object[] fila = filaData.toArray();
                dtm.addRow(fila);
                filasOriginales.add(fila);
            }
        }

        table = new JTable(dtm);
        JScrollPane scrollPane = new JScrollPane(table);

        btnDelete = new JButton(delete);   
        btnUpdate = new JButton(update);   
        btnReadCreate = new JButton(create); 
        
        configurarBoton(btnDelete, delete);
        configurarBoton(btnUpdate, update);
        configurarBoton(btnReadCreate, create);
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(btnDelete);
        bottomPanel.add(btnUpdate);
        bottomPanel.add(btnReadCreate);
        
        this.add(topPanel, BorderLayout.NORTH);
        topPanel.setBackground(Color.WHITE);
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(bottomPanel, BorderLayout.SOUTH);

        btnSearch.addActionListener(e -> aplicarFiltro());
        txtBusqueda.addActionListener(e -> aplicarFiltro()); // permite buscar presionando Enter
    }

    /** Filtra las filas según el texto ingresado y el criterio (radio) seleccionado. Vacío = mostrar todo. */
    private void aplicarFiltro() {
        String criterio = txtBusqueda.getText().trim();
        if (criterio.isEmpty()) {
            mostrarFilas(filasOriginales);
            return;
        }
        String criterioLower = criterio.toLowerCase();
        int columna = resolverColumnaDeBusqueda();

        java.util.List<Object[]> coincidencias = new java.util.ArrayList<>();
        for (Object[] fila : filasOriginales) {
            boolean coincide;
            if (columna >= 0 && columna < fila.length) {
                coincide = fila[columna] != null && fila[columna].toString().toLowerCase().contains(criterioLower);
            } else {
                // No se pudo determinar con certeza a qué columna corresponde el criterio
                // elegido: como red de seguridad, se busca en toda la fila en vez de no filtrar nada.
                coincide = false;
                for (Object valor : fila) {
                    if (valor != null && valor.toString().toLowerCase().contains(criterioLower)) {
                        coincide = true;
                        break;
                    }
                }
            }
            if (coincide) coincidencias.add(fila);
        }
        mostrarFilas(coincidencias);
    }

    /**
     * Determina a qué columna de la tabla corresponde el radio button seleccionado.
     * El texto del radio no siempre coincide al pie de la letra con el encabezado real
     * de la columna (ej. "ID_Cliente" vs "ID Cliente / Socio"), así que primero se intenta
     * una coincidencia exacta y, si falla, una coincidencia parcial normalizando guiones
     * bajos y barras como espacios. Si ninguna calza, devuelve -1 (buscar en toda la fila).
     */
    private int resolverColumnaDeBusqueda() {
        JRadioButton seleccionado = radioCritery2.isSelected() ? radioCritery2 : radioCritery1;
        if (!seleccionado.isVisible() || seleccionado.getText() == null || seleccionado.getText().isBlank()) {
            return -1;
        }
        String etiqueta = seleccionado.getText().trim().toLowerCase();
        int columnas = dtm.getColumnCount();

        for (int i = 0; i < columnas; i++) {
            if (dtm.getColumnName(i).trim().equalsIgnoreCase(etiqueta)) return i;
        }
        String etiquetaNormalizada = etiqueta.replace("_", " ");
        for (int i = 0; i < columnas; i++) {
            String encabezado = dtm.getColumnName(i).toLowerCase().replace("_", " ").replace("/", " ");
            if (encabezado.contains(etiquetaNormalizada) || etiquetaNormalizada.contains(encabezado)) return i;
        }
        return -1;
    }

    private void mostrarFilas(java.util.List<Object[]> filas) {
        dtm.setRowCount(0);
        for (Object[] fila : filas) dtm.addRow(fila);
    }

    private void configurarBoton(JButton boton, String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            boton.setVisible(false);
        } else {
            boton.setText(texto);
            boton.setVisible(true);
        }
    }

    private void configurarRadioBoton(JRadioButton radio, String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            radio.setVisible(false);
        } else {
            radio.setText(texto);
            radio.setVisible(true);
        }
    }
}