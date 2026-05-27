package com.gestionMantenimiento.Modelo;

import java.time.LocalDate;

public class Tecnico {

    private int id;
    private String nombre;
    private String apellido;
    private String especialidad;
    private String telefono;
    private String email;
    private String estado;
    private LocalDate fechaIngreso;
    private String usuarioLogin;

    public Tecnico() {}

    public Tecnico(int id, String nombre, String apellido, String especialidad,
                   String telefono, String email, String estado,
                   LocalDate fechaIngreso, String usuarioLogin) {
        this.id           = id;
        this.nombre       = nombre;
        this.apellido     = apellido;
        this.especialidad = especialidad;
        this.telefono     = telefono;
        this.email        = email;
        this.estado       = estado;
        this.fechaIngreso = fechaIngreso;
        this.usuarioLogin = usuarioLogin;
    }

    // Nombre completo — útil para comparar con el campo "tecnico" de mantenimiento
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    // Getters
    public int getId()                { return id; }
    public String getNombre()         { return nombre; }
    public String getApellido()       { return apellido; }
    public String getEspecialidad()   { return especialidad; }
    public String getTelefono()       { return telefono; }
    public String getEmail()          { return email; }
    public String getEstado()         { return estado; }
    public LocalDate getFechaIngreso(){ return fechaIngreso; }
    public String getUsuarioLogin()   { return usuarioLogin; }

    // Setters
    public void setId(int id)                        { this.id = id; }
    public void setNombre(String nombre)             { this.nombre = nombre; }
    public void setApellido(String apellido)         { this.apellido = apellido; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
    public void setTelefono(String telefono)         { this.telefono = telefono; }
    public void setEmail(String email)               { this.email = email; }
    public void setEstado(String estado)             { this.estado = estado; }
    public void setFechaIngreso(LocalDate f)         { this.fechaIngreso = f; }
    public void setUsuarioLogin(String u)            { this.usuarioLogin = u; }
}