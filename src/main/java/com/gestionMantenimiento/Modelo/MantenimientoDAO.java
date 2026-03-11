package com.gestionMantenimiento.Modelo;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class MantenimientoDAO {

    private static final String ARCHIVO = "mantenimientos.csv";

    public static ArrayList<Mantenimiento> cargarTodos() {
        ArrayList<Mantenimiento> lista = new ArrayList<>();
        File archivo = new File(ARCHIVO);

        if (!archivo.exists()) {
            // Datos de ejemplo con fechaHoraInicio ya asignada
            LocalDateTime ahora = LocalDateTime.now();
            lista.add(new Mantenimiento(101, "26/04/2024", "Rodríguez, F.", "Plomería",  "Alta",  "Pendiente",  "",         "Fuga de agua en baño.",               "Torre B, Apto 102", ahora.minusDays(3), null));
            lista.add(new Mantenimiento(102, "25/04/2024", "Pérez, L.",     "Eléctrica", "Media", "En proceso", "López",    "Falla en el sistema de iluminación.", "Torre A, Apto 301", ahora.minusDays(2), null));
            lista.add(new Mantenimiento(103, "24/04/2024", "Gómez, A.",     "Ascensor",  "Alta",  "Finalizado", "Martínez", "Ascensor no responde en piso 3.",     "Torre A",            ahora.minusDays(5), ahora.minusDays(1)));
            lista.add(new Mantenimiento(104, "24/04/2024", "Martínez, J.",  "Plomería",  "Baja",  "Pendiente",  "",         "Goteo leve en lavamanos.",            "Torre C, Apto 201", ahora.minusHours(6), null));
            guardarTodos(lista);
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
            System.err.println("Error al leer: " + e.getMessage());
        }

        return lista;
    }

    public static void guardarTodos(ArrayList<Mantenimiento> lista) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO))) {
            for (Mantenimiento m : lista) {
                pw.println(m.toCSV());
            }
        } catch (IOException e) {
            System.err.println("Error al guardar: " + e.getMessage());
        }
    }
}