package com.mycompany.contoller;

import com.mycompany.DAO.PersonaDAO;
import com.mycompany.DAO.impl.PersonaDAOImpl;
import com.mycompany.model.Empleado;
import com.mycompany.model.Usuario;
import com.mycompany.view.LogInView;
import java.awt.Color;
import javax.swing.JOptionPane;
import javax.swing.border.LineBorder;

/**
 *
 * @author betuel
 */
public class LogInController {

    LogInView view;
    PersonaDAO personaDAO;

    public LogInController() {
        view = new LogInView();
        view.setVisible(true);
        personaDAO = new PersonaDAOImpl();

        buttonActions();
        textActions();
    }

    private void buttonActions() {
        view.btnlogIn.addActionListener(e -> intentarIniciarSesion());
    }

    /**
     * Valida los campos, consulta la base de datos y abre el controlador
     * correspondiente según el rol encontrado (usuario, empleado o
     * administrador).
     */
    private void intentarIniciarSesion() {
        String usuarioIngresado = view.txtCorreo.getText().trim();
        String contrasenaIngresada = new String(view.txtContraseña.getPassword());

        if (!validarCamposLlenos(usuarioIngresado, contrasenaIngresada)) {
            return;
        }

        try {
            // 1) ¿Es un usuario (cliente/lector)?
            Usuario usuario = personaDAO.autenticarUsuario(usuarioIngresado, contrasenaIngresada);
            if (usuario != null) {
                view.dispose();
                new ClientController(usuario);
                return;
            }

            // 2) ¿Es un empleado (bibliotecario, vigilante o gerente/admin)?
            Empleado empleado = personaDAO.autenticarEmpleado(usuarioIngresado, contrasenaIngresada);
            if (empleado != null) {
                view.dispose();
                if ("Gerente".equalsIgnoreCase(empleado.getTipoEmpleado())) {
                    new AdminController(empleado);
                } else {
                    new EmployeController(empleado);
                }
                return;
            }

            // 3) No coincide con ningún rol -> credenciales incorrectas
            marcarCredencialesIncorrectas();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view,
                    "Ocurrió un error al conectar con la base de datos:\n" + ex.getMessage(),
                    "Error de conexión",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Verifica que los campos no estén vacíos y marca visualmente los
     * errores. Nota: no se usan aquí las validaciones de formato de correo /
     * complejidad de contraseña (Validations.java), ya que esas reglas
     * aplican a un formulario de registro, no al inicio de sesión: las
     * credenciales ya existentes en la base de datos (admin/admin123,
     * carlosM/biblio123, etc.) no cumplirían esos formatos.
     */
    private boolean validarCamposLlenos(String usuario, String contrasena) {
        boolean valido = true;

        if (usuario.isEmpty()) {
            view.lblCorreoError.setText("No puede dejar este campo vacío");
            view.txtCorreo.setBorder(new LineBorder(Color.RED, 1));
            valido = false;
        } else {
            view.lblCorreoError.setText("");
            view.txtCorreo.setBorder(new LineBorder(Color.BLACK, 1));
        }

        if (contrasena.isEmpty()) {
            view.lblContraseñaError.setText("No puede dejar este campo vacío");
            view.txtContraseña.setBorder(new LineBorder(Color.RED, 1));
            valido = false;
        } else {
            view.lblContraseñaError.setText("");
            view.txtContraseña.setBorder(new LineBorder(Color.BLACK, 1));
        }

        return valido;
    }

    private void marcarCredencialesIncorrectas() {
        view.lblCorreoError.setText("Usuario o contraseña incorrectos");
        view.txtCorreo.setBorder(new LineBorder(Color.RED, 1));
        view.txtContraseña.setBorder(new LineBorder(Color.RED, 1));
    }

    private void textActions() {

    }
}
