package com.mycompany.view;

import java.awt.BorderLayout;
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
    JTable table;
    JButton btnReadCreate;
    JButton btnUpdate;
    JButton btnDelete;
    JButton btnSearch;
    JRadioButton radioCritery1;
    JRadioButton radioCritery2;
    ButtonGroup radios; 
    JTextField txtBusqueda;
    JLabel lblTableName;

    public DataViewer(List<?> data, String tableName, String radio1Text, String radio2Text, String create, String update, String delete) {
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(new EmptyBorder(15, 15, 15, 15)); // Margen externo del contenedor principal
        
        // Parte de arriba
        lblTableName = new JLabel(tableName);
        lblTableName.setFont(new Font("Arial", Font.BOLD, 18));
        
        txtBusqueda = new JTextField(20);
        // Hint / Placeholder opcional
        txtBusqueda.setToolTipText("Escribe el criterio de búsqueda"); 
        
        radioCritery1 = new JRadioButton(radio1Text, true); 
        radioCritery2 = new JRadioButton(radio2Text);
        
        radios = new ButtonGroup();
        radios.add(radioCritery1);
        radios.add(radioCritery2);
        
        btnSearch = new JButton("🔍 Buscar");
        
        // Superior
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        
        searchPanel.add(txtBusqueda);
        searchPanel.add(radioCritery1);
        searchPanel.add(radioCritery2);
        searchPanel.add(btnSearch);
        
        topPanel.add(lblTableName, BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.EAST);
        
        // Medio
        dtm = new DefaultTableModel();

        if (data != null && !data.isEmpty()) {
            // Asumimos que el primer elemento (índice 0) son los nombres de las columnas
            List<?> columnas = (List<?>) data.get(0);
            for (Object col : columnas) {
                dtm.addColumn(col.toString());
            }

            // El resto de los elementos (índice 1 en adelante) son las filas de datos
            for (int i = 1; i < data.size(); i++) {
                List<?> filaData = (List<?>) data.get(i);
                Object[] fila = filaData.toArray();
                dtm.addRow(fila);
            }
        }

        table = new JTable(dtm);
        JScrollPane scrollPane = new JScrollPane(table);

        btnDelete = new JButton(delete);   
        btnUpdate = new JButton(update);   
        btnReadCreate = new JButton(create); 
        
        // Inferior
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        bottomPanel.add(btnDelete);
        bottomPanel.add(btnUpdate);
        bottomPanel.add(btnReadCreate);
        
        //Asignsr psneles
        this.add(topPanel, BorderLayout.NORTH);
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(bottomPanel, BorderLayout.SOUTH);
    }
}