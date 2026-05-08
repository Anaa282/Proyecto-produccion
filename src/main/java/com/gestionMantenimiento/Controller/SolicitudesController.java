package com.gestionMantenimiento.Controller;

import com.gestionMantenimiento.Modelo.MantenimientoDAO;
import com.gestionMantenimiento.Modelo.Mantenimiento;
import com.gestionMantenimiento.Modelo.MantenimientoDAOMongo;
import com.gestionMantenimiento.Util.ConexionBD;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;



public class SolicitudesController {

    // Tabla
    @FXML
    private TableView<Mantenimiento> tablaMantenimientos;
    @FXML
    private TableColumn<Mantenimiento, Integer> colId;
    @FXML
    private TableColumn<Mantenimiento, String> colFecha;
    @FXML
    private TableColumn<Mantenimiento, String> colResidente;
    @FXML
    private TableColumn<Mantenimiento, String> colCategoria;
    @FXML
    private TableColumn<Mantenimiento, String> colPrioridad;
    @FXML
    private TableColumn<Mantenimiento, String> colEstado;
    @FXML
    private TableColumn<Mantenimiento, String> colTecnico;

    // Detalles
    @FXML
    private Label lblId;
    @FXML
    private Label lblFecha;
    @FXML
    private Label lblResidente;
    @FXML
    private Label lblUbicacion;
    @FXML
    private Label lblTiempo;
    @FXML
    private TextArea txtDescripcion;

    @FXML
    private ComboBox<String> cbEstado;
    @FXML
    private ComboBox<String> cbTecnico;

    // Panel nuevo
    @FXML
    private VBox panelNuevo;
    @FXML
    private TextField nResidente;
    @FXML
    private TextField nUbicacion;
    @FXML
    private DatePicker nFecha;
    @FXML
    private ComboBox<String> nCategoria;
    @FXML
    private ComboBox<String> nPrioridad;
    @FXML
    private ComboBox<String> nEstado;
    @FXML
    private ComboBox<String> nTecnico;
    @FXML
    private TextArea nDescripcion;
    @FXML
    private Label lblError;

    // DAO
    private MantenimientoDAO dao = new MantenimientoDAO();
    private ObservableList<Mantenimiento> lista;

    // Inicializa
    @FXML
    public void initialize() {

        configurarColumnas();
        cargarSolicitudes();
        configurarCombos();
        seleccionarFila();
    }

    // Columnas
    private void configurarColumnas() {

        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getId()).asObject());
        colFecha.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getFecha()));
        colResidente.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getResidente()));
        colCategoria.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getCategoria()));
        colPrioridad.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getPrioridad()));
        colEstado.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEstado()));
        colTecnico.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTecnico()));
    }

    // Cargar desde MySQL
    private void cargarSolicitudes() {
        List<Mantenimiento> datos;

        if (ConexionBD.getTipo() != null &&
                ConexionBD.getTipo().equalsIgnoreCase("mongoDB")) {
            MantenimientoDAOMongo dao = new MantenimientoDAOMongo();
            datos = dao.obtenerMantenimientos();
        } else {
            MantenimientoDAO dao = new MantenimientoDAO();
            datos = dao.obtenerMantenimientos();
        }

        lista = FXCollections.observableArrayList(datos);
        tablaMantenimientos.setItems(lista);
    }

    // Seleccionar fila
    private void seleccionarFila() {

        tablaMantenimientos.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, m) -> {

            if (m != null) {

                lblId.setText("ID: " + m.getId());
                lblFecha.setText("Fecha: " + m.getFecha());
                lblResidente.setText(m.getResidente());
                lblUbicacion.setText(m.getUbicacion());
                txtDescripcion.setText(m.getDescripcion());

                cbEstado.setValue(m.getEstado());
                cbTecnico.setValue(m.getTecnico());
            }
        });
    }

    // Combos
    private void configurarCombos() {

        cbEstado.setItems(FXCollections.observableArrayList(
                "Pendiente",
                "En proceso",
                "Finalizado"
        ));

        cbTecnico.setItems(FXCollections.observableArrayList(
                "Juan",
                "Pedro",
                "Maria"
        ));

        nCategoria.setItems(FXCollections.observableArrayList(
                "Electricidad",
                "Plomeria",
                "Infraestructura"
        ));

        nPrioridad.setItems(FXCollections.observableArrayList(
                "Alta",
                "Media",
                "Baja"
        ));

        nEstado.setItems(FXCollections.observableArrayList(
                "Pendiente",
                "En proceso",
                "Finalizado"
        ));

        nTecnico.setItems(FXCollections.observableArrayList(
                "Juan",
                "Pedro",
                "Maria"
        ));
    }

    // Botón actualizar
    @FXML
    private void actualizar() {

        cargarSolicitudes();
    }

    // Mostrar panel nuevo
    @FXML
    private void mostrarPanelNuevo() {

        panelNuevo.setVisible(true);
        panelNuevo.setManaged(true);
    }

    // Ocultar panel
    @FXML
    private void ocultarPanelNuevo() {

        panelNuevo.setVisible(false);
        panelNuevo.setManaged(false);
    }

    // Guardar nueva solicitud
    @FXML
    private void guardarNuevo() {
        if (nResidente.getText().isEmpty() ||
                nUbicacion.getText().isEmpty() ||
                nFecha.getValue() == null) {
            lblError.setText("Complete los campos");
            return;
        }

        Mantenimiento m = new Mantenimiento(
                nFecha.getValue().toString(),
                nResidente.getText(),
                nCategoria.getValue(),
                nPrioridad.getValue(),
                nEstado.getValue(),
                nTecnico.getValue(),
                nDescripcion.getText(),
                nUbicacion.getText(),
                LocalDateTime.now(),
                null,
                ""
        );

        if (ConexionBD.getTipo() != null &&
                ConexionBD.getTipo().equalsIgnoreCase("MongoDB")) {
            new MantenimientoDAOMongo().insertar(m);
        } else {
            new MantenimientoDAO().insertar(m);
        }

        cargarSolicitudes();
        ocultarPanelNuevo();

    }

    @FXML
    private void guardarCambios() {
        Mantenimiento m = tablaMantenimientos.getSelectionModel().getSelectedItem();
        if (m == null) return;

        m.setEstado(cbEstado.getValue());
        m.setTecnico(cbTecnico.getValue());

        if (ConexionBD.getTipo() != null &&
                ConexionBD.getTipo().equalsIgnoreCase("MongoDB")) {
            new MantenimientoDAOMongo().actualizar(m);
        } else {
            new MantenimientoDAO().actualizar(m);
        }

        cargarSolicitudes();
    }
}