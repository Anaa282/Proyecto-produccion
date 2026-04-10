package com.gestionMantenimiento.Modelo;

import java.time.LocalDateTime;

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
    private String comentarios;

    // Constructor vacío
    public Mantenimiento() {
    }

    // Constructor sin ID (para insertar en BD)
    public Mantenimiento(
            String fecha,
            String residente,
            String categoria,
            String prioridad,
            String estado,
            String tecnico,
            String descripcion,
            String ubicacion,
            LocalDateTime fechaHoraInicio,
            LocalDateTime fechaHoraFin,
            String comentarios
    ) {
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
        this.comentarios = comentarios;
    }

    // Constructor completo
    public Mantenimiento(
            int id,
            String fecha,
            String residente,
            String categoria,
            String prioridad,
            String estado,
            String tecnico,
            String descripcion,
            String ubicacion,
            LocalDateTime fechaHoraInicio,
            LocalDateTime fechaHoraFin,
            String comentarios
    ) {
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
        this.comentarios = comentarios;
    }

    // Getters y Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getResidente() {
        return residente;
    }

    public void setResidente(String residente) {
        this.residente = residente;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTecnico() {
        return tecnico;
    }

    public void setTecnico(String tecnico) {
        this.tecnico = tecnico;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    public void setFechaHoraInicio(LocalDateTime fechaHoraInicio) {
        this.fechaHoraInicio = fechaHoraInicio;
    }

    public LocalDateTime getFechaHoraFin() {
        return fechaHoraFin;
    }

    public void setFechaHoraFin(LocalDateTime fechaHoraFin) {
        this.fechaHoraFin = fechaHoraFin;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    @Override
    public String toString() {
        return "Mantenimiento{" +
                "id=" + id +
                ", fecha='" + fecha + '\'' +
                ", residente='" + residente + '\'' +
                ", categoria='" + categoria + '\'' +
                ", prioridad='" + prioridad + '\'' +
                ", estado='" + estado + '\'' +
                ", tecnico='" + tecnico + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", ubicacion='" + ubicacion + '\'' +
                ", fechaHoraInicio=" + fechaHoraInicio +
                ", fechaHoraFin=" + fechaHoraFin +
                ", comentarios='" + comentarios + '\'' +
                '}';
    }
}