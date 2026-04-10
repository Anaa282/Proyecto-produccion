package com.gestionMantenimiento.Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String URL = "jdbc:mysql://localhost:3306/gestion_mantenimiento";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection conectar() {

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            return DriverManager.getConnection(URL, USER, PASSWORD);

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
