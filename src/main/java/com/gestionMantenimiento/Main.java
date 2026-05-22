package com.gestionMantenimiento;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.gestionMantenimiento.Util.FestivosColombiaService;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Precargar festivos de Colombia en background (2026 y 2027)
        precargarFestivos();

        Parent root = FXMLLoader.load(getClass().getResource("/com/gestionMantenimiento/login.fxml"));
        primaryStage.setTitle("Gestión de Mantenimiento");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    /**
     * Precarga los festivos de Colombia en background para evitar latencias
     * cuando se cree la primera solicitud.
     */
    private void precargarFestivos() {
        FestivosColombiaService festivos = FestivosColombiaService.getInstance();
        // Precargar años actuales y siguientes
        festivos.precargarAnios(2026, 2027, 2028);
    }

    public static void main(String[] args) {
        launch(args);
    }
}