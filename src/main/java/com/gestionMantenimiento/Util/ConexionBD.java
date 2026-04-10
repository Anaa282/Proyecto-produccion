package com.gestionMantenimiento.Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String URL =
            "jdbc:mysql://172.30.16.36:3306/mantenimiento?useSSL=false&serverTimezone=UTC";

    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection conectar() {

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);

            System.out.println("Conectado a la BD mantenimiento");

            return conn;

        } catch (ClassNotFoundException e) {

            System.out.println("Driver MySQL no encontrado");
            e.printStackTrace();

        } catch (SQLException e) {

            System.out.println("Error de conexión a la BD");
            e.printStackTrace();
        }

        return null;
    }
}