package com.gestionMantenimiento.Util;

public class SessionManager {

    public enum Rol { ADMIN, USUARIO }

    private static Rol rolActual;

    public static void setRol(Rol rol) {
        rolActual = rol;
    }

    public static Rol getRol() {
        return rolActual;
    }

    public static boolean esAdmin() {
        return rolActual == Rol.ADMIN;
    }
}