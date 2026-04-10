package com.gestionMantenimiento.Controller;

import com.gestionMantenimiento.Modelo.Mantenimiento;
import com.gestionMantenimiento.Modelo.MantenimientoDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class HistorialController {

    @FXML private TableView<Mantenimiento> tablaHistorial;
    @FXML private TableColumn<Mantenimiento, String> colId;
    @FXML private TableColumn<Mantenimiento, String> colFecha;
    @FXML private TableColumn<Mantenimiento, String> colResidente;
    @FXML private TableColumn<Mantenimiento, String> colCategoria;
    @FXML private TableColumn<Mantenimiento, String> colTecnico;
    @FXML private TableColumn<Mantenimiento, String> colTiempo;

    @FXML private Label lblTotalFinalizados;

    @FXML
    public void initialize() {

        colId.setCellValueFactory(d ->
                new SimpleStringProperty(String.valueOf(d.getValue().getId()))
        );

        colFecha.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getFecha())
        );

        colResidente.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getResidente())
        );

        colCategoria.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getCategoria())
        );

        colTecnico.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getTecnico())
        );

        colTiempo.setCellValueFactory(d ->
                new SimpleStringProperty(
                        d.getValue().getTiempoSolucionHoras() + " horas"
                )
        );

        cargarDatos();
    }

    private void cargarDatos() {

        MantenimientoDAO dao = new MantenimientoDAO();

        ArrayList<Mantenimiento> lista = new ArrayList<>(dao.obtenerMantenimientos());

        ObservableList<Mantenimiento> finalizados = FXCollections.observableArrayList(
                lista.stream()
                        .filter(m -> m.getEstado().equalsIgnoreCase("Finalizado"))
                        .collect(Collectors.toList())
        );

        tablaHistorial.setItems(finalizados);

        lblTotalFinalizados.setText(
                "Total finalizados: " + finalizados.size()
        );
    }

    @FXML
    public void actualizar() {
        cargarDatos();
    }
}