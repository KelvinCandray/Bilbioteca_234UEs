/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.view;

import com.mycompany.model.Autor;
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
public class AutorData extends JPanel {

    // Componentes del formulario
    public JTextField txtIdAutor;
    public JTextField txtNombre;
    public JTextField txtApellido;
    public JTextField txtNacionalidad;
    
    public JButton btnCancelar;
    public JButton btnGuardar;
    public JButton btnVolver;

    public AutorData() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // TÍTULO
        JLabel lblTitulo = new JLabel("Editar datos del autor");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        add(lblTitulo, BorderLayout.NORTH);

        // FORMULARIO (GridLayout para 4 campos)
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 40, 20));
        formPanel.setBackground(Color.WHITE);

        txtIdAutor = new JTextField();
        txtIdAutor.setEditable(false); // ID suele ser autoincrementable o no editable
        txtNombre = new JTextField();
        txtApellido = new JTextField();
        txtNacionalidad = new JTextField();

        Font formFont = new Font("Comic Sans MS", Font.PLAIN, 14);

        formPanel.add(crearCampoPanel("ID Autor", txtIdAutor, formFont));
        formPanel.add(crearCampoPanel("Nombre", txtNombre, formFont));
        formPanel.add(crearCampoPanel("Apellido", txtApellido, formFont));
        formPanel.add(crearCampoPanel("Nacionalidad", txtNacionalidad, formFont));

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
    public void setAutor(Autor autor) {
        if (autor != null) {
            txtIdAutor.setText(String.valueOf(autor.getIdAutor()));
            txtNombre.setText(autor.getNombre());
            txtApellido.setText(autor.getApellido());
            txtNacionalidad.setText(autor.getNacionalidad());
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