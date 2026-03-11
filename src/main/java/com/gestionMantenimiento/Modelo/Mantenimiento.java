package com.gestionMantenimiento.Modelo;

import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

public class Mantenimiento {

    private int id;
    private String fecha;
    private String residente;
    private String categoria;
    private String prioridad;
    private String estado;
    private String tecnico;
    private String descripcion;
    private String ubicacion;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;

    public static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public Mantenimiento(int id, String fecha, String residente, String categoria,
                         String prioridad, String estado, String tecnico,
                         String descripcion, String ubicacion,
                         LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin) {
        this.id = id;
        this.fecha = fecha;
        this.residente = residente;
        this.categoria = categoria;
        this.prioridad = prioridad;
        this.estado = estado;
        this.tecnico = tecnico;
        this.descripcion = descripcion;
        this.ubicacion = ubicacion;
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
    }

    // Getters
    public int getId() { return id; }
    public String getFecha() { return fecha; }
    public String getResidente() { return residente; }
    public String getCategoria() { return categoria; }
    public String getPrioridad() { return prioridad; }
    public String getEstado() { return estado; }
    public String getTecnico() { return tecnico; }
    public String getDescripcion() { return descripcion; }
    public String getUbicacion() { return ubicacion; }
    public LocalDateTime getFechaHoraInicio() { return fechaHoraInicio; }
    public LocalDateTime getFechaHoraFin() { return fechaHoraFin; }

    // Setters
    public void setEstado(String estado) { this.estado = estado; }
    public void setTecnico(String tecnico) { this.tecnico = tecnico; }
    public void setFechaHoraFin(LocalDateTime fechaHoraFin) { this.fechaHoraFin = fechaHoraFin; }

    // Calcula y devuelve el tiempo de resolución como texto legible
    public String getTiempoResolucion() {
        if (fechaHoraInicio == null) return "Sin registro";
        if (fechaHoraFin == null) {
            // Si no está finalizado, muestra el tiempo transcurrido hasta ahora
            Duration d = Duration.between(fechaHoraInicio, LocalDateTime.now());
            return formatearDuracion(d) + " (en curso)";
        }
        Duration d = Duration.between(fechaHoraInicio, fechaHoraFin);
        return formatearDuracion(d);
    }

    private String formatearDuracion(Duration d) {
        long dias = d.toDays();
        long horas = d.toHours() % 24;
        long minutos = d.toMinutes() % 60;

        if (dias > 0) {
            return dias + "d " + horas + "h " + minutos + "m";
        } else if (horas > 0) {
            return horas + "h " + minutos + "m";
        } else {
            return minutos + " minutos";
        }
    }

    // Convierte a línea CSV
    public String toCSV() {
        String inicio = fechaHoraInicio != null ? fechaHoraInicio.format(FORMATO) : "";
        String fin = fechaHoraFin != null ? fechaHoraFin.format(FORMATO) : "";
        return id + ";" + fecha + ";" + residente + ";" + categoria + ";" +
                prioridad + ";" + estado + ";" + tecnico + ";" +
                descripcion + ";" + ubicacion + ";" + inicio + ";" + fin;
    }

    //  Mantenimiento desde línea CSV
    public static Mantenimiento fromCSV(String linea) {
        String[] p = linea.split(";", -1);
        LocalDateTime inicio = p[9].isEmpty() ? null : LocalDateTime.parse(p[9], FORMATO);
        LocalDateTime fin = p.length > 10 && !p[10].isEmpty() ? LocalDateTime.parse(p[10], FORMATO) : null;
        return new Mantenimiento(
                Integer.parseInt(p[0]), p[1], p[2], p[3], p[4], p[5], p[6], p[7], p[8], inicio, fin
        );
    }

    @Override
    public String toString() {
        return "Mantenimiento{id=" + id + ", residente='" + residente + "', estado='" + estado + "'}";
    }
}