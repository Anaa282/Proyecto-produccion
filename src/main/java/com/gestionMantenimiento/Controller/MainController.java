package com.gestionMantenimiento.Controller;

import com.gestionMantenimiento.Modelo.Mantenimiento;
import com.gestionMantenimiento.Modelo.MantenimientoDAO;
import com.gestionMantenimiento.Util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.util.ArrayList;

public class MainController {

    @FXML private BorderPane rootPane;
    @FXML private StackPane contentArea;
    @FXML private Label lblRol;

    // Botones sidebar
    @FXML private Button btnDashboard;
    @FXML private Button btnSolicitudes;
    @FXML private Button btnTecnicos;
    @FXML private Button btnReportes;
    @FXML private Button btnHistorial;
    @FXML private Button btnConfiguracion;

    private Button btnActivo;

    @FXML
    public void initialize() {
        // Mostrar rol en sidebar
        lblRol.setText(SessionManager.esAdmin() ? "Administrador" : "Usuario");

        // Ocultar botones restringidos para usuario
        if (!SessionManager.esAdmin()) {
            btnTecnicos.setVisible(false);
            btnTecnicos.setManaged(false);
            btnConfiguracion.setVisible(false);
            btnConfiguracion.setManaged(false);
        }

        // Cargar pantalla inicial
        navegarA("dashboard.fxml", btnDashboard);
    }

    @FXML public void irDashboard()     { navegarA("dashboard.fxml",      btnDashboard); }
    @FXML public void irSolicitudes()   { navegarA("solicitudes.fxml",    btnSolicitudes); }
    @FXML public void irTecnicos()      { if (SessionManager.esAdmin()) navegarA("tecnicos.fxml",       btnTecnicos); }
    @FXML public void irReportes()      { navegarA("reportes.fxml",       btnReportes); }
    @FXML public void irHistorial()     { navegarA("historial.fxml",      btnHistorial); }
    @FXML public void irConfiguracion() { if (SessionManager.esAdmin()) navegarA("configuracion.fxml",  btnConfiguracion); }

    @FXML
    public void cerrarSesion() {
        try {
            SessionManager.setRol(null);
            Parent login = FXMLLoader.load(getClass().getResource("/com/gestionMantenimiento/login.fxml"));
            rootPane.getScene().setRoot(login);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void navegarA(String fxml, Button boton) {
        try {
            Parent vista = FXMLLoader.load(getClass().getResource("/com/gestionMantenimiento/" + fxml));
            contentArea.getChildren().setAll(vista);

            // Actualizar botón activo
            if (btnActivo != null) {
                btnActivo.setStyle(btnActivo.getStyle()
                        .replace("-fx-background-color: #4A4A4A;", "-fx-background-color: transparent;")
                        .replace("-fx-text-fill: #FFFFFF;", "-fx-text-fill: #AAAAAA;")
                        .replace("-fx-font-weight: bold;", ""));
            }
            boton.setStyle(boton.getStyle()
                    .replace("-fx-background-color: transparent;", "-fx-background-color: #4A4A4A;")
                    .replace("-fx-text-fill: #AAAAAA;", "-fx-text-fill: #FFFFFF;")
                    + " -fx-font-weight: bold;");
            btnActivo = boton;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}