package com.mycompany.DAO;



import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public class ConexionBD {

    private static final String URL = "jdbc:sqlite:biblioteca.db";
    private static Connection instancia = null;

    public static Connection getConexion() throws SQLException {
        try {
            if (instancia == null || instancia.isClosed()) {
                Class.forName("org.sqlite.JDBC");
                instancia = DriverManager.getConnection(URL);
                
                // Activar claves foráneas inmediatamente
                instancia.createStatement().execute("PRAGMA foreign_keys = ON");
                
                // Inicializar base de datos leyendo tu archivo SQL de resources
                cargarEsquemaDesdeResources(instancia);
            }
            return instancia;
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver SQLite no encontrado.", e);
        }
    }

    private static void cargarEsquemaDesdeResources(Connection conexion) {
        // Buscamos el archivo en src/main/resources/
        // Si lo pusiste directo en resources va así. Si hiciste carpeta "db", cambia a "/db/BibliotecaSquema.sql"
        String rutaScript = "/BibliotecaSquema.sql"; 
        
        try (InputStream is = ConexionBD.class.getResourceAsStream(rutaScript)) {
            if (is == null) {
                System.err.println("No se encontró el archivo SQL en la ruta: " + rutaScript);
                return;
            }

            // Leer todo el archivo SQL y guardarlo en un solo String
            String scriptSql;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
                scriptSql = reader.lines().collect(Collectors.joining("\n"));
            }

            // SQLite JDBC permite ejecutar múltiples sentencias separadas por punto y coma (;) 
            // siempre y cuando usemos un Statement común
            try (Statement stmt = conexion.createStatement()) {
                // SQLite ejecuta bloque por bloque separado por ;
                for (String sql : scriptSql.split(";")) {
                    String sentenciaLimpia = sql.trim();
                    if (!sentenciaLimpia.isEmpty()) {
                        stmt.execute(sentenciaLimpia);
                    }
                }
                System.out.println("[SQLite] ¡Base de datos e inserciones iniciales cargadas con éxito desde el script!");
            }

        } catch (Exception e) {
            System.err.println("Error al leer o ejecutar el script de inicialización: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void cerrarConexion() {
        try {
            if (instancia != null && !instancia.isClosed()) {
                instancia.close();
                instancia = null;
            }
        } catch (SQLException ignored) {}
    }

    // ─── TRANSACCIONES ──────────────────────────────────────────────────────

    /**
     * Operación que agrupa una o más sentencias SQL que deben aplicarse
     * todas o ninguna (ej. actualizar un préstamo Y su ejemplar asociado).
     */
    @FunctionalInterface
    public interface OperacionTransaccional {
        void ejecutar() throws Exception;
    }

    /**
     * Ejecuta {@code operacion} dentro de una transacción real: desactiva el
     * autocommit, corre todas las sentencias que la operación contenga (sin
     * importar en qué DAO estén, ya que todos comparten la misma conexión
     * singleton) y hace commit solo si todo terminó bien. Si algo falla, se
     * revierte por completo evitando que la base de datos quede en un estado
     * a medias (ej. un préstamo marcado "Prestado" con su ejemplar todavía
     * "Disponible").
     */
    public static void ejecutarEnTransaccion(OperacionTransaccional operacion) throws Exception {
        Connection con = getConexion();
        boolean autoCommitOriginal = con.getAutoCommit();
        try {
            con.setAutoCommit(false);
            operacion.ejecutar();
            con.commit();
        } catch (Exception e) {
            try { con.rollback(); } catch (SQLException ignored) {}
            throw e;
        } finally {
            try { con.setAutoCommit(autoCommitOriginal); } catch (SQLException ignored) {}
        }
    }
}