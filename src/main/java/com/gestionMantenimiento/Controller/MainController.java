package com.gestionMantenimiento.Controller;

import com.gestionMantenimiento.Util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class MainController {

    @FXML private BorderPane rootPane;
    @FXML private StackPane contentArea;
    @FXML private Label lblRol;
    @FXML private Label lblNombre;

    @FXML private Button btnDashboard;
    @FXML private Button btnSolicitudes;
    @FXML private Button btnTecnicos;
    @FXML private Button btnReportes;
    @FXML private Button btnHistorial;
    @FXML private Button btnConfiguracion;

    private Button btnActivo;

    @FXML
    public void initialize() {
        lblNombre.setText(SessionManager.getNombreUsuario());
        lblRol.setText(getRolTexto());

        // Ocultar botones según rol
        switch (SessionManager.getRol()) {
            case RESIDENTE:
                btnTecnicos.setVisible(false);     btnTecnicos.setManaged(false);
                btnReportes.setVisible(false);      btnReportes.setManaged(false);
                btnConfiguracion.setVisible(false); btnConfiguracion.setManaged(false);
                break;
            case TECNICO:
                btnTecnicos.setVisible(false);     btnTecnicos.setManaged(false);
                btnReportes.setVisible(false);      btnReportes.setManaged(false);
                btnHistorial.setVisible(false);     btnHistorial.setManaged(false);
                btnConfiguracion.setVisible(false); btnConfiguracion.setManaged(false);
                break;
            default:
                break; // ADMIN ve todo
        }

        navegarA("dashboard.fxml", btnDashboard);
    }

    private String getRolTexto() {
        switch (SessionManager.getRol()) {
            case ADMIN:     return "Administrador";
            case RESIDENTE: return "Residente";
            case TECNICO:   return "Técnico";
            default:        return "";
        }
    }

    @FXML public void irDashboard()     { navegarA("dashboard.fxml",      btnDashboard); }
    @FXML public void irSolicitudes()   { navegarA("solicitudes.fxml",    btnSolicitudes); }
    @FXML public void irTecnicos()      { if (SessionManager.esAdmin()) navegarA("tecnicos.fxml", btnTecnicos); }
    @FXML public void irReportes()      { if (SessionManager.esAdmin()) navegarA("reportes.fxml", btnReportes); }
    @FXML public void irHistorial()     { if (!SessionManager.esTecnico()) navegarA("historial.fxml", btnHistorial); }
    @FXML public void irConfiguracion() { if (SessionManager.esAdmin()) navegarA("configuracion.fxml", btnConfiguracion); }

    @FXML
    public void cerrarSesion() {
        try {
            SessionManager.cerrarSesion();
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