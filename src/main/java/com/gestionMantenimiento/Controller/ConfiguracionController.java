package com.gestionMantenimiento.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ConfiguracionController {

    @FXML private TextField txtTecnico1;
    @FXML private TextField txtTecnico2;
    @FXML private TextField txtTecnico3;
    @FXML private Label lblMensaje;

    @FXML
    public void initialize() {
        txtTecnico1.setText("López");
        txtTecnico2.setText("Martínez");
        txtTecnico3.setText("García");
    }

    @FXML
    public void guardarConfiguracion() {
        // Por ahora solo muestra confirmación
        // En una versión futura se podría persistir en un archivo de config
        lblMensaje.setText("✔ Configuración guardada correctamente.");
        lblMensaje.setStyle("-fx-text-fill: #2A7A2A; -fx-font-size: 12px;");
    }
}