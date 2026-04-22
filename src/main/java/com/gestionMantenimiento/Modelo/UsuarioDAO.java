package com.gestionMantenimiento.Modelo;

import com.gestionMantenimiento.Util.ConexionBD;
import com.gestionMantenimiento.Util.SessionManager;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class UsuarioDAO {

    // Map: usuario -> {contraseña, rol, nombreMostrar}
    private static final Map<String, String[]> USUARIOS = new HashMap<>();

    static {
        USUARIOS.put("admin",     new String[]{"admin123",  "ADMIN",     "Administrador"});
        USUARIOS.put("residente", new String[]{"res123",    "RESIDENTE", "Residente"});
        USUARIOS.put("lopez",     new String[]{"tec123",    "TECNICO",   "López"});
        USUARIOS.put("martinez",  new String[]{"tec123",    "TECNICO",   "Martínez"});
        USUARIOS.put("garcia",    new String[]{"tec123",    "TECNICO",   "García"});
    }

    /**
     * Valida credenciales.
     */
    public static boolean login(String usuario, String contrasena) {
        String[] datos = USUARIOS.get(usuario.toLowerCase().trim());
        if (datos == null) return false;
        if (!datos[0].equals(contrasena)) return false;

        SessionManager.setRol(SessionManager.Rol.valueOf(datos[1]));
        SessionManager.setNombreUsuario(datos[2]);
        return true;
    }
    Connection conn = ConexionBD.getConexion();
}