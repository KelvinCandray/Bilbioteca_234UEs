package com.mycompany.view;

import com.mycompany.model.Persona;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 * Formulario único para los datos de una Persona, reutilizado en tres
 * contextos distintos (para no duplicar una pantalla casi idéntica tres
 * veces):
 * <ul>
 *   <li>Alta/edición desde el panel de Administrador (puede elegir si la
 *       persona es Usuario o Empleado, y fijar salario/cargo).</li>
 *   <li>Autorregistro desde la pantalla de inicio de sesión (rol fijo en
 *       "Usuario"; nadie puede autoasignarse como Empleado).</li>
 *   <li>"Mi perfil" (los datos de contacto son editables, pero el salario y
 *       el cargo de un empleado quedan de solo lectura: eso lo cambia un
 *       administrador, no el propio empleado).</li>
 * </ul>
 *
 * @author betuel
 */
public class PersonaData extends JPanel {

    public static final String ROL_USUARIO = "Usuario (Lector)";
    public static final String ROL_EMPLEADO = "Empleado";

    // Datos de Persona
    public JTextField txtIdPersona;
    public JTextField txtPrimerNombre;
    public JTextField txtApellido;
    public JTextField txtCorreo;
    public JTextField txtTelefono;
    public JTextField txtFechaNacimiento;
    public JTextField txtPasaje;
    public JTextField txtNumeroCasa;
    public JTextField txtColonia;
    public JTextField txtMunicipio;
    public JTextField txtDepartamento;

    // Rol y credenciales
    public JComboBox<String> comboRol;
    private final JLabel lblRolFijo = new JLabel();
    public JTextField txtUsuarioLogin;
    public JPasswordField txtContrasena;
    public JLabel lblContrasenaAyuda;

    // Solo aplica al rol Empleado
    public JLabel lblSalario;
    public JTextField txtSalario;
    public JLabel lblTipoEmpleado;
    public JComboBox<String> comboTipoEmpleado;

    public JButton btnCancelar;
    public JButton btnGuardar;
    public JButton btnVolver;

    private final JLabel lblTitulo = new JLabel("Registrar persona");
    private final JPanel panelEmpleado = new JPanel(new GridLayout(2, 1, 0, 10));

    public PersonaData() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 15));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 30));

        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        add(lblTitulo, BorderLayout.NORTH);

        Font formFont = new Font("Comic Sans MS", Font.PLAIN, 13);

        // --- Columna izquierda: datos de la persona ---
        JPanel datosPersona = new JPanel(new GridLayout(11, 1, 0, 8));
        datosPersona.setBackground(Color.WHITE);

        txtIdPersona = new JTextField();
        txtPrimerNombre = new JTextField();
        txtApellido = new JTextField();
        txtCorreo = new JTextField();
        txtTelefono = new JTextField();
        txtFechaNacimiento = new JTextField();
        txtFechaNacimiento.setToolTipText("Formato AAAA-MM-DD");
        txtPasaje = new JTextField();
        txtNumeroCasa = new JTextField();
        txtColonia = new JTextField();
        txtMunicipio = new JTextField();
        txtDepartamento = new JTextField();

        datosPersona.add(crearCampoPanel("DUI / ID Persona", txtIdPersona, formFont));
        datosPersona.add(crearCampoPanel("Primer nombre", txtPrimerNombre, formFont));
        datosPersona.add(crearCampoPanel("Apellido", txtApellido, formFont));
        datosPersona.add(crearCampoPanel("Correo electrónico", txtCorreo, formFont));
        datosPersona.add(crearCampoPanel("Teléfono", txtTelefono, formFont));
        datosPersona.add(crearCampoPanel("Fecha de nacimiento (AAAA-MM-DD)", txtFechaNacimiento, formFont));
        datosPersona.add(crearCampoPanel("Pasaje", txtPasaje, formFont));
        datosPersona.add(crearCampoPanel("Número de casa", txtNumeroCasa, formFont));
        datosPersona.add(crearCampoPanel("Colonia", txtColonia, formFont));
        datosPersona.add(crearCampoPanel("Municipio", txtMunicipio, formFont));
        datosPersona.add(crearCampoPanel("Departamento", txtDepartamento, formFont));

        JScrollPane scrollPersona = new JScrollPane(datosPersona);
        scrollPersona.setBorder(BorderFactory.createEmptyBorder());

        // --- Columna derecha: rol y credenciales ---
        JPanel panelRol = new JPanel();
        panelRol.setLayout(new javax.swing.BoxLayout(panelRol, javax.swing.BoxLayout.Y_AXIS));
        panelRol.setBackground(Color.WHITE);

        comboRol = new JComboBox<>(new String[]{ROL_USUARIO, ROL_EMPLEADO});
        lblRolFijo.setFont(new Font("SansSerif", Font.BOLD, 14));

        txtUsuarioLogin = new JTextField();
        txtContrasena = new JPasswordField();
        lblContrasenaAyuda = new JLabel("(dejar vacío para no cambiarla)");
        lblContrasenaAyuda.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblContrasenaAyuda.setForeground(Color.GRAY);
        lblContrasenaAyuda.setVisible(false);

        txtSalario = new JTextField();
        lblSalario = new JLabel("Salario mensual ($)");
        comboTipoEmpleado = new JComboBox<>(new String[]{"Gerente", "Bibliotecario", "Vigilante"});
        lblTipoEmpleado = new JLabel("Cargo");

        panelRol.add(crearCampoPanel("Rol", comboRol, formFont));
        panelRol.add(lblRolFijo);
        panelRol.add(javax.swing.Box.createVerticalStrut(10));
        panelRol.add(crearCampoPanel("Usuario (login)", txtUsuarioLogin, formFont));
        JPanel panelClave = crearCampoPanel("Contraseña", txtContrasena, formFont);
        panelClave.add(lblContrasenaAyuda, BorderLayout.SOUTH);
        panelRol.add(panelClave);

        panelEmpleado.setBackground(Color.WHITE);
        panelEmpleado.add(crearCampoPanel(lblSalario.getText(), txtSalario, formFont));
        panelEmpleado.add(crearComboPanel(lblTipoEmpleado.getText(), comboTipoEmpleado, formFont));
        panelRol.add(javax.swing.Box.createVerticalStrut(10));
        panelRol.add(panelEmpleado);

        JPanel formPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        formPanel.setBackground(Color.WHITE);
        formPanel.add(scrollPersona);
        formPanel.add(panelRol);

        add(formPanel, BorderLayout.CENTER);

        // --- BOTONES ---
        JPanel panelBotonesContainer = new JPanel(new BorderLayout());
        panelBotonesContainer.setBackground(Color.WHITE);
        panelBotonesContainer.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JPanel panelIzquierdo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelIzquierdo.setBackground(Color.WHITE);
        btnVolver = new JButton("⬅ Volver");
        btnVolver.setFont(formFont);
        btnVolver.setBackground(new Color(200, 200, 200));
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.setVisible(false);
        panelIzquierdo.add(btnVolver);

        JPanel panelDerecho = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelDerecho.setBackground(Color.WHITE);
        btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(formFont);
        btnCancelar.setBackground(new Color(230, 230, 230));
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnGuardar = new JButton("💾 Guardar cambios");
        btnGuardar.setFont(formFont);
        btnGuardar.setBackground(new Color(42, 157, 143));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        panelDerecho.add(btnCancelar);
        panelDerecho.add(btnGuardar);

        panelBotonesContainer.add(panelIzquierdo, BorderLayout.WEST);
        panelBotonesContainer.add(panelDerecho, BorderLayout.EAST);
        add(panelBotonesContainer, BorderLayout.SOUTH);

        comboRol.addActionListener(e -> actualizarVisibilidadCamposEmpleado());
        actualizarVisibilidadCamposEmpleado();
    }

    private void actualizarVisibilidadCamposEmpleado() {
        boolean esEmpleado = ROL_EMPLEADO.equals(comboRol.getSelectedItem());
        panelEmpleado.setVisible(esEmpleado);
        revalidate();
        repaint();
    }

    private JPanel crearCampoPanel(String textoLabel, JTextField campoTexto, Font fuente) {
        JPanel panel = new JPanel(new BorderLayout(0, 3));
        panel.setBackground(Color.WHITE);
        JLabel etiqueta = new JLabel(textoLabel);
        etiqueta.setFont(fuente);
        campoTexto.setFont(fuente);
        campoTexto.setPreferredSize(new java.awt.Dimension(campoTexto.getPreferredSize().width, 28));
        panel.add(etiqueta, BorderLayout.NORTH);
        panel.add(campoTexto, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearCampoPanel(String textoLabel, JComboBox<String> combo, Font fuente) {
        JPanel panel = new JPanel(new BorderLayout(0, 3));
        panel.setBackground(Color.WHITE);
        JLabel etiqueta = new JLabel(textoLabel);
        etiqueta.setFont(fuente);
        combo.setFont(fuente);
        panel.add(etiqueta, BorderLayout.NORTH);
        panel.add(combo, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearComboPanel(String textoLabel, JComboBox<String> combo, Font fuente) {
        return crearCampoPanel(textoLabel, combo, fuente);
    }

    // ─── CONFIGURACIONES DE USO ─────────────────────────────────────────────

    /** Alta nueva desde el panel de Administrador: rol elegible, ID editable. */
    public void configurarParaRegistroAdmin() {
        lblTitulo.setText("Registrar persona");
        txtIdPersona.setEditable(true);
        comboRol.setVisible(true);
        lblRolFijo.setVisible(false);
        habilitarCamposEmpleado(true);
    }

    /** Edición desde el panel de Administrador: ID bloqueado, rol fijo al actual. */
    public void configurarParaEdicionAdmin(boolean esEmpleado) {
        lblTitulo.setText("Editar persona");
        txtIdPersona.setEditable(false);
        comboRol.setVisible(false);
        lblRolFijo.setVisible(true);
        lblRolFijo.setText("Rol: " + (esEmpleado ? ROL_EMPLEADO : ROL_USUARIO));
        comboRol.setSelectedItem(esEmpleado ? ROL_EMPLEADO : ROL_USUARIO);
        habilitarCamposEmpleado(true);
        lblContrasenaAyuda.setVisible(true);
        panelEmpleado.setVisible(esEmpleado);
    }

    /** Autorregistro desde la pantalla de inicio de sesión: siempre rol Usuario. */
    public void configurarParaAutoRegistro() {
        lblTitulo.setText("Crear cuenta de lector");
        txtIdPersona.setEditable(true);
        comboRol.setVisible(false);
        lblRolFijo.setVisible(true);
        lblRolFijo.setText("Rol: " + ROL_USUARIO);
        comboRol.setSelectedItem(ROL_USUARIO);
        panelEmpleado.setVisible(false);
    }

    /** "Mi perfil": puede editar sus datos de contacto y credenciales, pero no su salario/cargo. */
    public void configurarParaMiPerfil(boolean esEmpleado) {
        lblTitulo.setText("Mi perfil");
        txtIdPersona.setEditable(false);
        comboRol.setVisible(false);
        lblRolFijo.setVisible(true);
        lblRolFijo.setText("Rol: " + (esEmpleado ? ROL_EMPLEADO : ROL_USUARIO));
        comboRol.setSelectedItem(esEmpleado ? ROL_EMPLEADO : ROL_USUARIO);
        lblContrasenaAyuda.setVisible(true);
        panelEmpleado.setVisible(esEmpleado);
        habilitarCamposEmpleado(false); // puede ver su salario/cargo, pero no cambiarlos
    }

    private void habilitarCamposEmpleado(boolean habilitado) {
        txtSalario.setEditable(habilitado);
        comboTipoEmpleado.setEnabled(habilitado);
    }

    public void setVolverVisible(boolean visible) {
        btnVolver.setVisible(visible);
        revalidate();
        repaint();
    }

    // ─── CARGA / LECTURA DE DATOS ───────────────────────────────────────────

    public void setPersona(Persona p) {
        if (p == null) return;
        txtIdPersona.setText(String.valueOf(p.getIdPersona()));
        txtPrimerNombre.setText(p.getPrimerNombre());
        txtApellido.setText(p.getApellido());
        txtCorreo.setText(p.getCorreo());
        txtTelefono.setText(p.getTelefono());
        txtFechaNacimiento.setText(p.getFechaNacimiento());
        txtPasaje.setText(p.getPasaje());
        txtNumeroCasa.setText(p.getNumeroCasa());
        txtColonia.setText(p.getColonia());
        txtMunicipio.setText(p.getMunicipio());
        txtDepartamento.setText(p.getDepartamento());
    }

    public void setUsuarioLogin(String usuario) {
        txtUsuarioLogin.setText(usuario);
    }

    public void setSalario(double salario) {
        txtSalario.setText(String.valueOf(salario));
    }

    public void setTipoEmpleado(String tipo) {
        comboTipoEmpleado.setSelectedItem(tipo);
    }

    public boolean esRolEmpleado() {
        return ROL_EMPLEADO.equals(comboRol.getSelectedItem());
    }

    /** Construye una Persona base a partir de los campos. Lanza NumberFormatException si el ID no es válido. */
    public Persona getPersonaFromFields() {
        Persona p = new Persona();
        p.setIdPersona(Integer.parseInt(txtIdPersona.getText().trim()));
        p.setPrimerNombre(txtPrimerNombre.getText().trim());
        p.setApellido(txtApellido.getText().trim());
        p.setCorreo(txtCorreo.getText().trim());
        p.setTelefono(txtTelefono.getText().trim());
        p.setFechaNacimiento(txtFechaNacimiento.getText().trim());
        p.setPasaje(txtPasaje.getText().trim());
        p.setNumeroCasa(txtNumeroCasa.getText().trim());
        p.setColonia(txtColonia.getText().trim());
        p.setMunicipio(txtMunicipio.getText().trim());
        p.setDepartamento(txtDepartamento.getText().trim());
        return p;
    }

    public String getUsuarioLogin() {
        return txtUsuarioLogin.getText().trim();
    }

    /** Contraseña introducida, o cadena vacía si se dejó en blanco (= "no cambiar" en modo edición). */
    public String getContrasena() {
        return new String(txtContrasena.getPassword());
    }

    /** Lanza NumberFormatException si el salario no es un número válido. */
    public double getSalarioFromFields() {
        return Double.parseDouble(txtSalario.getText().trim());
    }

    public String getTipoEmpleadoSeleccionado() {
        return (String) comboTipoEmpleado.getSelectedItem();
    }
}
