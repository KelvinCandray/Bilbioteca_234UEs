/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.view;

import com.mycompany.model.Prestamo;
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
public class PrestamoData extends JPanel {

    // Componentes del formulario
    public JTextField txtIdPrestamo, txtIdUsuario, txtIdEmpleado, txtIdEjemplar;
    public JTextField txtIsbn, txtFechaRetiro, txtFechaIdeal, txtFechaReal;
    public JTextField txtEstado, txtUsuario, txtTituloLibro;
    
    public JButton btnCancelar, btnGuardar, btnVolver;

    public PrestamoData() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel lblTitulo = new JLabel("Editar datos de préstamo");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        add(lblTitulo, BorderLayout.NORTH);

        // Grid 6x2 para acomodar los 11 campos y uno vacío
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 40, 15));
        formPanel.setBackground(Color.WHITE);

        txtIdPrestamo = new JTextField(); txtIdPrestamo.setEditable(false);
        txtIdUsuario = new JTextField();
        txtIdEmpleado = new JTextField();
        txtIdEjemplar = new JTextField();
        txtIsbn = new JTextField();
        txtFechaRetiro = new JTextField();
        txtFechaIdeal = new JTextField();
        txtFechaReal = new JTextField();
        txtEstado = new JTextField();
        txtUsuario = new JTextField();
        txtTituloLibro = new JTextField();

        Font formFont = new Font("Comic Sans MS", Font.PLAIN, 14);

        formPanel.add(crearCampoPanel("ID Préstamo", txtIdPrestamo, formFont));
        formPanel.add(crearCampoPanel("ID Usuario", txtIdUsuario, formFont));
        formPanel.add(crearCampoPanel("ID Empleado", txtIdEmpleado, formFont));
        formPanel.add(crearCampoPanel("ID Ejemplar", txtIdEjemplar, formFont));
        formPanel.add(crearCampoPanel("ISBN", txtIsbn, formFont));
        formPanel.add(crearCampoPanel("Fecha Retiro", txtFechaRetiro, formFont));
        formPanel.add(crearCampoPanel("Fecha Límite", txtFechaIdeal, formFont));
        formPanel.add(crearCampoPanel("Fecha Real", txtFechaReal, formFont));
        formPanel.add(crearCampoPanel("Estado", txtEstado, formFont));
        formPanel.add(crearCampoPanel("Nombre Usuario", txtUsuario, formFont));
        formPanel.add(crearCampoPanel("Título Libro", txtTituloLibro, formFont));
        
        // Panel de relleno
        JPanel emptyPanel = new JPanel(); emptyPanel.setBackground(Color.WHITE);
        formPanel.add(emptyPanel);

        add(formPanel, BorderLayout.CENTER);

        // BOTONES
        JPanel panelBotonesContainer = new JPanel(new BorderLayout());
        panelBotonesContainer.setBackground(Color.WHITE);
        panelBotonesContainer.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        btnVolver = new JButton("⬅ Volver");
        btnVolver.setFont(formFont);
        btnVolver.setBackground(new Color(200, 200, 200));
        btnVolver.setVisible(false); 
        panelBotonesContainer.add(btnVolver, BorderLayout.WEST);

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

    public void setPrestamo(Prestamo p) {
        if (p != null) {
            txtIdPrestamo.setText(String.valueOf(p.getIdPrestamo()));
            txtIdUsuario.setText(String.valueOf(p.getIdUsuario()));
            txtIdEmpleado.setText(String.valueOf(p.getIdEmpleado()));
            txtIdEjemplar.setText(String.valueOf(p.getIdEjemplar()));
            txtIsbn.setText(p.getIsbn());
            txtFechaRetiro.setText(p.getFechaRetiro());
            txtFechaIdeal.setText(p.getFechaIdealRegreso());
            txtFechaReal.setText(p.getFechaRealRegreso());
            txtEstado.setText(p.getEstado());
            txtUsuario.setText(p.getNombreUsuario());
            txtTituloLibro.setText(p.getTituloLibro());
        }
    }

    public void setVolverVisible(boolean visible) {
        btnVolver.setVisible(visible);
        revalidate();
        repaint();
    }

    /**
     * Bloquea todos los campos y oculta "Guardar cambios": el historial de
     * préstamos es de solo consulta desde el panel de administración, ya que
     * mutar un préstamo a mano (sin pasar por aprobarPrestamo/registrarDevolucion)
     * rompería la sincronía con el estado del ejemplar y las multas asociadas.
     */
    public void activarSoloLectura() {
        for (JTextField campo : new JTextField[]{
                txtIdPrestamo, txtIdUsuario, txtIdEmpleado, txtIdEjemplar,
                txtIsbn, txtFechaRetiro, txtFechaIdeal, txtFechaReal,
                txtEstado, txtUsuario, txtTituloLibro}) {
            campo.setEditable(false);
        }
        btnGuardar.setVisible(false);
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