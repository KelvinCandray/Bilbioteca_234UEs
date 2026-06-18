/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.util;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author betuel
 */
public class UIFuctions {

    public static void addImageToButton(JButton boton, String image) {
        try {

            String rutaFisica = "src/main/java/com/mycompany/resources/" + image;
            java.io.File archivo = new java.io.File(rutaFisica);

            if (archivo.exists()) {
                boton.setIcon(new javax.swing.ImageIcon(archivo.getAbsolutePath()));
            }

        } catch (Exception e) {
            System.out.println("No se encontro la imagen");
        }
    }

    public static void addImageToLabel(JLabel label, String image) {
        try {

            String rutaFisica = "src/main/java/com/mycompany/resources/" + image;
            java.io.File archivo = new java.io.File(rutaFisica);

            if (archivo.exists()) {
                label.setIcon(new javax.swing.ImageIcon(archivo.getAbsolutePath()));
            }

        } catch (Exception e) {
            System.out.println("No se encontro la imagen para el jLabel");
        }
        label.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 12, 12));
    }

    public static void formatButton(JButton boton, int width, int height) {
        boton.setFont(new java.awt.Font(java.awt.Font.SANS_SERIF, java.awt.Font.BOLD, 12));

        boton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        boton.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

        boton.setMaximumSize(new java.awt.Dimension(
                width,
                height
        ));

        boton.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 12, 4, 4));
    }
    
    public static DefaultTableModel tableBuilder (int numColumns, int numRows){
        DefaultTableModel dtm = new DefaultTableModel(numColumns, numRows);
        return dtm;
    }
}
