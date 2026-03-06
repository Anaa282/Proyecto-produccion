package com.gestionMantenimiento;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class MainController {

    // ===== Tabla =====
    @FXML private TableView<Mantenimiento> tablaMantenimientos;
    @FXML private TableColumn<Mantenimiento, String> colId;
    @FXML private TableColumn<Mantenimiento, String> colFecha;
    @FXML private TableColumn<Mantenimiento, String> colResidente;
    @FXML private TableColumn<Mantenimiento, String> colCategoria;
    @FXML private TableColumn<Mantenimiento, String> colPrioridad;
    @FXML private TableColumn<Mantenimiento, String> colEstado;
    @FXML private TableColumn<Mantenimiento, String> colTecnico;

    // ===== Panel detalles =====
    @FXML private Label lblId;
    @FXML private Label lblFecha;
    @FXML private Label lblResidente;
    @FXML private Label lblUbicacion;
    @FXML private TextArea txtDescripcion;
    @FXML private ComboBox<String> cbEstado;
    @FXML private ComboBox<String> cbTecnico;
    @FXML private TextField txtBuscar;

    // ===== Panel nuevo registro =====
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

    // ===== Datos =====
    private ArrayList<Mantenimiento> listaMantenimientos;
    private ObservableList<Mantenimiento> observableList;

    @FXML
    public void initialize() {
        // ComboBox del panel de detalles
        cbEstado.setItems(FXCollections.observableArrayList("Pendiente", "En proceso", "Finalizado"));
        cbTecnico.setItems(FXCollections.observableArrayList("López", "Martínez", "García"));

        // ComboBox del panel nuevo
        nCategoria.setItems(FXCollections.observableArrayList("Plomeria", "Electrica", "Ascensor", "Pintura", "Otro"));
        nPrioridad.setItems(FXCollections.observableArrayList("Alta", "Media", "Baja"));
        nEstado.setItems(FXCollections.observableArrayList("Pendiente", "En proceso", "Finalizado"));
        nTecnico.setItems(FXCollections.observableArrayList("López", "Martínez", "García"));

        // Configurar columnas
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

        // Cargar datos desde CSV
        listaMantenimientos = MantenimientoDAO.cargarTodos();
        observableList = FXCollections.observableArrayList(listaMantenimientos);
        tablaMantenimientos.setItems(observableList);

        // Al seleccionar fila mostrar detalles
        tablaMantenimientos.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> mostrarDetalles(newVal));
    }

    // Muestra detalles del registro seleccionado
    private void mostrarDetalles(Mantenimiento m) {
        if (m != null) {
            lblId.setText("ID: " + m.getId());
            lblFecha.setText("Fecha: " + m.getFecha());
            lblResidente.setText(m.getResidente());
            lblUbicacion.setText(m.getUbicacion());
            txtDescripcion.setText(m.getDescripcion());
            cbEstado.setValue(m.getEstado());
            cbTecnico.setValue(m.getTecnico());
        }
    }

    // Mostrar panel de nuevo registro
    @FXML
    public void mostrarPanelNuevo() {
        limpiarFormulario();
        panelNuevo.setVisible(true);
        panelNuevo.setManaged(true);
    }

    // Ocultar panel de nuevo registro
    @FXML
    public void ocultarPanelNuevo() {
        panelNuevo.setVisible(false);
        panelNuevo.setManaged(false);
        limpiarFormulario();
    }

    // Guardar nuevo registro
    @FXML
    public void guardarNuevo() {
        // Validar campos obligatorios
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

        // Generar nuevo ID
        int nuevoId = listaMantenimientos.isEmpty() ? 1 :
                listaMantenimientos.stream().mapToInt(Mantenimiento::getId).max().getAsInt() + 1;

        // Crear nuevo mantenimiento
        Mantenimiento nuevo = new Mantenimiento(
                nuevoId,
                nFecha.getText().trim(),
                nResidente.getText().trim(),
                nCategoria.getValue(),
                nPrioridad.getValue(),
                nEstado.getValue(),
                nTecnico.getValue() != null ? nTecnico.getValue() : "",
                nDescripcion.getText().trim(),
                nUbicacion.getText().trim()
        );

        // Agregar al ArrayList y al CSV
        listaMantenimientos.add(nuevo);
        observableList.add(nuevo);
        MantenimientoDAO.guardarTodos(listaMantenimientos);

        // Cerrar panel y seleccionar el nuevo registro
        ocultarPanelNuevo();
        tablaMantenimientos.getSelectionModel().selectLast();

        System.out.println("Nuevo registro guardado: " + nuevo);
    }

    // Guardar cambios en registro seleccionado
    @FXML
    public void guardarCambios() {
        Mantenimiento seleccionado = tablaMantenimientos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            seleccionado.setEstado(cbEstado.getValue());
            seleccionado.setTecnico(cbTecnico.getValue());
            MantenimientoDAO.guardarTodos(listaMantenimientos);
            tablaMantenimientos.refresh();
            System.out.println("Guardado: " + seleccionado);
        }
    }

    // Recargar tabla desde CSV
    @FXML
    public void actualizar() {
        listaMantenimientos = MantenimientoDAO.cargarTodos();
        observableList.setAll(listaMantenimientos);
        tablaMantenimientos.refresh();
    }

    // Limpiar formulario de nuevo registro
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