package com.mycompany.view;

import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;
import static javax.swing.BoxLayout.Y_AXIS;
import javax.swing.JButton;
import javax.swing.JPanel;
import com.mycompany.util.UIFuctions;

/**
 *
 * @author betuel
 */
public class MenuPanel extends JPanel {

    public JButton boton1;
    public JButton boton2;
    public JButton boton3;
    public JButton boton4;
    public JButton boton5;
    public JButton boton6;
    public JButton boton7;

    public JButton miPerfil;

    public MenuPanel(int userType) {
        initialize(userType);
        
        prepararBotonMenu(miPerfil);
        UIFuctions.addImageToButton(miPerfil, "Perfil.png");
        
        this.add(Box.createVerticalStrut(10));
        this.add(Box.createVerticalGlue());
        this.add(miPerfil);
    }

    private void initialize(int userType) {
    this.setPreferredSize(new Dimension(300, 540));
    this.setLayout(new BoxLayout(this, Y_AXIS));
            
    miPerfil = new JButton("Mi perfil");

    switch (userType) {
        case 0: {
            boton1 = new JButton("Solicitar prestamo");
            boton2 = new JButton("Consultar prestamos");
            boton3 = new JButton("Consultar multas");

            // Aplicamos el formato unificado a los botones del caso 0
            prepararBotonMenu(boton1);
            prepararBotonMenu(boton2);
            prepararBotonMenu(boton3);
            
            UIFuctions.addImageToButton(boton1, "Solicitar p.png");
            UIFuctions.addImageToButton(boton2, "Consultar p.png");
            UIFuctions.addImageToButton(boton3, "Consultar m.png");

            this.add(Box.createVerticalStrut(10));
            this.add(boton1);
            this.add(Box.createVerticalStrut(10));
            this.add(boton2);
            this.add(Box.createVerticalStrut(10));
            this.add(boton3);
            break;
        }
        case 1: {
            boton1 = new JButton("Solicitudes de prestamo");
            boton2 = new JButton("Multas");
            boton3 = new JButton("Prestamos");
            boton4 = new JButton("Clientes");

            // Aplicamos el formato unificado a los botones del caso 1
            prepararBotonMenu(boton1);
            prepararBotonMenu(boton2);
            prepararBotonMenu(boton3);
            prepararBotonMenu(boton4);
            
            UIFuctions.addImageToButton(boton1, "Solicitudes p.png");
            UIFuctions.addImageToButton(boton2, "Consultar m.png");
            UIFuctions.addImageToButton(boton3, "Consultar p.png");
            UIFuctions.addImageToButton(boton4, "Usuarios.png");

            this.add(Box.createVerticalStrut(10));
            this.add(boton1);
            this.add(Box.createVerticalStrut(10));
            this.add(boton2);
            this.add(Box.createVerticalStrut(10));
            this.add(boton3);
            this.add(Box.createVerticalStrut(10));
            this.add(boton4);
            break;
        }
        case 2: {
            
            boton1 = new JButton("📚 Ejemplares registrados");
            boton2 = new JButton("📖 Libros registrados");
            boton3 = new JButton("🔤 Categorias registradas");
            boton4 = new JButton("✍️ Autores registrados");
            boton5 = new JButton("🤝 Prestamos");
            boton6 = new JButton("📜 Multas");
            boton7 = new JButton("👥 Usuarios");

            // Aplicamos el formato unificado a los botones del caso 2
            prepararBotonMenu(boton1);
            prepararBotonMenu(boton2);
            prepararBotonMenu(boton3);
            prepararBotonMenu(boton4);
            prepararBotonMenu(boton5);
            prepararBotonMenu(boton6);
            prepararBotonMenu(boton7);

            this.add(Box.createVerticalStrut(10));
            this.add(boton1);
            this.add(Box.createVerticalStrut(10));
            this.add(boton2);
            this.add(Box.createVerticalStrut(10));
            this.add(boton3);
            this.add(Box.createVerticalStrut(10));
            this.add(boton4);
            this.add(Box.createVerticalStrut(10));
            this.add(boton5);
            this.add(Box.createVerticalStrut(10));
            this.add(boton6);
            this.add(Box.createVerticalStrut(10));
            this.add(boton7);
            break;
        }
        default: {
            break;
        }
    }
}

/**
 * Método auxiliar que replica exactamente el formato visual de los botones.
 */
private void prepararBotonMenu(JButton boton) {
    boton.setFont(new java.awt.Font(java.awt.Font.SANS_SERIF, java.awt.Font.BOLD, 12));
    
    boton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    
    boton.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
    
    boton.setMaximumSize(new java.awt.Dimension(
        280, 
        60
    ));
    
    boton.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 12, 4, 4));
}
}
