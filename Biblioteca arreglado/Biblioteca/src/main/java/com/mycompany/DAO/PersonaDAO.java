/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.DAO;

/**
 *
 * @author KelvinCandray
 */


import com.mycompany.model.Empleado;
import com.mycompany.model.Persona;
import com.mycompany.model.Usuario;
import java.util.List;

public interface PersonaDAO {

    // Autenticación
    Empleado autenticarEmpleado(String usuario, String contrasena) throws Exception;
    Usuario  autenticarUsuario(String usuario, String contrasena) throws Exception;

    // Registro
    void registrarUsuario(Usuario usuario) throws Exception;
    void registrarEmpleado(Empleado empleado) throws Exception;

    // Verificaciones
    boolean existePersona(int idPersona) throws Exception;
    boolean existeUsuario(int idPersona) throws Exception;
    boolean existeEmpleado(int idPersona) throws Exception;
    boolean existeBibliotecario(int idPersona) throws Exception;
    int     calcularEdad(int idUsuario) throws Exception;

    // Consultas
    List<Object[]> obtenerPersonasCompletas() throws Exception;
    List<Empleado> obtenerEmpleados() throws Exception;
    List<Usuario>  obtenerUsuarios() throws Exception;
}
