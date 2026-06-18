/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.view;

import com.mycompany.model.Ejemplar;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Formulario para editar el estado de un ejemplar puntual. El ISBN y el ID
 * son de solo lectura (un ejemplar no se reasigna a otro libro desde aquí);
 * el estado se elige de una lista cerrada que coincide con la restricción
 * CHECK de la base de datos, evitando valores inválidos.
 *
 * @author betuel
 */
public class EjemplarData extends JPanel {

    // "Prestado" se excluye intencionalmente: ese estado solo lo asigna el sistema
    // al aprobar un préstamo (PrestamoDAO.aprobarPrestamo), nunca a mano desde aquí.
    private static final String[] ESTADOS_VALIDOS = {"Disponible", "Dañado", "Perdido"};

    // Componentes del formulario
    public JTextField txtIdEjemplar;
    public JTextField txtIsbn;
    public JComboBox<String> cboEstado;

    public JButton btnCancelar;
    public JButton btnGuardar;
    public JButton btnVolver;

    public EjemplarData() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // TÍTULO
        JLabel lblTitulo = new JLabel("Editar estado de ejemplar");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        add(lblTitulo, BorderLayout.NORTH);

        // FORMULARIO (GridLayout para 3 campos)
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 40, 20));
        formPanel.setBackground(Color.WHITE);

        txtIdEjemplar = new JTextField();
        txtIdEjemplar.setEditable(false);
        txtIsbn = new JTextField();
        txtIsbn.setEditable(false);
        cboEstado = new JComboBox<>(ESTADOS_VALIDOS);

        Font formFont = new Font("Comic Sans MS", Font.PLAIN, 14);

        formPanel.add(crearCampoPanel("ID Ejemplar", txtIdEjemplar, formFont));
        formPanel.add(crearCampoPanel("ISBN Libro", txtIsbn, formFont));
        formPanel.add(crearComboPanel("Estado", cboEstado, formFont));

        add(formPanel, BorderLayout.CENTER);

        // BOTONES
        JPanel panelBotonesContainer = new JPanel(new BorderLayout());
        panelBotonesContainer.setBackground(Color.WHITE);
        panelBotonesContainer.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Botón Volver
        btnVolver = new JButton("⬅ Volver");
        btnVolver.setFont(formFont);
        btnVolver.setBackground(new Color(200, 200, 200));
        btnVolver.setVisible(false); 
        panelBotonesContainer.add(btnVolver, BorderLayout.WEST);

        // Botones Guardar/Cancelar
        JPanel panelDerecho = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelDerecho.setBackground(Color.WHITE);
        btnCancelar = new JButton("Cancelar");
        btnGuardar = new JButton("💾 Guardar cambios");
        btnGuardar.setBackground(new Color(42, 157, 143));
        btnGuardar.setForeground(Color.WHITE);
        
        panelDerecho.add(btnCancelar);
        panelDerecho.add(btnGuardar);
        panelBotonesContainer.add(panelDerecho, BorderLayout.EAST);

        add(panelBotonesContainer, BorderLayout.SOUTH);
    }

    // MÉTODOS DE CONTROL
    public void setEjemplar(Ejemplar ejemplar) {
        if (ejemplar != null) {
            txtIdEjemplar.setText(String.valueOf(ejemplar.getIdEjemplar()));
            txtIsbn.setText(ejemplar.getIsbn());
            cboEstado.setSelectedItem(ejemplar.getEstado());
        }
    }

    /** Construye un Ejemplar con el ID original y el estado elegido en el combo. */
    public Ejemplar getEjemplarFromFields() {
        Ejemplar e = new Ejemplar();
        e.setIdEjemplar(Integer.parseInt(txtIdEjemplar.getText().trim()));
        e.setIsbn(txtIsbn.getText().trim());
        e.setEstado((String) cboEstado.getSelectedItem());
        return e;
    }

    public void setVolverVisible(boolean visible) {
        btnVolver.setVisible(visible);
        revalidate();
        repaint();
    }

    private JPanel crearCampoPanel(String textoLabel, JTextField campoTexto, Font fuente) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setBackground(Color.WHITE);
        JLabel etiqueta = new JLabel(textoLabel);
        etiqueta.setFont(fuente);
        campoTexto.setFont(fuente);
        campoTexto.setPreferredSize(new java.awt.Dimension(200, 30));
        panel.add(etiqueta, BorderLayout.NORTH);
        panel.add(campoTexto, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearComboPanel(String textoLabel, JComboBox<String> combo, Font fuente) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setBackground(Color.WHITE);
        JLabel etiqueta = new JLabel(textoLabel);
        etiqueta.setFont(fuente);
        combo.setFont(fuente);
        combo.setPreferredSize(new java.awt.Dimension(200, 30));
        panel.add(etiqueta, BorderLayout.NORTH);
        panel.add(combo, BorderLayout.CENTER);
        return panel;
    }
}