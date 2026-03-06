package com.gestionMantenimiento;

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

    public Mantenimiento(int id, String fecha, String residente, String categoria,
                         String prioridad, String estado, String tecnico,
                         String descripcion, String ubicacion) {
        this.id = id;
        this.fecha = fecha;
        this.residente = residente;
        this.categoria = categoria;
        this.prioridad = prioridad;
        this.estado = estado;
        this.tecnico = tecnico;
        this.descripcion = descripcion;
        this.ubicacion = ubicacion;
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

    // Setters
    public void setEstado(String estado) { this.estado = estado; }
    public void setTecnico(String tecnico) { this.tecnico = tecnico; }

    // Convierte el objeto a una línea CSV (separado por ;)
    public String toCSV() {
        return id + ";" + fecha + ";" + residente + ";" + categoria + ";" +
                prioridad + ";" + estado + ";" + tecnico + ";" +
                descripcion + ";" + ubicacion;
    }

    // Crea un Mantenimiento desde una línea CSV
    public static Mantenimiento fromCSV(String linea) {
        String[] partes = linea.split(";", -1);
        return new Mantenimiento(
                Integer.parseInt(partes[0]),
                partes[1],
                partes[2],
                partes[3],
                partes[4],
                partes[5],
                partes[6],
                partes[7],
                partes[8]
        );
    }

    @Override
    public String toString() {
        return "Mantenimiento{id=" + id + ", residente='" + residente + "', estado='" + estado + "'}";
    }
}