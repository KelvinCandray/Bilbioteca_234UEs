/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.view;

import com.mycompany.model.Categoria;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author betuel
 */
public class CategoriaData extends JPanel {

    // Componentes del formulario
    public JTextField txtIdCategoria;
    public JTextField txtNombre;
    public JTextField txtEdadMinima;
    
    public JButton btnCancelar;
    public JButton btnGuardar;
    public JButton btnVolver;

    public CategoriaData() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // TÍTULO
        JLabel lblTitulo = new JLabel("Editar datos de categoría");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        add(lblTitulo, BorderLayout.NORTH);

        // FORMULARIO (GridLayout para 3 campos)
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 40, 20));
        formPanel.setBackground(Color.WHITE);

        txtIdCategoria = new JTextField();
        txtIdCategoria.setEditable(false);
        txtNombre = new JTextField();
        txtEdadMinima = new JTextField();

        Font formFont = new Font("Comic Sans MS", Font.PLAIN, 14);

        formPanel.add(crearCampoPanel("ID Categoría", txtIdCategoria, formFont));
        formPanel.add(crearCampoPanel("Nombre Categoría", txtNombre, formFont));
        formPanel.add(crearCampoPanel("Edad Mínima", txtEdadMinima, formFont));

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
    public void setCategoria(Categoria categoria) {
        if (categoria != null) {
            txtIdCategoria.setText(String.valueOf(categoria.getIdCategoria()));
            txtNombre.setText(categoria.getNombreCategoria());
            txtEdadMinima.setText(String.valueOf(categoria.getEdadMinima()));
        }
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
}