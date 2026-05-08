package com.gestionMantenimiento.Controller;

import com.gestionMantenimiento.Modelo.Mantenimiento;
import com.gestionMantenimiento.Modelo.MantenimientoDAO;
import com.gestionMantenimiento.Util.FestivosColombiaService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;

public class ReportesController {

    @FXML private TableView<String[]> tablaReportes;
    @FXML private TableColumn<String[], String> colCategoria;
    @FXML private TableColumn<String[], String> colTotal;
    @FXML private TableColumn<String[], String> colPendientes;
    @FXML private TableColumn<String[], String> colFinalizados;
    @FXML private TableColumn<String[], String> colTiempoPromedio;

    @FXML private Label lblTotalSolicitudes;
    @FXML private Label lblPromedioResolucion;
    @FXML private Label lblCategoriaMasFrecuente;

    private static final List<String> CATEGORIAS =
            Arrays.asList("Plomeria", "Electrica", "Ascensor", "Pintura", "Otro");

    @FXML
    public void initialize() {

        // Precargar festivos del año actual y siguiente en background
        int anioActual = java.time.LocalDate.now().getYear();
        FestivosColombiaService.getInstance().precargarAnios(anioActual, anioActual + 1);

        colCategoria.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[0]));
        colTotal.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[1]));
        colPendientes.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[2]));
        colFinalizados.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[3]));
        colTiempoPromedio.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[4]));

        cargarDatos();
    }

    private void cargarDatos() {

        MantenimientoDAO dao = new MantenimientoDAO();
        ArrayList<Mantenimiento> lista = new ArrayList<>(dao.obtenerMantenimientos());

        List<String[]> filas = new ArrayList<>();

        String categoriaMasFrecuente = "";
        long maxCount = 0;

        for (String cat : CATEGORIAS) {

            List<Mantenimiento> porCat = lista.stream()
                    .filter(m -> cat.equalsIgnoreCase(m.getCategoria()))
                    .toList();

            long total = porCat.size();

            long pendientes = porCat.stream()
                    .filter(m -> m.getEstado().equalsIgnoreCase("Pendiente"))
                    .count();

            long finalizados = porCat.stream()
                    .filter(m -> m.getEstado().equalsIgnoreCase("Finalizado"))
                    .count();

            OptionalDouble avg = porCat.stream()
                    .filter(m -> m.getFechaHoraInicio() != null && m.getFechaHoraFin() != null)
                    .mapToLong(Mantenimiento::getTiempoSolucionDiasHabiles)
                    .average();

            String promedio = avg.isPresent()
                    ? String.format("%.1f días háb.", avg.getAsDouble())
                    : "-";

            if (total > maxCount) {
                maxCount = total;
                categoriaMasFrecuente = cat;
            }

            filas.add(new String[]{
                    cat,
                    String.valueOf(total),
                    String.valueOf(pendientes),
                    String.valueOf(finalizados),
                    promedio
            });
        }

        tablaReportes.setItems(FXCollections.observableArrayList(filas));

        lblTotalSolicitudes.setText(String.valueOf(lista.size()));

        lblCategoriaMasFrecuente.setText(
                categoriaMasFrecuente.isEmpty() ? "-" : categoriaMasFrecuente
        );

        lista.stream()
                .filter(m -> m.getFechaHoraInicio() != null && m.getFechaHoraFin() != null)
                .mapToLong(Mantenimiento::getTiempoSolucionDiasHabiles)
                .average()
                .ifPresentOrElse(
                        d -> lblPromedioResolucion.setText(String.format("%.1f días hábiles", d)),
                        () -> lblPromedioResolucion.setText("-")
                );
    }

    @FXML
    public void actualizar() {
        cargarDatos();
    }
}