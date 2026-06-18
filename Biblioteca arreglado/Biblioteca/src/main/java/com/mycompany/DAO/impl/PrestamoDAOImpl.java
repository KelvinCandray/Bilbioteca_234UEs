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
import com.mycompany.DAO.PrestamoDAO;
import com.mycompany.model.Multa;
import com.mycompany.model.Prestamo;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAOImpl implements PrestamoDAO {

    private final PersonaDAOImpl personaDAO = new PersonaDAOImpl();
    private final LibroDAOImpl   libroDAO   = new LibroDAOImpl();

    private Connection con() throws SQLException { return ConexionBD.getConexion(); }

    // ─── OPERACIONES PRINCIPALES ──────────────────────────────────────────────

    /**
     * El usuario solicita un préstamo. Se valida todo antes de insertar.
     * El préstamo queda en estado 'Solicitado' hasta que un bibliotecario lo apruebe.
     */
    @Override
    public void solicitarPrestamo(int idUsuario, int idEjemplar) throws Exception {

        // 1. ¿Existe el usuario?
        if (!personaDAO.existeUsuario(idUsuario))
            throw new Exception("El usuario no existe en el sistema.");

        // 2. ¿El ejemplar está disponible?
        String sqlEj = "SELECT estado FROM ejemplares WHERE id_ejemplar = ?";
        try (PreparedStatement ps = con().prepareStatement(sqlEj)) {
            ps.setInt(1, idEjemplar);
            ResultSet rs = ps.executeQuery();
            if (!rs.next())
                throw new Exception("El ejemplar no existe.");
            if (!"Disponible".equals(rs.getString("estado")))
                throw new Exception("El ejemplar no está disponible en este momento.");
        }

        // 3. ¿Tiene menos de 5 préstamos activos?
        if (contarPrestamosActivos(idUsuario) >= 5)
            throw new Exception("Has alcanzado el límite de 5 préstamos activos.");

        // 4. ¿Tiene préstamos vencidos sin devolver?
        if (tieneRetrasados(idUsuario))
            throw new Exception("Tienes préstamos vencidos sin devolver. Regresa el material antes de solicitar nuevos.");

        // 5. ¿Tiene multas pendientes?
        if (tieneMultasPendientes(idUsuario))
            throw new Exception("Tienes multas pendientes de pago.");

        // 6. ¿Cumple la edad mínima para esta categoría?
        int edadMinima = libroDAO.obtenerEdadMinima(idEjemplar);
        int edadUsuario = personaDAO.calcularEdad(idUsuario);
        if (edadUsuario < edadMinima)
            throw new Exception("No cumples con la edad mínima (" + edadMinima + " años) para este libro.");

        // 7. Todo en orden → insertar solicitud
        String sql = """
            INSERT INTO prestamos (id_usuario, id_ejemplar, estado, fecha_solicitud)
            VALUES (?, ?, 'Solicitado', DATE('now'))
            """;
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idEjemplar);
            ps.executeUpdate();
        }
    }

    /**
     * El bibliotecario aprueba la solicitud.
     * El préstamo pasa de 'Solicitado' a 'Prestado' y el ejemplar queda bloqueado.
     */
    @Override
    public void aprobarPrestamo(int idPrestamo, int idEmpleado) throws Exception {

        // Verificar que sea bibliotecario
        if (!personaDAO.existeBibliotecario(idEmpleado))
            throw new Exception("El empleado no tiene permisos para aprobar préstamos.");

        // Verificar que el préstamo esté en estado Solicitado
        String sqlCheck = "SELECT id_ejemplar, estado FROM prestamos WHERE id_prestamo = ?";
        int idEjemplar;
        try (PreparedStatement ps = con().prepareStatement(sqlCheck)) {
            ps.setInt(1, idPrestamo);
            ResultSet rs = ps.executeQuery();
            if (!rs.next())
                throw new Exception("El préstamo no existe.");
            if (!"Solicitado".equals(rs.getString("estado")))
                throw new Exception("Este préstamo no está pendiente de aprobación.");
            idEjemplar = rs.getInt("id_ejemplar");
        }

        // Actualizar el préstamo a 'Prestado' y el ejemplar a 'Prestado' como una sola unidad:
        // si una de las dos sentencias falla, no debe quedar un préstamo "Prestado" con su
        // ejemplar todavía "Disponible" (o viceversa).
        ConexionBD.ejecutarEnTransaccion(() -> {
            String sqlPrestamo = """
                UPDATE prestamos
                SET estado = 'Prestado',
                    id_empleado = ?,
                    fecha_retiro = DATE('now'),
                    fecha_ideal_regreso = DATE('now', '+31 days')
                WHERE id_prestamo = ?
                """;
            try (PreparedStatement ps = con().prepareStatement(sqlPrestamo)) {
                ps.setInt(1, idEmpleado);
                ps.setInt(2, idPrestamo);
                ps.executeUpdate();
            }

            String sqlEjemplar = "UPDATE ejemplares SET estado = 'Prestado' WHERE id_ejemplar = ?";
            try (PreparedStatement ps = con().prepareStatement(sqlEjemplar)) {
                ps.setInt(1, idEjemplar);
                ps.executeUpdate();
            }
        });
    }

    /**
     * El bibliotecario rechaza la solicitud.
     */
    @Override
    public void rechazarPrestamo(int idPrestamo) throws Exception {
        String sql = "UPDATE prestamos SET estado = 'Rechazado' WHERE id_prestamo = ? AND estado = 'Solicitado'";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, idPrestamo);
            int filas = ps.executeUpdate();
            if (filas == 0)
                throw new Exception("No se pudo rechazar. El préstamo no existe o ya fue procesado.");
        }
    }

    /**
     * Registrar devolución de un libro.
     */
    @Override
    public void registrarDevolucion(int idPrestamo, String estadoEjemplar) throws Exception {
        // Obtener datos del préstamo
        String sqlGet = "SELECT id_ejemplar, estado, fecha_ideal_regreso FROM prestamos WHERE id_prestamo = ?";
        int idEjemplar;
        String estadoPrestamo;
        String fechaIdeal;

        try (PreparedStatement ps = con().prepareStatement(sqlGet)) {
            ps.setInt(1, idPrestamo);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) throw new Exception("El préstamo no existe.");
            estadoPrestamo = rs.getString("estado");
            idEjemplar     = rs.getInt("id_ejemplar");
            fechaIdeal     = rs.getString("fecha_ideal_regreso");
        }

        if ("Devuelto".equals(estadoPrestamo))
            throw new Exception("Este préstamo ya fue devuelto anteriormente.");

        boolean veniaRetrasado = LocalDate.now().isAfter(LocalDate.parse(fechaIdeal));

        ConexionBD.ejecutarEnTransaccion(() -> {
            // Cerrar el préstamo
            String sqlPrestamo = """
                UPDATE prestamos
                SET estado = 'Devuelto', fecha_real_regreso = DATE('now')
                WHERE id_prestamo = ?
                """;
            try (PreparedStatement ps = con().prepareStatement(sqlPrestamo)) {
                ps.setInt(1, idPrestamo);
                ps.executeUpdate();
            }

            // Si había multas retrasadas, congelarlas en 'Pendiente'
            if (veniaRetrasado) {
                String sqlMulta = "UPDATE multas SET estado = 'Pendiente' WHERE id_prestamo = ? AND estado = 'Retrasada'";
                try (PreparedStatement ps = con().prepareStatement(sqlMulta)) {
                    ps.setInt(1, idPrestamo);
                    ps.executeUpdate();
                }
            }

            // Actualizar estado físico del ejemplar
            String sqlEjemplar = "UPDATE ejemplares SET estado = ? WHERE id_ejemplar = ?";
            try (PreparedStatement ps = con().prepareStatement(sqlEjemplar)) {
                ps.setString(1, estadoEjemplar);
                ps.setInt(2, idEjemplar);
                ps.executeUpdate();
            }

            // Si el ejemplar regresó dañado, generar multa por daño (lo que hacía el trigger)
            if ("Dañado".equals(estadoEjemplar)) {
                String sqlMultaDanio = """
                    INSERT INTO multas (id_prestamo, tipo_multa, fecha_generacion, fecha_maxima_pagar, monto, estado)
                    VALUES (?, 'Daño', DATE('now'), DATE('now', '+31 days'), 15.00, 'Pendiente')
                    """;
                try (PreparedStatement ps = con().prepareStatement(sqlMultaDanio)) {
                    ps.setInt(1, idPrestamo);
                    ps.executeUpdate();
                }
            }
        });
    }

    /**
     * Registrar pago de una multa.
     */
    @Override
    public void registrarPago(int idMulta, double monto) throws Exception {
        // Verificar que la multa exista y obtener su monto real
        String sqlGet = "SELECT estado, monto FROM multas WHERE id_multa = ?";
        try (PreparedStatement ps = con().prepareStatement(sqlGet)) {
            ps.setInt(1, idMulta);
            ResultSet rs = ps.executeQuery();
            if (!rs.next())
                throw new Exception("La multa no existe.");
            if ("Pagada".equals(rs.getString("estado")))
                throw new Exception("Esta multa ya fue pagada.");
            if (Math.abs(rs.getDouble("monto") - monto) > 0.001)
                throw new Exception("El monto ingresado ($" + monto + ") no coincide con el adeudado ($" + rs.getDouble("monto") + ").");
        }

        String sql = "UPDATE multas SET estado = 'Pagada', fecha_real_pago = DATE('now') WHERE id_multa = ?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, idMulta);
            ps.executeUpdate();
        }
    }

    /**
     * Permite a un administrador ajustar el monto o extender la fecha límite de pago
     * de una multa (ej. por un reclamo justificado). No se puede modificar una multa
     * que ya fue pagada; el resto de los campos (tipo, fechas de generación/pago real,
     * estado) son hechos históricos y solo cambian a través de los flujos dedicados
     * (registrarPago, exonerarMulta, revisarMultasVencidas).
     */
    @Override
    public void modificarMulta(int idMulta, double nuevoMonto, String nuevaFechaMaximaPagar) throws Exception {
        String estadoActual;
        String sqlGet = "SELECT estado FROM multas WHERE id_multa = ?";
        try (PreparedStatement ps = con().prepareStatement(sqlGet)) {
            ps.setInt(1, idMulta);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) throw new Exception("La multa no existe.");
            estadoActual = rs.getString("estado");
        }
        if ("Pagada".equals(estadoActual))
            throw new Exception("No se puede modificar una multa que ya fue pagada.");
        if (nuevoMonto < 0)
            throw new Exception("El monto no puede ser negativo.");

        String sql = "UPDATE multas SET monto = ?, fecha_maxima_pagar = ? WHERE id_multa = ?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setDouble(1, nuevoMonto);
            ps.setString(2, nuevaFechaMaximaPagar);
            ps.setInt(3, idMulta);
            ps.executeUpdate();
        }
    }

    /**
     * Exonera (perdona) una multa: bajo el esquema actual no existe un estado
     * "Exonerada" propio, así que se modela como pagada con monto $0 y la fecha
     * de hoy, dejando rastro de que se cerró sin cobrar.
     */
    @Override
    public void exonerarMulta(int idMulta) throws Exception {
        String estadoActual;
        String sqlGet = "SELECT estado FROM multas WHERE id_multa = ?";
        try (PreparedStatement ps = con().prepareStatement(sqlGet)) {
            ps.setInt(1, idMulta);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) throw new Exception("La multa no existe.");
            estadoActual = rs.getString("estado");
        }
        if ("Pagada".equals(estadoActual))
            throw new Exception("Esta multa ya está cerrada (pagada o exonerada).");

        String sql = "UPDATE multas SET estado = 'Pagada', monto = 0, fecha_real_pago = DATE('now') WHERE id_multa = ?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, idMulta);
            ps.executeUpdate();
        }
    }

    // ─── AUDITORÍA (reemplaza los cursores de MySQL) ──────────────────────────

    /**
     * Revisa todos los préstamos activos y genera multas por retraso.
     * Equivalente al proc_cursor_revisar_prestamos de MySQL.
     */
    @Override
    public void revisarPrestamosVencidos() throws Exception {
        String sqlVencidos = """
            SELECT id_prestamo FROM prestamos
            WHERE estado = 'Prestado' AND fecha_ideal_regreso < DATE('now')
            """;
        List<Integer> vencidos = new ArrayList<>();
        try (Statement st = con().createStatement();
             ResultSet rs = st.executeQuery(sqlVencidos)) {
            while (rs.next()) vencidos.add(rs.getInt("id_prestamo"));
        }

        for (int idPrestamo : vencidos) {
            ConexionBD.ejecutarEnTransaccion(() -> {
                // Generar multa de retraso
                String sqlMulta = """
                    INSERT INTO multas (id_prestamo, tipo_multa, fecha_generacion, fecha_maxima_pagar, monto, estado)
                    VALUES (?, 'Retraso', DATE('now'), DATE('now', '+31 days'), 10.00, 'Pendiente')
                    """;
                try (PreparedStatement ps = con().prepareStatement(sqlMulta)) {
                    ps.setInt(1, idPrestamo);
                    ps.executeUpdate();
                }
                // Marcar préstamo como retrasado
                String sqlUpdate = "UPDATE prestamos SET estado = 'Retrasado' WHERE id_prestamo = ?";
                try (PreparedStatement ps = con().prepareStatement(sqlUpdate)) {
                    ps.setInt(1, idPrestamo);
                    ps.executeUpdate();
                }
            });
        }
    }

    /**
     * Revisa multas no pagadas a tiempo y aplica recargos.
     * Equivalente al proc_cursor_revisar_multas de MySQL.
     */
    @Override
    public void revisarMultasVencidas() throws Exception {
        // Cursor 2: multas pendientes cuya fecha máxima ya venció → recargo + estado Retrasada
        String sqlPendientes = """
            SELECT id_multa FROM multas
            WHERE estado = 'Pendiente' AND fecha_maxima_pagar < DATE('now')
            """;
        List<Integer> pendientes = new ArrayList<>();
        try (Statement st = con().createStatement();
             ResultSet rs = st.executeQuery(sqlPendientes)) {
            while (rs.next()) pendientes.add(rs.getInt("id_multa"));
        }
        for (int idMulta : pendientes) {
            String sql = "UPDATE multas SET monto = monto + 10.00, estado = 'Retrasada' WHERE id_multa = ?";
            try (PreparedStatement ps = con().prepareStatement(sql)) {
                ps.setInt(1, idMulta); ps.executeUpdate();
            }
        }

        // Cursor 3: multas retrasadas con préstamo aún abierto → ejemplar perdido
        String sqlRetrasadas = """
            SELECT m.id_prestamo, p.id_ejemplar
            FROM multas m
            INNER JOIN prestamos p ON m.id_prestamo = p.id_prestamo
            WHERE m.tipo_multa = 'Retraso'
            AND m.estado = 'Retrasada'
            AND p.estado = 'Retrasado'
            """;
        try (Statement st = con().createStatement();
             ResultSet rs = st.executeQuery(sqlRetrasadas)) {
            while (rs.next()) {
                int idEjemplar = rs.getInt("id_ejemplar");
                String sql = "UPDATE ejemplares SET estado = 'Perdido' WHERE id_ejemplar = ?";
                try (PreparedStatement ps = con().prepareStatement(sql)) {
                    ps.setInt(1, idEjemplar); ps.executeUpdate();
                }
            }
        }
    }

    // ─── CONSULTAS ────────────────────────────────────────────────────────────

    @Override
    public List<Prestamo> obtenerSolicitudesPendientes() throws Exception {
        return obtenerPrestamos("WHERE pr.estado = 'Solicitado'");
    }

    @Override
    public List<Prestamo> obtenerPrestamosActivos() throws Exception {
        return obtenerPrestamos("WHERE pr.estado IN ('Prestado', 'Retrasado')");
    }

    @Override
    public List<Prestamo> obtenerPrestamosDeUsuario(int idUsuario) throws Exception {
        String sql = """
            SELECT pr.id_prestamo, pr.id_usuario, pr.id_empleado, pr.id_ejemplar, e.ISBN,
                   pr.fecha_solicitud, pr.estado, pr.fecha_retiro, pr.fecha_ideal_regreso, pr.fecha_real_regreso,
                   p.primer_nombre || ' ' || p.apellido AS nombre_usuario,
                   l.titulo AS titulo_libro
            FROM prestamos pr
            INNER JOIN personas p ON pr.id_usuario = p.id_persona
            INNER JOIN ejemplares e ON pr.id_ejemplar = e.id_ejemplar
            INNER JOIN libros l ON e.ISBN = l.ISBN
            WHERE pr.id_usuario = ?
            ORDER BY pr.id_prestamo DESC
            """;
        List<Prestamo> lista = new ArrayList<>();
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapPrestamo(rs));
        }
        return lista;
    }

    @Override
    public List<Prestamo> obtenerHistorialCompleto() throws Exception {
        return obtenerPrestamos("");
    }

    @Override
    public List<Multa> obtenerMultasDeUsuario(int idUsuario) throws Exception {
        String sql = """
            SELECT m.id_multa, m.id_prestamo, m.tipo_multa, m.fecha_generacion,
                   m.fecha_maxima_pagar, m.fecha_real_pago, m.monto, m.estado,
                   pr.id_usuario AS id_usuario
            FROM multas m
            INNER JOIN prestamos pr ON m.id_prestamo = pr.id_prestamo
            WHERE pr.id_usuario = ?
            ORDER BY m.id_multa DESC
            """;
        List<Multa> lista = new ArrayList<>();
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapMulta(rs));
        }
        return lista;
    }

    @Override
    public List<Multa> obtenerTodasLasMultas() throws Exception {
        String sql = """
            SELECT m.id_multa, m.id_prestamo, m.tipo_multa, m.fecha_generacion,
                   m.fecha_maxima_pagar, m.fecha_real_pago, m.monto, m.estado,
                   pr.id_usuario AS id_usuario
            FROM multas m
            INNER JOIN prestamos pr ON m.id_prestamo = pr.id_prestamo
            ORDER BY m.id_multa DESC
            """;
        List<Multa> lista = new ArrayList<>();
        try (Statement st = con().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapMulta(rs));
        }
        return lista;
    }

    // ─── VALIDACIONES ─────────────────────────────────────────────────────────

    @Override
    public int contarPrestamosActivos(int idUsuario) throws Exception {
        String sql = "SELECT COUNT(*) FROM prestamos WHERE id_usuario = ? AND estado IN ('Prestado', 'Solicitado')";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    @Override
    public boolean tieneRetrasados(int idUsuario) throws Exception {
        String sql = "SELECT COUNT(*) FROM prestamos WHERE id_usuario = ? AND estado = 'Retrasado'";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    @Override
    public boolean tieneMultasPendientes(int idUsuario) throws Exception {
        String sql = """
            SELECT COUNT(*) FROM multas m
            INNER JOIN prestamos pr ON m.id_prestamo = pr.id_prestamo
            WHERE pr.id_usuario = ? AND m.estado IN ('Pendiente', 'Retrasada')
            """;
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    // ─── HELPERS ──────────────────────────────────────────────────────────────

    private List<Prestamo> obtenerPrestamos(String where) throws Exception {
        String sql = """
            SELECT pr.id_prestamo, pr.id_usuario, pr.id_empleado, pr.id_ejemplar, e.ISBN,
                   pr.fecha_solicitud, pr.estado, pr.fecha_retiro, pr.fecha_ideal_regreso, pr.fecha_real_regreso,
                   p.primer_nombre || ' ' || p.apellido AS nombre_usuario,
                   l.titulo AS titulo_libro
            FROM prestamos pr
            INNER JOIN personas p ON pr.id_usuario = p.id_persona
            INNER JOIN ejemplares e ON pr.id_ejemplar = e.id_ejemplar
            INNER JOIN libros l ON e.ISBN = l.ISBN
            """ + where + " ORDER BY pr.id_prestamo DESC";
        List<Prestamo> lista = new ArrayList<>();
        try (Statement st = con().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapPrestamo(rs));
        }
        return lista;
    }

    private Prestamo mapPrestamo(ResultSet rs) throws SQLException {
        Prestamo p = new Prestamo();
        p.setIdPrestamo(rs.getInt("id_prestamo"));
        p.setIdUsuario(rs.getInt("id_usuario"));
        p.setIdEmpleado(rs.getInt("id_empleado"));
        p.setIdEjemplar(rs.getInt("id_ejemplar"));
        p.setIsbn(rs.getString("ISBN"));
        p.setFechaSolicitud(rs.getString("fecha_solicitud"));
        p.setEstado(rs.getString("estado"));
        p.setFechaRetiro(rs.getString("fecha_retiro"));
        p.setFechaIdealRegreso(rs.getString("fecha_ideal_regreso"));
        p.setFechaRealRegreso(rs.getString("fecha_real_regreso"));
        p.setNombreUsuario(rs.getString("nombre_usuario"));
        p.setTituloLibro(rs.getString("titulo_libro"));
        return p;
    }

    private Multa mapMulta(ResultSet rs) throws SQLException {
        Multa m = new Multa();
        m.setIdMulta(rs.getInt("id_multa"));
        m.setIdPrestamo(rs.getInt("id_prestamo"));
        m.setTipoMulta(rs.getString("tipo_multa"));
        m.setFechaGeneracion(rs.getString("fecha_generacion"));
        m.setFechaMaximaPagar(rs.getString("fecha_maxima_pagar"));
        m.setFechaRealPago(rs.getString("fecha_real_pago"));
        m.setMonto(rs.getDouble("monto"));
        m.setEstado(rs.getString("estado"));
        m.setIdUsuario(rs.getInt("id_usuario"));
        m.setPagoATiempo(calcularPagoATiempo(rs.getString("fecha_real_pago"), rs.getString("fecha_maxima_pagar")));
        return m;
    }

    /** "Pendiente" si aún no se pagó; si ya se pagó, "Si" o "No" según si fue antes o después del límite. */
    private String calcularPagoATiempo(String fechaRealPago, String fechaMaximaPagar) {
        if (fechaRealPago == null || fechaRealPago.isEmpty()) return "Pendiente";
        LocalDate pago = LocalDate.parse(fechaRealPago);
        LocalDate maxima = LocalDate.parse(fechaMaximaPagar);
        return !pago.isAfter(maxima) ? "Si" : "No";
    }
}