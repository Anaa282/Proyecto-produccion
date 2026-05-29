package com.gestionMantenimiento.Controller;

import com.gestionMantenimiento.Modelo.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TecnicosController {

    // ── Tabla de carga de trabajo ─────────────────────────────────────────────
    @FXML private TableView<String[]> tablaTecnicos;
    @FXML private TableColumn<String[], String> colNombre;
    @FXML private TableColumn<String[], String> colEspecialidad;
    @FXML private TableColumn<String[], String> colEstado;
    @FXML private TableColumn<String[], String> colPendientes;
    @FXML private TableColumn<String[], String> colEnProceso;
    @FXML private TableColumn<String[], String> colFinalizados;
    @FXML private TableColumn<String[], String> colTotal;

    // ── Tabla de gestión (CRUD) ───────────────────────────────────────────────
    @FXML private TableView<Tecnico> tablaGestion;
    @FXML private TableColumn<Tecnico, String> gColNombre;
    @FXML private TableColumn<Tecnico, String> gColApellido;
    @FXML private TableColumn<Tecnico, String> gColEspecialidad;
    @FXML private TableColumn<Tecnico, String> gColTelefono;
    @FXML private TableColumn<Tecnico, String> gColEmail;
    @FXML private TableColumn<Tecnico, String> gColEstado;
    @FXML private TableColumn<Tecnico, String> gColUsuario;

    // ── Formulario ────────────────────────────────────────────────────────────
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtEspecialidad;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmail;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private TextField txtUsuarioLogin;
    @FXML private DatePicker dpFechaIngreso;

    // ── Botones ───────────────────────────────────────────────────────────────
    @FXML private Button btnAgregar;
    @FXML private Button btnActualizarTecnico;
    @FXML private Button btnEliminar;
    @FXML private Button btnLimpiar;

    // ── Estado interno ────────────────────────────────────────────────────────
    private final TecnicoDAO    tecnicoDAO    = new TecnicoDAO();
    private final AuditoriaDAO  auditoriaDAO  = new AuditoriaDAO();
    private final MantenimientoDAO mantenimientoDAO = new MantenimientoDAO();

    /** Usuario administrador activo. Ajústalo a tu sistema de sesión. */
    private String adminActual = "admin";

    /** Técnico seleccionado en la tabla de gestión (null si ninguno). */
    private Tecnico tecnicoSeleccionado = null;

    // ═════════════════════════════════════════════════════════════════════════
    //  INICIALIZACIÓN
    // ═════════════════════════════════════════════════════════════════════════

    @FXML
    public void initialize() {
        configurarTablaCarga();
        configurarTablaGestion();
        configurarFormulario();
        cargarDatos();
        cargarTablaGestion();
    }

    // ─── Tabla de carga de trabajo ────────────────────────────────────────────
    private void configurarTablaCarga() {
        colNombre      .setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[0]));
        colEspecialidad.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[1]));
        colEstado      .setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[2]));
        colPendientes  .setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[3]));
        colEnProceso   .setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[4]));
        colFinalizados .setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[5]));
        colTotal       .setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[6]));
    }

    // ─── Tabla de gestión CRUD ────────────────────────────────────────────────
    private void configurarTablaGestion() {
        gColNombre      .setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNombre()));
        gColApellido    .setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getApellido()));
        gColEspecialidad.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEspecialidad()));
        gColTelefono    .setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTelefono()));
        gColEmail       .setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEmail()));
        gColEstado      .setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEstado()));
        gColUsuario     .setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getUsuarioLogin()));

        // Al seleccionar una fila, cargar datos en el formulario
        tablaGestion.getSelectionModel().selectedItemProperty().addListener(
                (obs, anterior, seleccionado) -> {
                    tecnicoSeleccionado = seleccionado;
                    if (seleccionado != null) {
                        cargarEnFormulario(seleccionado);
                        btnActualizarTecnico.setDisable(false);
                        btnEliminar.setDisable(false);
                    } else {
                        btnActualizarTecnico.setDisable(true);
                        btnEliminar.setDisable(true);
                    }
                });
    }

    private void configurarFormulario() {
        cmbEstado.setItems(FXCollections.observableArrayList("Activo", "Inactivo"));
        cmbEstado.setValue("Activo");
        btnActualizarTecnico.setDisable(true);
        btnEliminar.setDisable(true);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  CRUD — AGREGAR
    // ═════════════════════════════════════════════════════════════════════════

    @FXML
    public void agregarTecnico() {
        if (!validarFormulario()) return;

        Tecnico nuevo = construirDesdFormulario(0);

        boolean ok = tecnicoDAO.insertar(nuevo);

        if (ok) {
            // Obtener el técnico recién insertado para capturar su id generado
            List<Tecnico> todos = tecnicoDAO.obtenerTecnicos();
            Tecnico insertado = todos.stream()
                    .filter(t -> t.getUsuarioLogin() != null
                            && t.getUsuarioLogin().equals(nuevo.getUsuarioLogin())
                            && t.getNombre().equals(nuevo.getNombre())
                            && t.getApellido().equals(nuevo.getApellido()))
                    .reduce((a, b) -> b)   // el último (mayor id)
                    .orElse(nuevo);

            auditoriaDAO.registrarTecnico(insertado, "CREACION_TECNICO", adminActual);

            mostrarInfo("Técnico agregado correctamente y registrado en auditoría MongoDB.");
            limpiarFormulario();
            cargarTablaGestion();
            cargarDatos();
        } else {
            mostrarError("No se pudo agregar el técnico. Revisa la consola.");
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  CRUD — ACTUALIZAR
    // ═════════════════════════════════════════════════════════════════════════

    @FXML
    public void actualizarTecnico() {
        if (tecnicoSeleccionado == null) {
            mostrarError("Selecciona un técnico en la tabla para actualizar.");
            return;
        }
        if (!validarFormulario()) return;

        Tecnico modificado = construirDesdFormulario(tecnicoSeleccionado.getId());

        boolean ok = tecnicoDAO.actualizar(modificado);

        if (ok) {
            auditoriaDAO.registrarTecnico(modificado, "ACTUALIZACION_TECNICO", adminActual);

            mostrarInfo("Técnico actualizado correctamente y registrado en auditoría MongoDB.");
            limpiarFormulario();
            cargarTablaGestion();
            cargarDatos();
        } else {
            mostrarError("No se pudo actualizar el técnico. Revisa la consola.");
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  CRUD — ELIMINAR
    // ═════════════════════════════════════════════════════════════════════════

    @FXML
    public void eliminarTecnico() {
        if (tecnicoSeleccionado == null) {
            mostrarError("Selecciona un técnico en la tabla para eliminar.");
            return;
        }

        String nombreCompleto = tecnicoSeleccionado.getNombreCompleto();
        int id = tecnicoSeleccionado.getId();

        // Confirmación antes de eliminar
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("¿Eliminar al técnico " + nombreCompleto + "?");
        confirm.setContentText("Esta acción no se puede deshacer.\nSe registrará en auditoría MongoDB.");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isEmpty() || result.get() != ButtonType.OK) return;

        // Guardar datos antes de eliminar para la auditoría
        Tecnico copia = new Tecnico(
                id,
                tecnicoSeleccionado.getNombre(),
                tecnicoSeleccionado.getApellido(),
                tecnicoSeleccionado.getEspecialidad(),
                tecnicoSeleccionado.getTelefono(),
                tecnicoSeleccionado.getEmail(),
                tecnicoSeleccionado.getEstado(),
                tecnicoSeleccionado.getFechaIngreso(),
                tecnicoSeleccionado.getUsuarioLogin()
        );

        boolean ok = tecnicoDAO.eliminar(id);

        if (ok) {
            // Auditar DESPUÉS de eliminar exitosamente
            auditoriaDAO.registrarTecnico(copia, "ELIMINACION_TECNICO", adminActual);

            mostrarInfo("Técnico eliminado correctamente y registrado en auditoría MongoDB.");
            limpiarFormulario();
            cargarTablaGestion();
            cargarDatos();
        } else {
            mostrarError("No se pudo eliminar el técnico. Revisa la consola.");
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  CARGA DE DATOS
    // ═════════════════════════════════════════════════════════════════════════

    /** Recarga la tabla de carga de trabajo (técnico ↔ mantenimientos). */
    private void cargarDatos() {
        List<Tecnico> tecnicos = tecnicoDAO.obtenerTecnicos();
        List<Mantenimiento> mantenimientos = new ArrayList<>(mantenimientoDAO.obtenerMantenimientos());
        List<String[]> filas = new ArrayList<>();

        for (Tecnico tec : tecnicos) {
            String nombreCompleto = tec.getNombreCompleto().toLowerCase();
            String nombre         = tec.getNombre().toLowerCase();
            String apellido       = tec.getApellido().toLowerCase();

            long pendientes  = mantenimientos.stream()
                    .filter(m -> coincideTecnico(m.getTecnico(), nombreCompleto, nombre, apellido)
                            && "Pendiente".equalsIgnoreCase(m.getEstado())).count();
            long enProceso   = mantenimientos.stream()
                    .filter(m -> coincideTecnico(m.getTecnico(), nombreCompleto, nombre, apellido)
                            && "En proceso".equalsIgnoreCase(m.getEstado())).count();
            long finalizados = mantenimientos.stream()
                    .filter(m -> coincideTecnico(m.getTecnico(), nombreCompleto, nombre, apellido)
                            && "Finalizado".equalsIgnoreCase(m.getEstado())).count();

            filas.add(new String[]{
                    tec.getNombreCompleto(),
                    tec.getEspecialidad() != null ? tec.getEspecialidad() : "",
                    tec.getEstado()       != null ? tec.getEstado()       : "",
                    String.valueOf(pendientes),
                    String.valueOf(enProceso),
                    String.valueOf(finalizados),
                    String.valueOf(pendientes + enProceso + finalizados)
            });
        }

        tablaTecnicos.setItems(FXCollections.observableArrayList(filas));
        System.out.println("Tabla de carga cargada con " + filas.size() + " filas.");
    }

    /** Recarga la tabla de gestión CRUD con todos los técnicos. */
    private void cargarTablaGestion() {
        List<Tecnico> lista = tecnicoDAO.obtenerTecnicos();
        tablaGestion.setItems(FXCollections.observableArrayList(lista));
        tablaGestion.getSelectionModel().clearSelection();
        tecnicoSeleccionado = null;
        btnActualizarTecnico.setDisable(true);
        btnEliminar.setDisable(true);
        System.out.println("Tabla de gestión cargada con " + lista.size() + " técnicos.");
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  BOTÓN ACTUALIZAR (refresco general)
    // ═════════════════════════════════════════════════════════════════════════

    @FXML
    public void actualizar() {
        cargarDatos();
        cargarTablaGestion();
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  FORMULARIO — helpers
    // ═════════════════════════════════════════════════════════════════════════

    private void cargarEnFormulario(Tecnico t) {
        txtNombre       .setText(t.getNombre()       != null ? t.getNombre()       : "");
        txtApellido     .setText(t.getApellido()     != null ? t.getApellido()     : "");
        txtEspecialidad .setText(t.getEspecialidad() != null ? t.getEspecialidad() : "");
        txtTelefono     .setText(t.getTelefono()     != null ? t.getTelefono()     : "");
        txtEmail        .setText(t.getEmail()        != null ? t.getEmail()        : "");
        txtUsuarioLogin .setText(t.getUsuarioLogin() != null ? t.getUsuarioLogin() : "");
        cmbEstado       .setValue(t.getEstado()      != null ? t.getEstado()       : "Activo");
        dpFechaIngreso  .setValue(t.getFechaIngreso());
    }

    @FXML
    public void limpiarFormulario() {
        txtNombre      .clear();
        txtApellido    .clear();
        txtEspecialidad.clear();
        txtTelefono    .clear();
        txtEmail       .clear();
        txtUsuarioLogin.clear();
        cmbEstado      .setValue("Activo");
        dpFechaIngreso .setValue(null);
        tablaGestion.getSelectionModel().clearSelection();
        tecnicoSeleccionado = null;
        btnActualizarTecnico.setDisable(true);
        btnEliminar         .setDisable(true);
    }

    private Tecnico construirDesdFormulario(int id) {
        return new Tecnico(
                id,
                txtNombre      .getText().trim(),
                txtApellido    .getText().trim(),
                txtEspecialidad.getText().trim(),
                txtTelefono    .getText().trim(),
                txtEmail       .getText().trim(),
                cmbEstado      .getValue(),
                dpFechaIngreso .getValue(),
                txtUsuarioLogin.getText().trim()
        );
    }

    private boolean validarFormulario() {
        if (txtNombre.getText().isBlank()) {
            mostrarError("El campo Nombre es obligatorio.");
            return false;
        }
        if (txtApellido.getText().isBlank()) {
            mostrarError("El campo Apellido es obligatorio.");
            return false;
        }
        if (cmbEstado.getValue() == null) {
            mostrarError("Selecciona un Estado.");
            return false;
        }
        return true;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  HELPERS — coincidencia y alertas
    // ═════════════════════════════════════════════════════════════════════════

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

    private void mostrarInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Éxito");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void mostrarError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  Setter para inyectar el usuario admin desde la sesión
    //  Llama a esto desde tu LoginController o MainController:
    //    tecnicosController.setAdminActual(sesion.getUsuario());
    // ═════════════════════════════════════════════════════════════════════════
    public void setAdminActual(String usuario) {
        this.adminActual = usuario;
    }
}