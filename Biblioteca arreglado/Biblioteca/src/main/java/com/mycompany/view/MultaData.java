/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.view;

import com.mycompany.model.Multa;
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
public class MultaData extends JPanel {

    // Componentes del formulario
    public JTextField txtIdMulta, txtIdPrestamo, txtIdUsuario;
    public JTextField txtTipoMulta, txtMonto, txtEstado;
    public JTextField txtFechaGeneracion, txtFechaMaxima, txtFechaRealPago, txtPagoATiempo;
    
    public JButton btnCancelar, btnGuardar, btnVolver;

    public MultaData() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel lblTitulo = new JLabel("Detalle / modificar multa");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        add(lblTitulo, BorderLayout.NORTH);

        // Grid de 5x2 para los 10 campos
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 40, 15));
        formPanel.setBackground(Color.WHITE);

        txtIdMulta = new JTextField();
        txtIdPrestamo = new JTextField();
        txtIdUsuario = new JTextField();
        txtTipoMulta = new JTextField();
        txtMonto = new JTextField();
        txtEstado = new JTextField();
        txtFechaGeneracion = new JTextField();
        txtFechaMaxima = new JTextField();
        txtFechaRealPago = new JTextField();
        txtPagoATiempo = new JTextField();

        // Solo "Monto" y "Fecha máxima de pago" son ajustables manualmente; el resto son
        // hechos históricos o calculados que solo deben cambiar a través de los flujos
        // dedicados (registrarPago, exonerarMulta, la auditoría automática).
        txtIdMulta.setEditable(false);
        txtIdPrestamo.setEditable(false);
        txtIdUsuario.setEditable(false);
        txtTipoMulta.setEditable(false);
        txtEstado.setEditable(false);
        txtFechaGeneracion.setEditable(false);
        txtFechaRealPago.setEditable(false);
        txtPagoATiempo.setEditable(false);

        Font formFont = new Font("Comic Sans MS", Font.PLAIN, 14);

        formPanel.add(crearCampoPanel("ID Multa", txtIdMulta, formFont));
        formPanel.add(crearCampoPanel("ID Préstamo", txtIdPrestamo, formFont));
        formPanel.add(crearCampoPanel("ID Usuario", txtIdUsuario, formFont));
        formPanel.add(crearCampoPanel("Tipo Multa", txtTipoMulta, formFont));
        formPanel.add(crearCampoPanel("Monto ($)", txtMonto, formFont));
        formPanel.add(crearCampoPanel("Estado", txtEstado, formFont));
        formPanel.add(crearCampoPanel("Fecha Generación", txtFechaGeneracion, formFont));
        formPanel.add(crearCampoPanel("Fecha Máxima Pago", txtFechaMaxima, formFont));
        formPanel.add(crearCampoPanel("Fecha Real Pago", txtFechaRealPago, formFont));
        formPanel.add(crearCampoPanel("Pago a Tiempo", txtPagoATiempo, formFont));

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

    public void setMulta(Multa m) {
        if (m != null) {
            txtIdMulta.setText(String.valueOf(m.getIdMulta()));
            txtIdPrestamo.setText(String.valueOf(m.getIdPrestamo()));
            txtIdUsuario.setText(String.valueOf(m.getIdUsuario()));
            txtTipoMulta.setText(m.getTipoMulta());
            txtMonto.setText(String.valueOf(m.getMonto()));
            txtEstado.setText(m.getEstado());
            txtFechaGeneracion.setText(m.getFechaGeneracion());
            txtFechaMaxima.setText(m.getFechaMaximaPagar());
            txtFechaRealPago.setText(m.getFechaRealPago());
            txtPagoATiempo.setText(m.getPagoATiempo());
        }
    }

    /** Nuevo monto introducido por el administrador. */
    public double getMontoEditado() {
        return Double.parseDouble(txtMonto.getText().trim());
    }

    /** Nueva fecha máxima de pago introducida por el administrador (formato AAAA-MM-DD). */
    public String getFechaMaximaEditada() {
        return txtFechaMaxima.getText().trim();
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