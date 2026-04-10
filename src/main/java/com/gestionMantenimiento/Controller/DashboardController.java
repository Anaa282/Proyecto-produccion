package com.gestionMantenimiento.Controller;

import com.gestionMantenimiento.Modelo.Mantenimiento;
import com.gestionMantenimiento.Modelo.MantenimientoDAO;
import com.gestionMantenimiento.Util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.List;

public class DashboardController {

    @FXML private Label lblTotal;
    @FXML private Label lblPendientes;
    @FXML private Label lblEnProceso;
    @FXML private Label lblFinalizados;
    @FXML private Label lblAltaPrioridad;
    @FXML private Label lblBienvenida;

    @FXML
    public void initialize() {

        String rol = SessionManager.esAdmin() ? "Administrador" : "Usuario";
        lblBienvenida.setText("Bienvenido, " + rol);

        MantenimientoDAO dao = new MantenimientoDAO();

        List<Mantenimiento> lista = dao.obtenerMantenimientos();

        lblTotal.setText(String.valueOf(lista.size()));

        lblPendientes.setText(
                String.valueOf(lista.stream()
                        .filter(m -> m.getEstado().equalsIgnoreCase("Pendiente"))
                        .count())
        );

        lblEnProceso.setText(
                String.valueOf(lista.stream()
                        .filter(m -> m.getEstado().equalsIgnoreCase("En proceso"))
                        .count())
        );

        lblFinalizados.setText(
                String.valueOf(lista.stream()
                        .filter(m -> m.getEstado().equalsIgnoreCase("Finalizado"))
                        .count())
        );

        lblAltaPrioridad.setText(
                String.valueOf(lista.stream()
                        .filter(m -> m.getPrioridad().equalsIgnoreCase("Alta"))
                        .count())
        );
    }
}