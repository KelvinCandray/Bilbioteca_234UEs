/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.DAO.impl;

/**
 *
 * @author KelvinCandray
 */


import com.mycompany.DAO.ConexionBD;
import com.mycompany.DAO.PersonaDAO;
import com.mycompany.model.Empleado;
import com.mycompany.model.Persona;
import com.mycompany.model.Usuario;

import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class PersonaDAOImpl implements PersonaDAO {

    private Connection con() throws SQLException { return ConexionBD.getConexion(); }

    // ─── AUTENTICACIÓN ────────────────────────────────────────────────────────

    @Override
    public Empleado autenticarEmpleado(String usuario, String contrasena) throws Exception {
        String sql = """
            SELECT p.id_persona, p.primer_nombre, p.apellido, p.correo, p.telefono,
                   p.fecha_nacimiento, p.pasaje, p.numero_casa, p.colonia, p.municipio,
                   p.departamento, e.salario, e.tipo_empleado
            FROM personas p
            INNER JOIN empleados e ON p.id_persona = e.id_persona
            WHERE e.usuario = ? AND e.contrasena = ?
            """;
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, usuario);
            ps.setString(2, contrasena);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Empleado e = new Empleado();
                e.setIdPersona(rs.getInt("id_persona"));
                e.setPrimerNombre(rs.getString("primer_nombre"));
                e.setApellido(rs.getString("apellido"));
                e.setCorreo(rs.getString("correo"));
                e.setTelefono(rs.getString("telefono"));
                e.setFechaNacimiento(rs.getString("fecha_nacimiento"));
                e.setPasaje(rs.getString("pasaje"));
                e.setNumeroCasa(rs.getString("numero_casa"));
                e.setColonia(rs.getString("colonia"));
                e.setMunicipio(rs.getString("municipio"));
                e.setDepartamento(rs.getString("departamento"));
                e.setSalario(rs.getDouble("salario"));
                e.setTipoEmpleado(rs.getString("tipo_empleado"));
                e.setUsuario(usuario);
                return e;
            }
        }
        return null; // credenciales incorrectas
    }

    @Override
    public Usuario autenticarUsuario(String usuario, String contrasena) throws Exception {
        String sql = """
            SELECT p.id_persona, p.primer_nombre, p.apellido, p.correo, p.telefono,
                   p.fecha_nacimiento, p.pasaje, p.numero_casa, p.colonia, p.municipio,
                   p.departamento
            FROM personas p
            INNER JOIN usuarios u ON p.id_persona = u.id_persona
            WHERE u.usuario = ? AND u.contrasena = ?
            """;
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, usuario);
            ps.setString(2, contrasena);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Usuario u = new Usuario();
                u.setIdPersona(rs.getInt("id_persona"));
                u.setPrimerNombre(rs.getString("primer_nombre"));
                u.setApellido(rs.getString("apellido"));
                u.setCorreo(rs.getString("correo"));
                u.setTelefono(rs.getString("telefono"));
                u.setFechaNacimiento(rs.getString("fecha_nacimiento"));
                u.setPasaje(rs.getString("pasaje"));
                u.setNumeroCasa(rs.getString("numero_casa"));
                u.setColonia(rs.getString("colonia"));
                u.setMunicipio(rs.getString("municipio"));
                u.setDepartamento(rs.getString("departamento"));
                u.setUsuario(usuario);
                return u;
            }
        }
        return null;
    }

    // ─── REGISTRO ─────────────────────────────────────────────────────────────

    @Override
    public void registrarUsuario(Usuario u) throws Exception {
        // Si la persona ya existe, solo crear el registro de usuario
        if (!existePersona(u.getIdPersona())) {
            insertarPersona(u);
        } else if (existeUsuario(u.getIdPersona())) {
            throw new Exception("Esta persona ya está registrada como usuario.");
        }
        String sql = "INSERT INTO usuarios (id_persona, usuario, contrasena) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, u.getIdPersona());
            ps.setString(2, u.getUsuario());
            ps.setString(3, u.getContrasena());
            ps.executeUpdate();
        }
    }

    @Override
    public void registrarEmpleado(Empleado e) throws Exception {
        if (existePersona(e.getIdPersona())) {
            throw new Exception("Esta persona ya está registrada en el sistema.");
        }
        if (e.getSalario() < 408.80) {
            throw new Exception("El salario no puede ser menor al mínimo (Q408.80).");
        }
        insertarPersona(e);
        String sql = "INSERT INTO empleados (id_persona, salario, tipo_empleado, usuario, contrasena) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, e.getIdPersona());
            ps.setDouble(2, e.getSalario());
            ps.setString(3, e.getTipoEmpleado());
            ps.setString(4, e.getUsuario());
            ps.setString(5, e.getContrasena());
            ps.executeUpdate();
        }
    }

    private void insertarPersona(Persona p) throws Exception {
        String sql = """
            INSERT INTO personas (id_persona, primer_nombre, apellido, correo, telefono,
                fecha_nacimiento, pasaje, numero_casa, colonia, municipio, departamento)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, p.getIdPersona());
            ps.setString(2, p.getPrimerNombre());
            ps.setString(3, p.getApellido());
            ps.setString(4, p.getCorreo());
            ps.setString(5, p.getTelefono());
            ps.setString(6, p.getFechaNacimiento());
            ps.setString(7, p.getPasaje());
            ps.setString(8, p.getNumeroCasa());
            ps.setString(9, p.getColonia());
            ps.setString(10, p.getMunicipio());
            ps.setString(11, p.getDepartamento());
            ps.executeUpdate();
        }
    }

    // ─── EDICIÓN / ELIMINACIÓN ──────────────────────────────────────────────

    @Override
    public void actualizarPersona(Persona persona) throws Exception {
        String check = "SELECT COUNT(*) FROM personas WHERE correo = ? AND id_persona != ?";
        try (PreparedStatement ps = con().prepareStatement(check)) {
            ps.setString(1, persona.getCorreo());
            ps.setInt(2, persona.getIdPersona());
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0)
                throw new Exception("Ya existe otra persona registrada con ese correo.");
        }
        String sql = """
            UPDATE personas
            SET primer_nombre = ?, apellido = ?, correo = ?, telefono = ?, fecha_nacimiento = ?,
                pasaje = ?, numero_casa = ?, colonia = ?, municipio = ?, departamento = ?
            WHERE id_persona = ?
            """;
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, persona.getPrimerNombre());
            ps.setString(2, persona.getApellido());
            ps.setString(3, persona.getCorreo());
            ps.setString(4, persona.getTelefono());
            ps.setString(5, persona.getFechaNacimiento());
            ps.setString(6, persona.getPasaje());
            ps.setString(7, persona.getNumeroCasa());
            ps.setString(8, persona.getColonia());
            ps.setString(9, persona.getMunicipio());
            ps.setString(10, persona.getDepartamento());
            ps.setInt(11, persona.getIdPersona());
            int filas = ps.executeUpdate();
            if (filas == 0) throw new Exception("La persona que intenta editar ya no existe.");
        }
    }

    @Override
    public void actualizarUsuario(int idPersona, String usuario, String nuevaContrasena) throws Exception {
        String check = "SELECT COUNT(*) FROM usuarios WHERE usuario = ? AND id_persona != ?";
        try (PreparedStatement ps = con().prepareStatement(check)) {
            ps.setString(1, usuario);
            ps.setInt(2, idPersona);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0)
                throw new Exception("Ese nombre de usuario ya está en uso.");
        }
        // Si no se proporciona una contraseña nueva, se conserva la actual (no se pisa con vacío).
        boolean cambiarContrasena = nuevaContrasena != null && !nuevaContrasena.isEmpty();
        String sql = cambiarContrasena
                ? "UPDATE usuarios SET usuario = ?, contrasena = ? WHERE id_persona = ?"
                : "UPDATE usuarios SET usuario = ? WHERE id_persona = ?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, usuario);
            if (cambiarContrasena) {
                ps.setString(2, nuevaContrasena);
                ps.setInt(3, idPersona);
            } else {
                ps.setInt(2, idPersona);
            }
            int filas = ps.executeUpdate();
            if (filas == 0) throw new Exception("El usuario que intenta editar ya no existe.");
        }
    }

    @Override
    public void actualizarEmpleado(int idPersona, double salario, String tipoEmpleado, String usuario, String nuevaContrasena) throws Exception {
        if (salario < 408.80)
            throw new Exception("El salario no puede ser menor al mínimo (Q408.80).");
        String check = "SELECT COUNT(*) FROM empleados WHERE usuario = ? AND id_persona != ?";
        try (PreparedStatement ps = con().prepareStatement(check)) {
            ps.setString(1, usuario);
            ps.setInt(2, idPersona);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0)
                throw new Exception("Ese nombre de usuario ya está en uso.");
        }
        boolean cambiarContrasena = nuevaContrasena != null && !nuevaContrasena.isEmpty();
        String sql = cambiarContrasena
                ? "UPDATE empleados SET salario = ?, tipo_empleado = ?, usuario = ?, contrasena = ? WHERE id_persona = ?"
                : "UPDATE empleados SET salario = ?, tipo_empleado = ?, usuario = ? WHERE id_persona = ?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setDouble(1, salario);
            ps.setString(2, tipoEmpleado);
            ps.setString(3, usuario);
            if (cambiarContrasena) {
                ps.setString(4, nuevaContrasena);
                ps.setInt(5, idPersona);
            } else {
                ps.setInt(4, idPersona);
            }
            int filas = ps.executeUpdate();
            if (filas == 0) throw new Exception("El empleado que intenta editar ya no existe.");
        }
    }

    @Override
    public void eliminarPersona(int idPersona) throws Exception {
        if (idPersona == 1)
            throw new Exception("No se puede dar de baja la cuenta administradora predeterminada del sistema.");

        ConexionBD.ejecutarEnTransaccion(() -> {
            if (existeUsuario(idPersona)) {
                String sqlHistorial = "SELECT COUNT(*) FROM prestamos WHERE id_usuario = ?";
                try (PreparedStatement ps = con().prepareStatement(sqlHistorial)) {
                    ps.setInt(1, idPersona);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0)
                        throw new Exception("No se puede dar de baja: esta persona tiene préstamos en su historial como lector.");
                }
                try (PreparedStatement ps = con().prepareStatement("DELETE FROM usuarios WHERE id_persona = ?")) {
                    ps.setInt(1, idPersona);
                    ps.executeUpdate();
                }
            }
            if (existeEmpleado(idPersona)) {
                String sqlHistorial = "SELECT COUNT(*) FROM prestamos WHERE id_empleado = ?";
                try (PreparedStatement ps = con().prepareStatement(sqlHistorial)) {
                    ps.setInt(1, idPersona);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0)
                        throw new Exception("No se puede dar de baja: esta persona aprobó préstamos que siguen en el historial.");
                }
                try (PreparedStatement ps = con().prepareStatement("DELETE FROM empleados WHERE id_persona = ?")) {
                    ps.setInt(1, idPersona);
                    ps.executeUpdate();
                }
            }
            try (PreparedStatement ps = con().prepareStatement("DELETE FROM personas WHERE id_persona = ?")) {
                ps.setInt(1, idPersona);
                int filas = ps.executeUpdate();
                if (filas == 0) throw new Exception("La persona que intenta dar de baja ya no existe.");
            }
        });
    }

    // ─── VERIFICACIONES ───────────────────────────────────────────────────────

    @Override
    public boolean existePersona(int id) throws Exception {
        return contar("SELECT COUNT(*) FROM personas WHERE id_persona = ?", id) > 0;
    }

    @Override
    public boolean existeUsuario(int id) throws Exception {
        return contar("SELECT COUNT(*) FROM usuarios WHERE id_persona = ?", id) > 0;
    }

    @Override
    public boolean existeEmpleado(int id) throws Exception {
        return contar("SELECT COUNT(*) FROM empleados WHERE id_persona = ?", id) > 0;
    }

    @Override
    public boolean existeBibliotecario(int id) throws Exception {
        String sql = "SELECT COUNT(*) FROM empleados WHERE id_persona = ? AND tipo_empleado = 'Bibliotecario'";
        return contar(sql, id) > 0;
    }

    @Override
    public int calcularEdad(int idUsuario) throws Exception {
        String sql = """
            SELECT p.fecha_nacimiento FROM personas p
            INNER JOIN usuarios u ON p.id_persona = u.id_persona
            WHERE u.id_persona = ?
            """;
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                LocalDate fechaNac = LocalDate.parse(rs.getString("fecha_nacimiento"));
                return Period.between(fechaNac, LocalDate.now()).getYears();
            }
        }
        return 0;
    }

    // ─── CONSULTAS ────────────────────────────────────────────────────────────

    @Override
    public List<Object[]> obtenerPersonasCompletas() throws Exception {
        List<Object[]> lista = new ArrayList<>();
        String sql = """
            SELECT p.id_persona, p.primer_nombre, p.apellido, p.correo, p.telefono,
                   p.fecha_nacimiento, p.departamento, p.municipio,
                   CASE WHEN u.id_persona IS NOT NULL THEN 'Sí' ELSE 'No' END AS es_usuario,
                   CASE WHEN e.id_persona IS NOT NULL THEN e.tipo_empleado ELSE 'No' END AS es_empleado,
                   COALESCE(e.salario, 0) AS salario,
                   p.pasaje, p.numero_casa, p.colonia,
                   COALESCE(u.usuario, e.usuario) AS usuario_login
            FROM personas p
            LEFT JOIN usuarios u ON p.id_persona = u.id_persona
            LEFT JOIN empleados e ON p.id_persona = e.id_persona
            ORDER BY p.id_persona DESC
            """;
        try (Statement st = con().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getInt("id_persona"),
                    rs.getString("primer_nombre"),
                    rs.getString("apellido"),
                    rs.getString("correo"),
                    rs.getString("telefono"),
                    rs.getString("fecha_nacimiento"),
                    rs.getString("departamento"),
                    rs.getString("municipio"),
                    rs.getString("es_usuario"),
                    rs.getString("es_empleado"),
                    rs.getDouble("salario") > 0 ? rs.getDouble("salario") : "N/A",
                    rs.getString("pasaje"),
                    rs.getString("numero_casa"),
                    rs.getString("colonia"),
                    rs.getString("usuario_login")
                });
            }
        }
        return lista;
    }

    @Override
    public List<Empleado> obtenerEmpleados() throws Exception {
        List<Empleado> lista = new ArrayList<>();
        String sql = """
            SELECT p.id_persona, p.primer_nombre, p.apellido, p.correo, p.telefono,
                   e.salario, e.tipo_empleado, e.usuario
            FROM personas p INNER JOIN empleados e ON p.id_persona = e.id_persona
            ORDER BY p.id_persona DESC
            """;
        try (Statement st = con().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Empleado e = new Empleado();
                e.setIdPersona(rs.getInt("id_persona"));
                e.setPrimerNombre(rs.getString("primer_nombre"));
                e.setApellido(rs.getString("apellido"));
                e.setCorreo(rs.getString("correo"));
                e.setTelefono(rs.getString("telefono"));
                e.setSalario(rs.getDouble("salario"));
                e.setTipoEmpleado(rs.getString("tipo_empleado"));
                e.setUsuario(rs.getString("usuario"));
                lista.add(e);
            }
        }
        return lista;
    }

    @Override
    public List<Usuario> obtenerUsuarios() throws Exception {
        List<Usuario> lista = new ArrayList<>();
        String sql = """
            SELECT p.id_persona, p.primer_nombre, p.apellido, p.correo, p.telefono,
                   p.fecha_nacimiento, u.usuario
            FROM personas p INNER JOIN usuarios u ON p.id_persona = u.id_persona
            ORDER BY p.id_persona DESC
            """;
        try (Statement st = con().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setIdPersona(rs.getInt("id_persona"));
                u.setPrimerNombre(rs.getString("primer_nombre"));
                u.setApellido(rs.getString("apellido"));
                u.setCorreo(rs.getString("correo"));
                u.setTelefono(rs.getString("telefono"));
                u.setFechaNacimiento(rs.getString("fecha_nacimiento"));
                u.setUsuario(rs.getString("usuario"));
                lista.add(u);
            }
        }
        return lista;
    }

    // ─── HELPER ───────────────────────────────────────────────────────────────

    private int contar(String sql, int id) throws Exception {
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}