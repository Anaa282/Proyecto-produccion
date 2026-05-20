package com.gestionMantenimiento.Util;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class ConexionMongo {

    private static MongoClient clienteActual;
    private static MongoDatabase dbActual;


    private static final String URI      = "mongodb://172.30.16.49:27017";
    private static final String DB_NAME  = "mantenimiento";

    public static MongoDatabase conectar() {
        try {
            clienteActual = MongoClients.create(URI);
            dbActual = clienteActual.getDatabase(DB_NAME);
            // ping para verificar conexión
            dbActual.runCommand(new org.bson.Document("ping", 1));
            System.out.println("Conectado a MongoDB: " + DB_NAME);
            return dbActual;
        } catch (Exception e) {
            System.out.println("Error MongoDB: " + e.getMessage());
            return null;
        }
    }

    public static MongoDatabase getDB() {
        if (dbActual == null) {
            System.out.println("MongoDB no conectado. Llama a conectar() primero.");
        }
        return dbActual;
    }

    public static void cerrar() {
        if (clienteActual != null) {
            clienteActual.close();
            clienteActual = null;
            dbActual = null;
            System.out.println("Conexión MongoDB cerrada.");
        }
    }
}