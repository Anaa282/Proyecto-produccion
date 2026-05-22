package com.gestionMantenimiento.Controller;

import com.gestionMantenimiento.Modelo.MantenimientoDAO;
import com.gestionMantenimiento.Modelo.Mantenimiento;
import com.gestionMantenimiento.Modelo.MantenimientoDAOMongo;
import com.gestionMantenimiento.Modelo.AuditoriaDAO;
import com.gestionMantenimiento.Util.ConexionBD;
import com.gestionMantenimiento.Util.FestivosColombiaService;

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

        // Excluir los finalizados — esos viven en Historial/Auditoría
        lista = FXCollections.observableArrayList(
                datos.stream()
                        .filter(m -> !m.getEstado().equalsIgnoreCase("Finalizado"))
                        .collect(java.util.stream.Collectors.toList())
        );
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

        boolean insertado;
        if (ConexionBD.getTipo() != null &&
                ConexionBD.getTipo().equalsIgnoreCase("MongoDB")) {
            insertado = new MantenimientoDAOMongo().insertar(m);
        } else {
            insertado = new MantenimientoDAO().insertar(m);
        }

        // Registrar en auditoría de MongoDB siempre que se inserte correctamente
        if (insertado) {
            new AuditoriaDAO().registrar(m, "CREACION");
            // Mostrar aviso de días hábiles para responder
            mostrarAvisoDiasHabiles(m);
        }

        cargarSolicitudes();
        ocultarPanelNuevo();

    }

    @FXML
    private void guardarCambios() {
        Mantenimiento m = tablaMantenimientos.getSelectionModel().getSelectedItem();
        if (m == null) return;

        String estadoAnterior = m.getEstado();
        m.setEstado(cbEstado.getValue());
        m.setTecnico(cbTecnico.getValue());

        boolean actualizado;
        if (ConexionBD.getTipo() != null &&
                ConexionBD.getTipo().equalsIgnoreCase("MongoDB")) {
            actualizado = new MantenimientoDAOMongo().actualizar(m);
        } else {
            actualizado = new MantenimientoDAO().actualizar(m);
        }

        if (actualizado) {
            boolean recienFinalizado = !estadoAnterior.equalsIgnoreCase("Finalizado")
                    && "Finalizado".equalsIgnoreCase(cbEstado.getValue());

            if (recienFinalizado) {
                // Registrar fecha/hora de finalización y auditar como FINALIZACION
                m.setFechaHoraFin(java.time.LocalDateTime.now());
                new AuditoriaDAO().registrar(m, "FINALIZACION");
            } else {
                // Cualquier otro cambio también queda en auditoría
                new AuditoriaDAO().registrar(m, "ACTUALIZACION");
            }
        }

        cargarSolicitudes();
    }

    /**
     * Muestra un aviso con el tiempo de respuesta estimado en días hábiles
     * según la prioridad y considerando los festivos de Colombia.
     */
    private void mostrarAvisoDiasHabiles(Mantenimiento m) {
        try {
            String prioridad = m.getPrioridad();
            int diasHabiles = calcularDiasHabilesSegunPrioridad(prioridad);
            LocalDate fechaEstimada = calcularFechaEstimada(LocalDate.now(), diasHabiles);

            String mensaje = String.format(
                    "✓ Solicitud creada correctamente (ID: %d)\n\n" +
                    "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                    "Información de Tiempo de Respuesta\n" +
                    "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                    "📌 Prioridad: %s\n" +
                    "📅 Fecha de solicitud: %s\n" +
                    "⏰ Días hábiles para responder: %d\n" +
                    "📆 Fecha estimada de respuesta: %s\n\n" +
                    "Nota: Los días hábiles excluyen fines de semana\n" +
                    "y festivos colombianos.",
                    m.getId(),
                    prioridad,
                    m.getFecha(),
                    diasHabiles,
                    fechaEstimada
            );

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("✓ Solicitud Creada");
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();

        } catch (Exception e) {
            System.out.println(" Error mostrando aviso de días hábiles: " + e.getMessage());
        }
    }

    /**
     * Retorna los días hábiles para responder según la prioridad.
     * Estos son los SLA (Service Level Agreement) definidos.
     */
    private int calcularDiasHabilesSegunPrioridad(String prioridad) {
        if (prioridad == null) return 5; // Default

        switch (prioridad.toLowerCase()) {
            case "alta":
                return 2;
            case "media":
                return 5;
            case "baja":
                return 10;
            default:
                return 5;
        }
    }

    /**
     * Calcula la fecha estimada sumando días hábiles (excluye fines de semana y festivos).
     * Utiliza el servicio de festivos de Colombia.
     */
    private LocalDate calcularFechaEstimada(LocalDate inicio, int diasHabiles) {
        FestivosColombiaService festivos = FestivosColombiaService.getInstance();
        LocalDate cursor = inicio;
        int diasContados = 0;

        while (diasContados < diasHabiles) {
            cursor = cursor.plusDays(1);
            if (festivos.esDiaHabil(cursor)) {
                diasContados++;
            }
        }

        return cursor;
    }
}