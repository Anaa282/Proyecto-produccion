package com.gestionMantenimiento.Modelo;

import com.gestionMantenimiento.Util.ConexionBD;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MantenimientoDAO {

    // 🔧 MÉTODO CENTRAL PARA CONVERTIR FECHAS (CLAVE)
    private LocalDateTime convertirFecha(Object obj) {
        if (obj == null) return null;

        try {
            if (obj instanceof Long) {
                return new Timestamp((Long) obj).toLocalDateTime();
            } else if (obj instanceof String) {
                return Timestamp.valueOf((String) obj).toLocalDateTime();
            } else if (obj instanceof Timestamp) {
                return ((Timestamp) obj).toLocalDateTime();
            }
        } catch (Exception e) {
            System.out.println(" Error convirtiendo fecha: " + obj);
            e.printStackTrace();
        }

        return null;
    }

    // 🔹 OBTENER TODOS
    public List<Mantenimiento> obtenerMantenimientos() {

        List<Mantenimiento> lista = new ArrayList<>();
        String sql = "SELECT * FROM mantenimiento";

        try (Connection conn = ConexionBD.nuevaConexion();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                lista.add(new Mantenimiento(
                        rs.getInt("id"),
                        rs.getString("fecha"),
                        rs.getString("residente"),
                        rs.getString("categoria"),
                        rs.getString("prioridad"),
                        rs.getString("estado"),
                        rs.getString("tecnico"),
                        rs.getString("descripcion"),
                        rs.getString("ubicacion"),
                        convertirFecha(rs.getObject("fecha_hora_inicio")),
                        convertirFecha(rs.getObject("fecha_hora_fin")),
                        rs.getString("comentarios")
                ));
            }

            System.out.println(" Se cargaron " + lista.size() + " registros");

        } catch (SQLException e) {
            System.out.println(" Error al obtener mantenimientos");
            e.printStackTrace();
        }

        return lista;
    }

    // 🔹 OBTENER POR ID
    public Mantenimiento obtenerPorId(int id) {

        String sql = "SELECT * FROM mantenimiento WHERE id = ?";

        try (Connection conn = ConexionBD.nuevaConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return new Mantenimiento(
                            rs.getInt("id"),
                            rs.getString("fecha"),
                            rs.getString("residente"),
                            rs.getString("categoria"),
                            rs.getString("prioridad"),
                            rs.getString("estado"),
                            rs.getString("tecnico"),
                            rs.getString("descripcion"),
                            rs.getString("ubicacion"),
                            convertirFecha(rs.getObject("fecha_hora_inicio")),
                            convertirFecha(rs.getObject("fecha_hora_fin")),
                            rs.getString("comentarios")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println(" Error al buscar mantenimiento");
            e.printStackTrace();
        }

        return null;
    }

    // 🔹 INSERTAR
    public boolean insertar(Mantenimiento m) {

        String sql = "INSERT INTO mantenimiento " +
                "(fecha, residente, categoria, prioridad, estado, tecnico, descripcion, ubicacion, fecha_hora_inicio, fecha_hora_fin, comentarios) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.nuevaConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, m.getFecha());
            stmt.setString(2, m.getResidente());
            stmt.setString(3, m.getCategoria());
            stmt.setString(4, m.getPrioridad());
            stmt.setString(5, m.getEstado());
            stmt.setString(6, m.getTecnico());
            stmt.setString(7, m.getDescripcion());
            stmt.setString(8, m.getUbicacion());

            stmt.setTimestamp(9,
                    m.getFechaHoraInicio() != null ? Timestamp.valueOf(m.getFechaHoraInicio()) : null);

            stmt.setTimestamp(10,
                    m.getFechaHoraFin() != null ? Timestamp.valueOf(m.getFechaHoraFin()) : null);

            stmt.setString(11, m.getComentarios());

            int filas = stmt.executeUpdate();
            System.out.println(" Filas insertadas: " + filas);

            return filas > 0;

        } catch (SQLException e) {
            System.out.println(" Error al insertar mantenimiento");
            e.printStackTrace();
        }

        return false;
    }

    // 🔹 ACTUALIZAR
    public boolean actualizar(Mantenimiento m) {

        String sql = "UPDATE mantenimiento SET " +
                "fecha=?, residente=?, categoria=?, prioridad=?, estado=?, tecnico=?, descripcion=?, ubicacion=?, fecha_hora_inicio=?, fecha_hora_fin=?, comentarios=? " +
                "WHERE id=?";

        try (Connection conn = ConexionBD.nuevaConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, m.getFecha());
            stmt.setString(2, m.getResidente());
            stmt.setString(3, m.getCategoria());
            stmt.setString(4, m.getPrioridad());
            stmt.setString(5, m.getEstado());
            stmt.setString(6, m.getTecnico());
            stmt.setString(7, m.getDescripcion());
            stmt.setString(8, m.getUbicacion());

            stmt.setTimestamp(9,
                    m.getFechaHoraInicio() != null ? Timestamp.valueOf(m.getFechaHoraInicio()) : null);

            stmt.setTimestamp(10,
                    m.getFechaHoraFin() != null ? Timestamp.valueOf(m.getFechaHoraFin()) : null);

            stmt.setString(11, m.getComentarios());
            stmt.setInt(12, m.getId());

            int filas = stmt.executeUpdate();
            System.out.println(" Filas actualizadas: " + filas);

            return filas > 0;

        } catch (SQLException e) {
            System.out.println(" Error al actualizar mantenimiento");
            e.printStackTrace();
        }

        return false;
    }

    // 🔹 ELIMINAR
    public boolean eliminar(int id) {

        String sql = "DELETE FROM mantenimiento WHERE id=?";

        try (Connection conn = ConexionBD.nuevaConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int filas = stmt.executeUpdate();
            System.out.println(" Filas eliminadas: " + filas);

            return filas > 0;

        } catch (SQLException e) {
            System.out.println(" Error al eliminar mantenimiento");
            e.printStackTrace();
        }

        return false;
    }
}