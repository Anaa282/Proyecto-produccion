package com.gestionMantenimiento.Controller;

import com.gestionMantenimiento.Modelo.Mantenimiento;
import com.gestionMantenimiento.Modelo.MantenimientoDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TecnicosController {

    @FXML private TableView<String[]> tablaTecnicos;
    @FXML private TableColumn<String[], String> colNombre;
    @FXML private TableColumn<String[], String> colPendientes;
    @FXML private TableColumn<String[], String> colEnProceso;
    @FXML private TableColumn<String[], String> colFinalizados;
    @FXML private TableColumn<String[], String> colTotal;

    private static final List<String> TECNICOS =
            Arrays.asList("Lopez", "Martinez", "Garcia");

    @FXML
    public void initialize() {

        colNombre.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[0]));
        colPendientes.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[1]));
        colEnProceso.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[2]));
        colFinalizados.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[3]));
        colTotal.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[4]));

        cargarDatos();
    }

    private void cargarDatos() {

        MantenimientoDAO dao = new MantenimientoDAO();
        ArrayList<Mantenimiento> lista = new ArrayList<>(dao.obtenerMantenimientos());

        List<String[]> filas = new ArrayList<>();

        for (String tec : TECNICOS) {

            long pendientes = lista.stream()
                    .filter(m -> tec.equalsIgnoreCase(m.getTecnico()) &&
                            m.getEstado().equalsIgnoreCase("Pendiente"))
                    .count();

            long enProceso = lista.stream()
                    .filter(m -> tec.equalsIgnoreCase(m.getTecnico()) &&
                            m.getEstado().equalsIgnoreCase("En proceso"))
                    .count();

            long finalizados = lista.stream()
                    .filter(m -> tec.equalsIgnoreCase(m.getTecnico()) &&
                            m.getEstado().equalsIgnoreCase("Finalizado"))
                    .count();

            long total = pendientes + enProceso + finalizados;

            filas.add(new String[]{
                    tec,
                    String.valueOf(pendientes),
                    String.valueOf(enProceso),
                    String.valueOf(finalizados),
                    String.valueOf(total)
            });
        }

        tablaTecnicos.setItems(FXCollections.observableArrayList(filas));
    }

    @FXML
    public void actualizar() {
        cargarDatos();
    }
}