package com.gestionMantenimiento.Controller;

import com.gestionMantenimiento.Modelo.Mantenimiento;
import com.gestionMantenimiento.Modelo.MantenimientoDAO;
import com.gestionMantenimiento.Util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.stream.Collectors;

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

        ArrayList<Mantenimiento> lista = MantenimientoDAO.cargarTodos();

        lblTotal.setText(String.valueOf(lista.size()));
        lblPendientes.setText(String.valueOf(lista.stream().filter(m -> m.getEstado().equals("Pendiente")).count()));
        lblEnProceso.setText(String.valueOf(lista.stream().filter(m -> m.getEstado().equals("En proceso")).count()));
        lblFinalizados.setText(String.valueOf(lista.stream().filter(m -> m.getEstado().equals("Finalizado")).count()));
        lblAltaPrioridad.setText(String.valueOf(lista.stream().filter(m -> m.getPrioridad().equals("Alta")).count()));
    }
}