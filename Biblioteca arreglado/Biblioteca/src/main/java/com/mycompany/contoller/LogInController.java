package com.mycompany.contoller;

import com.mycompany.DAO.PersonaDAO;
import com.mycompany.DAO.impl.PersonaDAOImpl;
import com.mycompany.model.Empleado;
import com.mycompany.model.Persona;
import com.mycompany.model.Usuario;
import com.mycompany.util.Validations;
import com.mycompany.view.LogInView;
import com.mycompany.view.PersonaData;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JFrame;
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
        view.btnsingUp.addActionListener(e -> mostrarRegistro());
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

    /**
     * Abre una ventana de autorregistro para crear una cuenta de Usuario
     * (lector). A propósito no permite elegir el rol "Empleado": esa cuenta
     * solo la puede crear un administrador desde su panel.
     */
    private void mostrarRegistro() {
        PersonaData form = new PersonaData();
        form.configurarParaAutoRegistro();

        JFrame ventana = new JFrame("Crear cuenta de lector");
        ventana.setSize(820, 560);
        ventana.setMinimumSize(new java.awt.Dimension(820, 560));
        ventana.setLocationRelativeTo(view);
        ventana.setLayout(new BorderLayout());
        ventana.add(form, BorderLayout.CENTER);
        ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        form.btnGuardar.addActionListener(e -> {
            try {
                String errorCorreo = Validations.correoValidation(form.txtCorreo);
                if (!errorCorreo.isEmpty()) {
                    JOptionPane.showMessageDialog(ventana, errorCorreo, "Correo inválido", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String errorPass = Validations.passwordValidation(form.txtContrasena);
                if (!errorPass.isEmpty()) {
                    JOptionPane.showMessageDialog(ventana, errorPass, "Contraseña inválida", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String usuarioLogin = form.getUsuarioLogin();
                if (usuarioLogin.isEmpty()) {
                    JOptionPane.showMessageDialog(ventana, "El nombre de usuario es obligatorio.", "Datos incompletos", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Persona datos;
                try {
                    datos = form.getPersonaFromFields();
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(ventana, "El DUI / ID Persona debe ser un número entero.", "Datos inválidos", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (datos.getPrimerNombre().isEmpty() || datos.getApellido().isEmpty() || datos.getFechaNacimiento().isEmpty()) {
                    JOptionPane.showMessageDialog(ventana, "Nombre, apellido y fecha de nacimiento son obligatorios.", "Datos incompletos", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Usuario nuevoUsuario = new Usuario();
                nuevoUsuario.setIdPersona(datos.getIdPersona());
                nuevoUsuario.setPrimerNombre(datos.getPrimerNombre());
                nuevoUsuario.setApellido(datos.getApellido());
                nuevoUsuario.setCorreo(datos.getCorreo());
                nuevoUsuario.setTelefono(datos.getTelefono());
                nuevoUsuario.setFechaNacimiento(datos.getFechaNacimiento());
                nuevoUsuario.setPasaje(datos.getPasaje());
                nuevoUsuario.setNumeroCasa(datos.getNumeroCasa());
                nuevoUsuario.setColonia(datos.getColonia());
                nuevoUsuario.setMunicipio(datos.getMunicipio());
                nuevoUsuario.setDepartamento(datos.getDepartamento());
                nuevoUsuario.setUsuario(usuarioLogin);
                nuevoUsuario.setContrasena(form.getContrasena());

                personaDAO.registrarUsuario(nuevoUsuario);
                JOptionPane.showMessageDialog(ventana, "¡Cuenta creada! Ya puedes iniciar sesión.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                ventana.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(ventana, ex.getMessage(), "No se pudo crear la cuenta", JOptionPane.ERROR_MESSAGE);
            }
        });

        form.btnCancelar.addActionListener(e -> ventana.dispose());

        ventana.setVisible(true);
    }

    private void textActions() {

    }
}