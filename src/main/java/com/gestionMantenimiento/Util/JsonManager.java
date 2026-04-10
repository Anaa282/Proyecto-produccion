package com.gestionMantenimiento.Util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestionMantenimiento.Modelo.Mantenimiento;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JsonManager {

    private static final String FILE_PATH =
            "src/main/resources/com.gestionMantenimiento/mantenimiento.json";

    private static ObjectMapper mapper = new ObjectMapper();

    public static List<Mantenimiento> leerMantenimientos() {

        try {

            File file = new File(FILE_PATH);

            if (!file.exists() || file.length() == 0) {
                return new ArrayList<>();
            }

            return mapper.readValue(
                    file,
                    new TypeReference<List<Mantenimiento>>() {}
            );

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    public static void guardarMantenimientos(List<Mantenimiento> lista) {

        try {

            File file = new File(FILE_PATH);

            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(file, lista);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}