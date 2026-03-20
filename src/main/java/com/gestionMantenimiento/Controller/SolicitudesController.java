package com.gestionMantenimiento.Controller;

import com.gestionMantenimiento.Modelo.Mantenimiento;
import com.gestionMantenimiento.Modelo.MantenimientoDAO;
import com.gestionMantenimiento.Util.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class SolicitudesController {

    @FXML private TableView<Mantenimiento> tablaMantenimientos;
    @FXML private TableColumn<Mantenimiento, String> colId;
    @FXML private TableColumn<Mantenimiento, String> colFecha;
    @FXML private TableColumn<Mantenimiento, String> colResidente;
    @FXML private TableColumn<Mantenimiento, String> colCategoria;
    @FXML private TableColumn<Mantenimiento, String> colPrioridad;
    @FXML private TableColumn<Mantenimiento, String> colEstado;
    @FXML private TableColumn<Mantenimiento, String> colTecnico;

    @FXML private Label lblId;
    @FXML private Label lblFecha;
    @FXML private Label lblResidente;
    @FXML private Label lblUbicacion;
    @FXML private Label lblTiempo;
    @FXML private TextArea txtDescripcion;
    @FXML private ComboBox<String> cbEstado;
    @FXML private ComboBox<String> cbTecnico;
    @FXML private HBox hboxEdicion;
    @FXML private HBox hboxEdicionTecnico; // fila de asignar técnico (solo admin)
    @FXML private Button btnNuevo;

    @FXML private VBox panelNuevo;
    @FXML private TextField nResidente;
    @FXML private TextField nUbicacion;
    @FXML private DatePicker nFecha;
    @FXML private ComboBox<String> nCategoria;
    @FXML private ComboBox<String> nPrioridad;
    @FXML private ComboBox<String> nEstado;
    @FXML private ComboBox<String> nTecnico;
    @FXML private TextArea nDescripcion;
    @FXML private Label lblError;

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

        colId.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getId())));
        colFecha.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getFecha()));
        colResidente.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getResidente()));
        colCategoria.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCategoria()));
        colPrioridad.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPrioridad()));
        colEstado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEstado()));
        colTecnico.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTecnico()));

        listaMantenimientos = MantenimientoDAO.cargarTodos();
        cargarTablaSegunRol();

        tablaMantenimientos.getSelectionModel().selectedItemProperty().addListener(
                (obs, o, n) -> mostrarDetalles(n));

        aplicarRol();
    }

    private void cargarTablaSegunRol() {
        // Técnico solo ve sus solicitudes asignadas
        if (SessionManager.esTecnico()) {
            String nombreTecnico = SessionManager.getNombreUsuario();
            ObservableList<Mantenimiento> suyas = FXCollections.observableArrayList(
                    listaMantenimientos.stream()
                            .filter(m -> nombreTecnico.equals(m.getTecnico()))
                            .collect(Collectors.toList())
            );
            observableList = suyas;
        } else {
            observableList = FXCollections.observableArrayList(listaMantenimientos);
        }
        tablaMantenimientos.setItems(observableList);
    }

    private void aplicarRol() {
        switch (SessionManager.getRol()) {
            case ADMIN:
                // Ve y puede hacer todo
                break;
            case RESIDENTE:
                // Puede crear pero no editar
                hboxEdicion.setVisible(false);
                hboxEdicion.setManaged(false);
                nEstado.setVisible(false);  nEstado.setManaged(false);
                nTecnico.setVisible(false); nTecnico.setManaged(false);
                break;
            case TECNICO:
                // Puede cambiar estado de sus solicitudes, no puede crear ni asignar técnico
                btnNuevo.setVisible(false);
                btnNuevo.setManaged(false);
                // Ocultar fila de asignar técnico
                hboxEdicionTecnico.setVisible(false);
                hboxEdicionTecnico.setManaged(false);
                break;
        }
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
            lblTiempo.setText("⏱ Tiempo: " + m.getTiempoResolucion());
        }
    }

    @FXML
    public void guardarCambios() {
        Mantenimiento sel = tablaMantenimientos.getSelectionModel().getSelectedItem();
        if (sel == null) return;

        // Técnico solo puede editar sus propias solicitudes
        if (SessionManager.esTecnico()) {
            if (!SessionManager.getNombreUsuario().equals(sel.getTecnico())) return;
            sel.setEstado(cbEstado.getValue());
        } else {
            // Admin puede cambiar todo
            String anterior = sel.getEstado();
            String nuevo    = cbEstado.getValue();
            sel.setEstado(nuevo);
            sel.setTecnico(cbTecnico.getValue());
            if (!anterior.equals("Finalizado") && nuevo.equals("Finalizado"))
                sel.setFechaHoraFin(LocalDateTime.now());
            if (anterior.equals("Finalizado") && !nuevo.equals("Finalizado"))
                sel.setFechaHoraFin(null);
        }

        MantenimientoDAO.guardarTodos(listaMantenimientos);
        tablaMantenimientos.refresh();
        lblTiempo.setText("⏱ Tiempo: " + sel.getTiempoResolucion());
    }

    @FXML public void mostrarPanelNuevo() { limpiarFormulario(); panelNuevo.setVisible(true); panelNuevo.setManaged(true); }
    @FXML public void ocultarPanelNuevo() { panelNuevo.setVisible(false); panelNuevo.setManaged(false); limpiarFormulario(); }

    @FXML
    public void guardarNuevo() {
        if (nResidente.getText().trim().isEmpty() || nUbicacion.getText().trim().isEmpty() ||
                nFecha.getValue() == null || nCategoria.getValue() == null || nPrioridad.getValue() == null) {
            lblError.setText("* Completa todos los campos obligatorios");
            return;
        }
        lblError.setText("");
        String fechaStr     = nFecha.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String estadoFinal  = SessionManager.esAdmin() && nEstado.getValue() != null ? nEstado.getValue() : "Pendiente";
        String tecnicoFinal = SessionManager.esAdmin() && nTecnico.getValue() != null ? nTecnico.getValue() : "";

        int nuevoId = listaMantenimientos.isEmpty() ? 1 :
                listaMantenimientos.stream().mapToInt(Mantenimiento::getId).max().getAsInt() + 1;

        LocalDateTime ahora = LocalDateTime.now();
        Mantenimiento nuevo = new Mantenimiento(nuevoId, fechaStr,
                nResidente.getText().trim(), nCategoria.getValue(), nPrioridad.getValue(),
                estadoFinal, tecnicoFinal, nDescripcion.getText().trim(),
                nUbicacion.getText().trim(), ahora,
                estadoFinal.equals("Finalizado") ? ahora : null, "");

        listaMantenimientos.add(nuevo);
        observableList.add(nuevo);
        MantenimientoDAO.guardarTodos(listaMantenimientos);
        ocultarPanelNuevo();
        tablaMantenimientos.getSelectionModel().selectLast();
    }

    @FXML
    public void actualizar() {
        listaMantenimientos = MantenimientoDAO.cargarTodos();
        cargarTablaSegunRol();
    }

    private void limpiarFormulario() {
        nResidente.clear(); nUbicacion.clear(); nFecha.setValue(null);
        nDescripcion.clear(); nCategoria.setValue(null); nPrioridad.setValue(null);
        nEstado.setValue(null); nTecnico.setValue(null); lblError.setText("");
    }
}