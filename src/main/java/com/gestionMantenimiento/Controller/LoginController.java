package com.gestionMantenimiento.Controller;

import com.gestionMantenimiento.Util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;

public class LoginController {

    @FXML private Button btnAdmin;
    @FXML private Button btnUsuario;

    @FXML
    public void entrarComoAdmin() {
        SessionManager.setRol(SessionManager.Rol.ADMIN);
        abrirPrincipal();
    }

    @FXML
    public void entrarComoUsuario() {
        SessionManager.setRol(SessionManager.Rol.USUARIO);
        abrirPrincipal();
    }

    private void abrirPrincipal() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/gestionMantenimiento/main.fxml"));
            Stage stage = (Stage) btnAdmin.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestión de Mantenimiento");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}