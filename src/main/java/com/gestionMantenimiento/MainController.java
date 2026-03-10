package com.gestionMantenimiento;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class MainController {

    // Tabla
    @FXML private TableView<Mantenimiento> tablaMantenimientos;
    @FXML private TableColumn<Mantenimiento, String> colId;
    @FXML private TableColumn<Mantenimiento, String> colFecha;
    @FXML private TableColumn<Mantenimiento, String> colResidente;
    @FXML private TableColumn<Mantenimiento, String> colCategoria;
    @FXML private TableColumn<Mantenimiento, String> colPrioridad;
    @FXML private TableColumn<Mantenimiento, String> colEstado;
    @FXML private TableColumn<Mantenimiento, String> colTecnico;

    // Panel detalles
    @FXML private Label lblId;
    @FXML private Label lblFecha;
    @FXML private Label lblResidente;
    @FXML private Label lblUbicacion;
    @FXML private Label lblTiempo;
    @FXML private TextArea txtDescripcion;
    @FXML private ComboBox<String> cbEstado;
    @FXML private ComboBox<String> cbTecnico;
    @FXML private TextField txtBuscar;

    // Panel nuevo registro
    @FXML private VBox panelNuevo;
    @FXML private TextField nResidente;
    @FXML private TextField nUbicacion;
    @FXML private TextField nFecha;
    @FXML private ComboBox<String> nCategoria;
    @FXML private ComboBox<String> nPrioridad;
    @FXML private ComboBox<String> nEstado;
    @FXML private ComboBox<String> nTecnico;
    @FXML private TextArea nDescripcion;
    @FXML private Label lblError;

    // Datos
    private ArrayList<Mantenimiento> listaMantenimientos;
    private ObservableList<Mantenimiento> observableList;

    @FXML
    public void initialize() {
        cbEstado.setItems(FXCollections.observableArrayList("Pendiente", "En proceso", "Finalizado"));
        cbTecnico.setItems(FXCollections.observableArrayList("López", "Martínez", "García"));
        nCategoria.setItems(FXCollections.observableArrayList("Plomeria", "Electrica", "Ascensor", "Pintura", "Otro"));
        nPrioridad.setItems(FXCollections.observableArrayList("Alta", "Media", "Baja"));
        nEstado.setItems(FXCollections.observableArrayList("Pendiente", "En proceso", "Finalizado"));
        nTecnico.setItems(FXCollections.observableArrayList("López", "Martínez", "García"));

        colId.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().getId())));
        colFecha.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getFecha()));
        colResidente.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getResidente()));
        colCategoria.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getCategoria()));
        colPrioridad.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getPrioridad()));
        colEstado.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getEstado()));
        colTecnico.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getTecnico()));

        listaMantenimientos = MantenimientoDAO.cargarTodos();
        observableList = FXCollections.observableArrayList(listaMantenimientos);
        tablaMantenimientos.setItems(observableList);

        tablaMantenimientos.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> mostrarDetalles(newVal));
    }

    private void mostrarDetalles(Mantenimiento m) {
        if (m != null) {
            lblId.setText("ID: " + m.getId());
            lblFecha.setText("Fecha: " + m.getFecha());
            lblResidente.setText(m.getResidente());
            lblUbicacion.setText(m.getUbicacion());
            txtDescripcion.setText(m.getDescripcion());
            cbEstado.setValue(m.getEstado());
            cbTecnico.setValue(m.getTecnico());
            // Mostrar tiempo de resolución
            lblTiempo.setText("⏱ Tiempo: " + m.getTiempoResolucion());
        }
    }

    @FXML
    public void guardarCambios() {
        Mantenimiento seleccionado = tablaMantenimientos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            String estadoAnterior = seleccionado.getEstado();
            String estadoNuevo = cbEstado.getValue();

            seleccionado.setEstado(estadoNuevo);
            seleccionado.setTecnico(cbTecnico.getValue());

            // Si acaba de marcarse como Finalizado, registrar fecha/hora de fin
            if (!estadoAnterior.equals("Finalizado") && estadoNuevo.equals("Finalizado")) {
                seleccionado.setFechaHoraFin(LocalDateTime.now());
            }

            // Si se revierte de Finalizado, borrar la fecha de fin
            if (estadoAnterior.equals("Finalizado") && !estadoNuevo.equals("Finalizado")) {
                seleccionado.setFechaHoraFin(null);
            }

            MantenimientoDAO.guardarTodos(listaMantenimientos);
            tablaMantenimientos.refresh();

            // Actualizar el label de tiempo en pantalla
            lblTiempo.setText("⏱ Tiempo: " + seleccionado.getTiempoResolucion());
            System.out.println("Guardado: " + seleccionado);
        }
    }

    @FXML
    public void mostrarPanelNuevo() {
        limpiarFormulario();
        panelNuevo.setVisible(true);
        panelNuevo.setManaged(true);
    }

    @FXML
    public void ocultarPanelNuevo() {
        panelNuevo.setVisible(false);
        panelNuevo.setManaged(false);
        limpiarFormulario();
    }

    @FXML
    public void guardarNuevo() {
        if (nResidente.getText().trim().isEmpty() ||
                nUbicacion.getText().trim().isEmpty() ||
                nFecha.getText().trim().isEmpty() ||
                nCategoria.getValue() == null ||
                nPrioridad.getValue() == null ||
                nEstado.getValue() == null) {
            lblError.setText("* Completa todos los campos obligatorios");
            return;
        }

        lblError.setText("");

        int nuevoId = listaMantenimientos.isEmpty() ? 1 :
                listaMantenimientos.stream().mapToInt(Mantenimiento::getId).max().getAsInt() + 1;

        // Registrar fecha/hora de inicio al crear
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime fin = nEstado.getValue().equals("Finalizado") ? ahora : null;

        Mantenimiento nuevo = new Mantenimiento(
                nuevoId,
                nFecha.getText().trim(),
                nResidente.getText().trim(),
                nCategoria.getValue(),
                nPrioridad.getValue(),
                nEstado.getValue(),
                nTecnico.getValue() != null ? nTecnico.getValue() : "",
                nDescripcion.getText().trim(),
                nUbicacion.getText().trim(),
                ahora,
                fin
        );

        listaMantenimientos.add(nuevo);
        observableList.add(nuevo);
        MantenimientoDAO.guardarTodos(listaMantenimientos);
        ocultarPanelNuevo();
        tablaMantenimientos.getSelectionModel().selectLast();
        System.out.println("Nuevo registro guardado: " + nuevo);
    }

    @FXML
    public void actualizar() {
        listaMantenimientos = MantenimientoDAO.cargarTodos();
        observableList.setAll(listaMantenimientos);
        tablaMantenimientos.refresh();
    }

    private void limpiarFormulario() {
        nResidente.clear();
        nUbicacion.clear();
        nFecha.clear();
        nDescripcion.clear();
        nCategoria.setValue(null);
        nPrioridad.setValue(null);
        nEstado.setValue(null);
        nTecnico.setValue(null);
        lblError.setText("");
    }
}