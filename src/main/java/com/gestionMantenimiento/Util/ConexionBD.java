package com.gestionMantenimiento.Util;

import java.io.File;
import java.sql.*;
import com.gestionMantenimiento.Util.ConexionMongo;

public class ConexionBD {

    private static Connection conexionActual;
    private static String tipoActual;


    // 📁 Ruta local de SQLite en Windows
    private static final String SQLITE_PATH = "C:\\Users\\sala7\\Documents\\mantenimiento.db";

    public static String getTipo() {
        return tipoActual;
    }


    // 🔹 MYSQL
    public static Connection conectarMySQL() {
        try {
            String url = "jdbc:mysql://172.30.16.49:3306/mantenimiento?useSSL=false&serverTimezone=UTC";
            String user = "ammican73";
            String password = "67001373";

            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println(" Conectado a MySQL");
            return conn;

        } catch (SQLException e) {
            System.out.println(" Error MySQL: " + e.getMessage());
            return null;
        }
    }

    // 🔹 SQLITE LOCAL
    public static Connection conectarSQLite() {
        try {
            String url = "jdbc:sqlite:" + SQLITE_PATH;
            Connection conn = DriverManager.getConnection(url);

            System.out.println(" Conectado a SQLite");
            System.out.println(" Ubicación: " + SQLITE_PATH);

            // Crear tabla si no existe
            inicializarBaseDatos(conn);

            return conn;

        } catch (SQLException e) {
            System.out.println(" Error SQLite: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

     // Crear tabla automáticamente
    private static void inicializarBaseDatos(Connection conn) {
        // Tabla de mantenimiento
        String sqlMantenimiento = "CREATE TABLE IF NOT EXISTS mantenimiento (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "fecha TEXT NOT NULL," +
                "residente TEXT NOT NULL," +
                "categoria TEXT NOT NULL," +
                "prioridad TEXT NOT NULL," +
                "estado TEXT NOT NULL," +
                "tecnico TEXT NOT NULL," +
                "descripcion TEXT," +
                "ubicacion TEXT," +
                "fecha_hora_inicio TIMESTAMP," +
                "fecha_hora_fin TIMESTAMP," +
                "comentarios TEXT" +
                ")";

        // Tabla de auditoría
        String sqlAuditoria = "CREATE TABLE IF NOT EXISTS auditoria (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "mantenimiento_id INTEGER NOT NULL," +
                "tipo_evento TEXT NOT NULL," +
                "fecha_evento TIMESTAMP NOT NULL," +
                "fecha TEXT," +
                "residente TEXT," +
                "categoria TEXT," +
                "prioridad TEXT," +
                "estado TEXT," +
                "tecnico TEXT," +
                "descripcion TEXT," +
                "ubicacion TEXT," +
                "comentarios TEXT," +
                "fecha_hora_inicio TIMESTAMP," +
                "fecha_hora_fin TIMESTAMP," +
                "horas_resolucion INTEGER," +
                "fecha_finalizacion TIMESTAMP" +
                ")";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sqlMantenimiento);
            System.out.println(" Tabla 'mantenimiento' lista");
            
            stmt.execute(sqlAuditoria);
            System.out.println(" Tabla 'auditoria' lista");

        } catch (SQLException e) {
            System.out.println(" Error creando tablas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 🔹 SELECTOR
    public static Connection conectar(String tipo) {
        tipoActual = tipo;

        if (conexionActual != null) {
            try { conexionActual.close(); } catch (SQLException e) { e.printStackTrace(); }
        }

        // ✅ Solo un bloque de lógica
        if ("MongoDB".equalsIgnoreCase(tipo)) {
            ConexionMongo.conectar();
            conexionActual = null;
        } else if ("SQLite".equalsIgnoreCase(tipo)) {
            conexionActual = conectarSQLite();
        } else {
            conexionActual = conectarMySQL();
        }

        return conexionActual;
    }

    public static Connection getConexion() {
        if (conexionActual == null) {
            System.out.println(" ADVERTENCIA: Conexión es NULL. Conecta primero.");
        }
        return conexionActual;
    }

    public static void cerrarConexion() {
        if (conexionActual != null) {
            try {
                conexionActual.close();
                System.out.println(" Conexión cerrada");
            } catch (SQLException e) {
                System.out.println(" Error cerrando conexión: " + e.getMessage());
            }
        }
    }
    public static Connection nuevaConexion() {
        if ("SQLite".equalsIgnoreCase(tipoActual)) {
            return conectarSQLite();
        } else {
            return conectarMySQL();
        }
    }

}
