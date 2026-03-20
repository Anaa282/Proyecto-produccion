package com.gestionMantenimiento.Util;

public class SessionManager {

    public enum Rol { ADMIN, RESIDENTE, TECNICO }

    private static Rol rolActual;
    private static String nombreUsuario;  // para técnicos: "López", "Martínez", "García"

    public static void setRol(Rol rol) { rolActual = rol; }
    public static Rol getRol() { return rolActual; }

    public static void setNombreUsuario(String nombre) { nombreUsuario = nombre; }
    public static String getNombreUsuario() { return nombreUsuario; }

    public static boolean esAdmin()     { return rolActual == Rol.ADMIN; }
    public static boolean esResidente() { return rolActual == Rol.RESIDENTE; }
    public static boolean esTecnico()   { return rolActual == Rol.TECNICO; }

    public static void cerrarSesion() {
        rolActual = null;
        nombreUsuario = null;
    }
}