package com.gestionMantenimiento.Controller;

import com.gestionMantenimiento.Modelo.UsuarioDAO;
import com.gestionMantenimiento.Util.ConexionBD;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasena;
    @FXML private Label lblError;
    @FXML private ComboBox<String> cbTipoBD;

    @FXML
    public void initialize() {
        cbTipoBD.getItems().addAll("MySQL", "SQLite", "MongoDB");
        cbTipoBD.setValue("SQLite");
    }

    @FXML
    public void iniciarSesion() {
        String usuario    = txtUsuario.getText().trim();
        String contrasena = txtContrasena.getText();
        String tipoBD     = cbTipoBD.getValue();

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            lblError.setText("* Ingresa usuario y contraseña.");
            return;
        }


        ConexionBD.conectar(tipoBD);

        if (UsuarioDAO.login(usuario, contrasena)) {
            abrirPrincipal();
        } else {
            lblError.setText("* Usuario o contraseña incorrectos.");
            txtContrasena.clear();
        }
    }

    private void abrirPrincipal() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/gestionMantenimiento/main.fxml"));
            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestión de Mantenimiento");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}