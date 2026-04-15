package com.gestionMantenimiento.Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConexionBD {

    private static final String URL =
            "jdbc:mysql://172.30.16.52:3306/mantenimiento?useSSL=false&serverTimezone=UTC";

    private static final String USER = "ammican73";
    private static final String PASSWORD = "67001373"; // Cambiar con la contraseña real

    private static final Logger logger = Logger.getLogger(ConexionBD.class.getName());
    private static final int MAX_INTENTOS = 3;

    public static Connection conectar() {
        int intentos = 0;
        SQLException ultimaExcepcion = null;

        while (intentos < MAX_INTENTOS) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");

                Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);

                logger.info("Conectado exitosamente a la BD mantenimiento en " + URL);
                return conn;

            } catch (ClassNotFoundException e) {
                logger.severe("Driver MySQL no encontrado: " + e.getMessage());
                return null;

            } catch (SQLException e) {
                intentos++;
                ultimaExcepcion = e;
                logger.log(Level.WARNING,
                        "Intento " + intentos + " - Error de conexión a la BD: " + e.getMessage());

                if (intentos < MAX_INTENTOS) {
                    try {
                        Thread.sleep(2000); // Esperar 2 segundos antes de reintentar
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        if (ultimaExcepcion != null) {
            logger.severe("No se pudo conectar después de " + MAX_INTENTOS + " intentos");
            ultimaExcepcion.printStackTrace();
        }

        return null;
    }
}