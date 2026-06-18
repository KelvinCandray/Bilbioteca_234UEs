package com.mycompany.view;

import com.mycompany.util.UIFuctions;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.BoxLayout;
import static javax.swing.BoxLayout.X_AXIS;
import static javax.swing.BoxLayout.Y_AXIS;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 *
 * @author betuel
 */
public class MainInterface extends JFrame {

    //Elementos
    JPanel jPanelUp;
    MenuPanel jPanelLeft;
    JPanel jPanelPrincipal;
    JButton logOut;
    JLabel welcome;
    JLabel logoSmall;
    JLabel logoLarge;
    String username;

    int userType;

    public MainInterface(String username, int userType) {
        super("Tarea EX-AULA APE115");
        //logoPequeño = new ImageIcon(getClass().getResource("com/mycompany/resources/logoSmall.png"));
        setSize(960, 540);
        setMinimumSize(new Dimension(960, 540));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        this.userType = userType;

        this.username = username;

        this.setLayout(new BorderLayout());

        Inicializar();

    }

    private void Inicializar() {
        //Inicialización
        jPanelUp = new javax.swing.JPanel();
        jPanelLeft = new MenuPanel(userType);
        jPanelPrincipal = new javax.swing.JPanel();

        logoSmall = new JLabel("");
        UIFuctions.addImageToLabel(logoSmall, "logoSmall.png");

        //Boon cerrar sesión
        logOut = new JButton("Cerrar sesión");
        UIFuctions.addImageToButton(logOut, "Salir.png");

        changeColors(false);

        this.add(jPanelUp, BorderLayout.NORTH);
        this.add(jPanelLeft, BorderLayout.WEST);
        this.add(jPanelPrincipal, BorderLayout.CENTER);

        jPanelUp.setPreferredSize(new Dimension(960, 100));
        jPanelUp.setLayout(new BoxLayout(jPanelUp, X_AXIS));

        initializePerUser();

        logoSmall.setAlignmentY(Component.CENTER_ALIGNMENT);
        welcome.setAlignmentY(Component.CENTER_ALIGNMENT);

        JPanel logOutContainer = new JPanel(new java.awt.GridBagLayout());
        logOutContainer.setOpaque(false);
        logOutContainer.setAlignmentY(Component.CENTER_ALIGNMENT);
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.anchor = java.awt.GridBagConstraints.EAST; 
        gbc.weightx = 1.0;

        logOutContainer.add(logOut, gbc);

        jPanelUp.add(logoSmall);
        jPanelUp.add(javax.swing.Box.createHorizontalStrut(15));
        jPanelUp.add(welcome);
        jPanelUp.add(javax.swing.Box.createHorizontalGlue());
        jPanelUp.add(logOutContainer);
        jPanelUp.add(javax.swing.Box.createHorizontalStrut(15));

        welcome.setFont(new java.awt.Font(java.awt.Font.SANS_SERIF, java.awt.Font.BOLD, 18));
    }

    //Diseño de interfaz ( colores )
    public void changeColors(boolean darkMode) {
        if (darkMode) {

        } else {
            this.jPanelUp.setBackground(new Color(235, 235, 235));
            this.jPanelLeft.setBackground(new Color(235, 235, 235));
            this.jPanelPrincipal.setBackground(new Color(255, 255, 255));
        }

    }

    private void initializePerUser() {

        switch (userType) {
            case 0: {
                welcome = new JLabel("Bienveido " + username + " nos alegra saludarte nuevamente");
                break;
            }
            case 1: {
                welcome = new JLabel("Bibioteca Alfredo Espino, bienvenido " + username);
                break;
            }
            case 2: {
                welcome = new JLabel("Sistema de administración El Espino DB");
                break;
            }

            default: {

                break;

            }

        }

    }
}
