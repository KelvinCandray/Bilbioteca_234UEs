/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.view;

import com.mycompany.model.Usuario;
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
public class userData extends JPanel {

    // Componentes del formulario
    public JTextField txtNombre;
    public JTextField txtApellidos;
    public JTextField txtTelefono;
    public JTextField txtCorreo;
    public JTextField txtFechaNacimiento;
    public JTextField txtDireccion;
    
    public JButton btnCancelar;
    public JButton btnGuardar;

    public userData(Usuario user) {
        initComponents(user);
    }

    private void initComponents(Usuario user) {
        // Configuración del panel principal
        setLayout(new BorderLayout(20, 20));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // --- TÍTULO ---
        JLabel lblTitulo = new JLabel("Editar datos personales");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        add(lblTitulo, BorderLayout.NORTH);

        // --- FORMULARIO CENTRAL ---
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 40, 20));
        formPanel.setBackground(Color.WHITE);

        // Inicializar campos de texto con datos de ejemplo de la imagen
        txtNombre = new JTextField(user.getPrimerNombre());
        txtApellidos = new JTextField(user.getApellido());
        txtTelefono = new JTextField(user.getTelefono());
        txtCorreo = new JTextField(user.getCorreo());
        txtFechaNacimiento = new JTextField(user.getFechaNacimiento());
        txtDireccion = new JTextField(user.getDepartamento() + ", " + user.getMunicipio() +  ", " + user.getColonia());

        // Simular el borde rojo de error en el correo
        txtCorreo.setBorder(BorderFactory.createLineBorder(new Color(255, 100, 100), 1));

        // Fuente para que se parezca a la de la captura
        Font formFont = new Font("Comic Sans MS", Font.PLAIN, 14);

        // Agregar los campos al grid
        formPanel.add(crearCampoPanel("Nombre", txtNombre, formFont));
        formPanel.add(crearCampoPanel("Apellidos", txtApellidos, formFont));
        formPanel.add(crearCampoPanel("Numero telefonico", txtTelefono, formFont));
        formPanel.add(crearCampoPanel("Correo electrónico", txtCorreo, formFont));
        formPanel.add(crearCampoPanel("Fecha de nacimiento", txtFechaNacimiento, formFont));
        formPanel.add(crearCampoPanel("Dirección", txtDireccion, formFont));

        add(formPanel, BorderLayout.CENTER);

        // --- BOTONES INFERIORES ---
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelBotones.setBackground(Color.WHITE);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        btnCancelar = new JButton("Cancelar edición");
        btnCancelar.setFont(formFont);
        btnCancelar.setBackground(new Color(230, 230, 230)); // Gris claro
        btnCancelar.setFocusPainted(false);
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnGuardar = new JButton("💾 Guardar cambios");
        btnGuardar.setFont(formFont);
        btnGuardar.setBackground(new Color(42, 157, 143)); // Verde azulado
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);

        add(panelBotones, BorderLayout.SOUTH);
    }

    /**
     * Método auxiliar para crear el contenedor de etiqueta + campo de texto
     */
    private JPanel crearCampoPanel(String textoLabel, JTextField campoTexto, Font fuente) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setBackground(Color.WHITE);
        
        JLabel etiqueta = new JLabel(textoLabel);
        etiqueta.setFont(fuente);
        campoTexto.setFont(fuente);
        
        // Ajustar el alto del campo de texto
        campoTexto.setPreferredSize(new java.awt.Dimension(campoTexto.getPreferredSize().width, 30));
        
        panel.add(etiqueta, BorderLayout.NORTH);
        panel.add(campoTexto, BorderLayout.CENTER);
        
        return panel;
    }
}