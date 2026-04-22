package com.gestionMantenimiento.Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ConexionBD {

    private static Connection conexionActual;

    // 🔹 MYSQL
    public static Connection conectarMySQL() {
        try {
            String url = "jdbc:mysql://172.30.16.36:3306/mantenimiento?useSSL=false&serverTimezone=UTC";
            String user = "ammican73";
            String password = "67001373";

            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Conectado a MySQL");
            return conn;

        } catch (SQLException e) {
            System.out.println("Error MySQL: " + e.getMessage());
            return null;
        }
    }

    // 🔹 SQLITE
    public static Connection conectarSQLite() {
        try {
            String url = "jdbc:sqlite:mantenimiento.db";
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            return null;
        }
    }

    // 🔹 SELECTOR


    public static Connection conectar(String tipo) {
        if ("SQLite".equalsIgnoreCase(tipo)) {
            conexionActual = conectarSQLite();
        } else {
            conexionActual = conectarMySQL();
        }
        return conexionActual;
    }

    public static Connection getConexion() {
        return conexionActual;
    }
}