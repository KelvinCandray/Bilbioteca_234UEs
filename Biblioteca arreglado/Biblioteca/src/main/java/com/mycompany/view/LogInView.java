package com.mycompany.view;

import com.mycompany.util.UIFuctions;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import javax.swing.border.Border;

/**
 *
 * @author betuel
 */
public class LogInView extends JFrame {
    JPanel supPanel;
    JPanel midPanel;
    JPanel subPanel;
    public JLabel lblCorreoError;
    public JLabel lblContraseñaError;
    JLabel imgLogo;
    JLabel lblLogIn;
    JLabel lblCorreo;
    JLabel lblContraseña;
    public JTextField txtCorreo;
    public JPasswordField txtContraseña;
    public JButton btnlogIn;
    public JButton btnsingUp;

    public LogInView() {
        setTitle("Alfredo Espino");
        setSize(480, 560);
        setMinimumSize(new Dimension(480, 560));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());
        
        supPanel = new JPanel();
        supPanel.setBackground(Color.WHITE);
        supPanel.setLayout(new BoxLayout(supPanel, BoxLayout.Y_AXIS));
        supPanel.setBorder(new EmptyBorder(20,20,0,20));//top - left - button - right
        
        midPanel = new JPanel();
        midPanel.setBackground(Color.WHITE);
        midPanel.setLayout(new BoxLayout(midPanel, BoxLayout.Y_AXIS));
        midPanel.setBorder(new EmptyBorder(0,20,0,20));
        
        subPanel = new JPanel();
        subPanel.setBackground(Color.WHITE);
        subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.Y_AXIS));
        subPanel.setBorder(new EmptyBorder(0,20,40,20));        
                
        imgLogo = new JLabel();
        lblLogIn = new JLabel("Inicio de sesión");
        lblContraseña = new JLabel("Contraseña");
        lblCorreo = new JLabel("Usuario");
        lblContraseñaError = new JLabel("");
        lblCorreoError = new JLabel("");
        
        lblContraseñaError.setForeground(Color.red);
        lblCorreoError.setForeground(Color.red);
        
        txtCorreo = new JTextField();
        txtContraseña = new JPasswordField();
        
        btnlogIn = new JButton("Iniciar sesión");
        btnsingUp = new JButton("Registrarse");
        
        inicializar();
        
        supPanel.add(imgLogo);
        supPanel.add(Box.createVerticalStrut(20)); 
        
        supPanel.add(lblLogIn);
        supPanel.add(Box.createVerticalStrut(20)); 
        
        midPanel.add(lblCorreo);
        midPanel.add(Box.createVerticalStrut(5));  
        midPanel.add(txtCorreo);
        midPanel.add(Box.createVerticalStrut(5)); 
        midPanel.add(lblCorreoError);
        midPanel.add(Box.createVerticalStrut(5)); 
        
        midPanel.add(lblContraseña);
        midPanel.add(Box.createVerticalStrut(5));
        midPanel.add(txtContraseña);
        midPanel.add(Box.createVerticalStrut(5)); 
        midPanel.add(lblContraseñaError);
        midPanel.add(Box.createVerticalStrut(5)); 
        
        subPanel.add(btnlogIn);
        subPanel.add(Box.createVerticalStrut(10)); 
        subPanel.add(btnsingUp);
        
        this.add(supPanel, BorderLayout.NORTH);
        this.add(midPanel, BorderLayout.CENTER);
        this.add(subPanel, BorderLayout.SOUTH);
        this.setVisible(true);
    }
    
    private void inicializar () {
        UIFuctions.addImageToLabel(imgLogo, "logoMiddle.png");
        
        lblLogIn.setFont(new Font("Serif", Font.PLAIN, 24));
        lblCorreo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblContraseña.setFont(new Font("SansSerif", Font.PLAIN, 13));
        
        Dimension campoDimension = new Dimension(Integer.MAX_VALUE, 35);
        txtCorreo.setMaximumSize(campoDimension);
        txtContraseña.setMaximumSize(campoDimension);
        
        Dimension botonDimension = new Dimension(130, 30);
        btnlogIn.setMaximumSize(botonDimension);
        btnsingUp.setMaximumSize(botonDimension);
        
        imgLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblLogIn.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        lblCorreo.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtCorreo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblContraseña.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtContraseña.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        btnlogIn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnsingUp.setAlignmentX(Component.CENTER_ALIGNMENT);
    }
    
    public void showErrorAlert(String text, int type){
        if (type==0){ //Correo electronico
            lblCorreoError.setText(text);
        } else {
            lblContraseñaError.setText(text);
        }
    }
}