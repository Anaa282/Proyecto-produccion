package com.gestionMantenimiento.Controller;

import com.gestionMantenimiento.Modelo.Mantenimiento;
import com.gestionMantenimiento.Modelo.MantenimientoDAO;
import com.gestionMantenimiento.Modelo.Tecnico;
import com.gestionMantenimiento.Modelo.TecnicoDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;

public class TecnicosController {

    @FXML private TableView<String[]> tablaTecnicos;
    @FXML private TableColumn<String[], String> colNombre;
    @FXML private TableColumn<String[], String> colEspecialidad;
    @FXML private TableColumn<String[], String> colEstado;
    @FXML private TableColumn<String[], String> colPendientes;
    @FXML private TableColumn<String[], String> colEnProceso;
    @FXML private TableColumn<String[], String> colFinalizados;
    @FXML private TableColumn<String[], String> colTotal;

    @FXML
    public void initialize() {
        // Índices del array: 0=nombre, 1=especialidad, 2=estado, 3=pendientes, 4=enProceso, 5=finalizados, 6=total
        colNombre      .setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[0]));
        colEspecialidad.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[1]));
        colEstado      .setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[2]));
        colPendientes  .setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[3]));
        colEnProceso   .setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[4]));
        colFinalizados .setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[5]));
        colTotal       .setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[6]));

        cargarDatos();
    }

    private void cargarDatos() {

        // ── 1. Cargar técnicos desde la BD relacional (MySQL / SQLite) ──────────
        TecnicoDAO tecnicoDAO = new TecnicoDAO();
        List<Tecnico> tecnicos = tecnicoDAO.obtenerTecnicos();

        // ── 2. Cargar órdenes de mantenimiento ───────────────────────────────────
        MantenimientoDAO mantenimientoDAO = new MantenimientoDAO();
        List<Mantenimiento> mantenimientos = new ArrayList<>(mantenimientoDAO.obtenerMantenimientos());

        // ── 3. Construir filas cruzando técnico ↔ mantenimiento ─────────────────
        List<String[]> filas = new ArrayList<>();

        for (Tecnico tec : tecnicos) {

            // El campo "tecnico" en mantenimiento puede contener nombre completo o solo apellido;
            // comparamos contra nombre completo, nombre solo y apellido solo para mayor tolerancia.
            String nombreCompleto = tec.getNombreCompleto().toLowerCase();
            String nombre         = tec.getNombre().toLowerCase();
            String apellido       = tec.getApellido().toLowerCase();

            long pendientes = mantenimientos.stream()
                    .filter(m -> coincideTecnico(m.getTecnico(), nombreCompleto, nombre, apellido)
                            && "Pendiente".equalsIgnoreCase(m.getEstado()))
                    .count();

            long enProceso = mantenimientos.stream()
                    .filter(m -> coincideTecnico(m.getTecnico(), nombreCompleto, nombre, apellido)
                            && "En proceso".equalsIgnoreCase(m.getEstado()))
                    .count();

            long finalizados = mantenimientos.stream()
                    .filter(m -> coincideTecnico(m.getTecnico(), nombreCompleto, nombre, apellido)
                            && "Finalizado".equalsIgnoreCase(m.getEstado()))
                    .count();

            long total = pendientes + enProceso + finalizados;

            filas.add(new String[]{
                    tec.getNombreCompleto(),
                    tec.getEspecialidad() != null ? tec.getEspecialidad() : "",
                    tec.getEstado()       != null ? tec.getEstado()       : "",
                    String.valueOf(pendientes),
                    String.valueOf(enProceso),
                    String.valueOf(finalizados),
                    String.valueOf(total)
            });
        }

        tablaTecnicos.setItems(FXCollections.observableArrayList(filas));
        System.out.println("Tabla de técnicos cargada con " + filas.size() + " filas.");
    }

    /**
     * Compara el campo "tecnico" de una orden contra las distintas formas
     * en que puede aparecer el nombre (completo, solo nombre, solo apellido).
     */
    private boolean coincideTecnico(String campoTecnico,
                                    String nombreCompleto,
                                    String nombre,
                                    String apellido) {
        if (campoTecnico == null) return false;
        String ct = campoTecnico.toLowerCase().trim();
        return ct.equals(nombreCompleto)
                || ct.equals(nombre)
                || ct.equals(apellido)
                || ct.contains(apellido)
                || ct.contains(nombre);
    }

    @FXML
    public void actualizar() {
        cargarDatos();
    }
}