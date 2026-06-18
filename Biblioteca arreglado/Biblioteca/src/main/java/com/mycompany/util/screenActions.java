/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.util;

import com.mycompany.model.Usuario;
import com.mycompany.view.MainInterface;
import com.mycompany.view.userData;
import javax.swing.JPanel;

/**
 *
 * @author betuel
 */
public class screenActions {
    public static void showUserData(MainInterface view, Usuario user){
        JPanel panel = new userData(user);
        
        view.remove(view.jPanelPrincipal);
        view.jPanelPrincipal = panel;
        view.add(view.jPanelPrincipal, java.awt.BorderLayout.CENTER);

        view.revalidate();
        view.repaint();
    }
}
