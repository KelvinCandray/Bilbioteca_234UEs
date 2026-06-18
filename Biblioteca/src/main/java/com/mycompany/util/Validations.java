/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author betuel
 */
public class Validations {

    public static String correoValidation(JTextField txt) {
        String correo = txt.getText().trim();

        //Verificar espacio vacio
        if (correo.isEmpty()) {
            return emptySpace(txt);
        }

        //Tiene @?
        if (!correo.contains("@")) {
            return "Al correo le hace falta el símbolo '@'. Estructura requerida: usuario@proveedor.dominio";
        }

        //No mas de un @ 
        if (correo.indexOf('@') != correo.lastIndexOf('@')) {
            return "El correo electrónico no puede tener más de un símbolo '@'.";
        }

        //Ver si todo está bien
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(correo);

        if (!matcher.matches()) {
            return "Asegúrate de incluir el proveedor y un dominio válido (ejemplo: ejemplo@gmail.com).";
        }

        return "";
    }

    public static String passwordValidation(JPasswordField txt) {
        System.out.println("Validando contraseña...");
        
        char[] pass = txt.getPassword();
        String password = new String(pass);
                
        //Verificar espacio vacio
        if (password.isEmpty()) {
            return emptySpace(txt);
        }

        //8 caraceres
        if (password.length() < 8) {
            System.out.println("Devolviendo La contraseña debe tener al menos 8 caracteres.");
            return "La contraseña debe tener al menos 8 caracteres.";
        }

        //Mayuscula
        if (!password.matches(".*[A-Z].*")) {
            return "La contraseña debe incluir al menos una letra mayúscula.";
        }

        //Minusculas
        if (!password.matches(".*[a-z].*")) {
            return "La contraseña debe incluir al menos una letra minúscula.";
        }

        //Especiales
        if (!password.matches(".*[^A-Za-z0-9].*")) {
            return "La contraseña debe incluir al menos un carácter especial (ej: !, @, #, $, etc.).";
        }

        return "";
    }

    public static String emptySpace(JTextField txt) {
        if (txt.getText().isEmpty()) {
            return "No puede dejar este campo vacío";
        } else {
            return txt.getText();
        }
    }
}
