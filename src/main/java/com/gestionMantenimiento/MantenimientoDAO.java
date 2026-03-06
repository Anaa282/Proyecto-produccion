package com.gestionMantenimiento;

import java.io.*;
import java.util.ArrayList;

public class MantenimientoDAO {

    // Ruta del archivo — se guarda en la carpeta del proyecto
    private static final String ARCHIVO = "mantenimientos.csv";

    // Lee el CSV y devuelve un ArrayList con todos los registros
    public static ArrayList<Mantenimiento> cargarTodos() {
        ArrayList<Mantenimiento> lista = new ArrayList<>();
        File archivo = new File(ARCHIVO);

        // Si el archivo no existe, devuelve datos de ejemplo
        if (!archivo.exists()) {
            lista.add(new Mantenimiento(101, "26/04/2024", "Rodríguez, F.", "Plomería",  "Alta",  "Pendiente",  "",         "Fuga de agua en baño.",               "Torre B, Apto 102"));
            lista.add(new Mantenimiento(102, "25/04/2024", "Pérez, L.",     "Eléctrica", "Media", "En proceso", "López",    "Falla en el sistema de iluminación.", "Torre A, Apto 301"));
            lista.add(new Mantenimiento(103, "24/04/2024", "Gómez, A.",     "Ascensor",  "Alta",  "Finalizado", "Martínez", "Ascensor no responde en piso 3.",     "Torre A"));
            lista.add(new Mantenimiento(104, "24/04/2024", "Martínez, J.",  "Plomería",  "Baja",  "Pendiente",  "",         "Goteo leve en lavamanos.",            "Torre C, Apto 201"));
            guardarTodos(lista); // guarda los datos de ejemplo al archivo
            return lista;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    lista.add(Mantenimiento.fromCSV(linea));
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }

        return lista;
    }

    // Guarda toda la lista en el CSV (sobreescribe el archivo)
    public static void guardarTodos(ArrayList<Mantenimiento> lista) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO))) {
            for (Mantenimiento m : lista) {
                pw.println(m.toCSV());
            }
        } catch (IOException e) {
            System.err.println("Error al guardar el archivo: " + e.getMessage());
        }
    }
}