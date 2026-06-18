package com.mycompany.contoller;

import com.mycompany.view.LogInView;
import com.mycompany.view.MainInterface;
import java.awt.event.ActionEvent;
import com.mycompany.util.Validations;
import java.awt.Color;
import javax.swing.border.LineBorder;

/**
 *
 * @author betuel
 */
public class LogInController {
    LogInView view;
    
    
    public LogInController (){
        view = new LogInView();
        view.setVisible(true);
        
        buttonActions();
        textActions();
    }

    private void buttonActions() {
        view.btnlogIn.addActionListener(e -> {
            view.lblContraseñaError.setText(Validations.passwordValidation(view.txtContraseña));
            view.lblCorreoError.setText(Validations.correoValidation(view.txtCorreo));
            
            if (!view.lblCorreoError.getText().isEmpty()){
                view.txtCorreo.setBorder(new LineBorder(Color.RED, 1));
            }else{
                view.txtCorreo.setBorder(new LineBorder(Color.BLACK, 1));
            }
            
            if (!view.lblContraseñaError.getText().isEmpty()){
                view.txtContraseña.setBorder(new LineBorder(Color.RED, 1));
            }else{
                view.txtContraseña.setBorder(new LineBorder(Color.BLACK, 1));
            }
        });
    }

    private void textActions() {
        
    }
}
