package com.gestionMantenimiento.Modelo;

import com.gestionMantenimiento.Modelo.Mantenimiento;
import com.gestionMantenimiento.Util.ConexionBD;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;

public class MantenimientoDAO {

    // Obtener todos los mantenimientos
    public List<Mantenimiento> obtenerMantenimientos() {

        List<Mantenimiento> lista = new ArrayList<>();

        String sql = "SELECT * FROM mantenimiento";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                Mantenimiento m = new Mantenimiento(

                        rs.getInt("id"),
                        rs.getString("fecha"),
                        rs.getString("residente"),
                        rs.getString("categoria"),
                        rs.getString("prioridad"),
                        rs.getString("estado"),
                        rs.getString("tecnico"),
                        rs.getString("descripcion"),
                        rs.getString("ubicacion"),
                        rs.getTimestamp("fecha_hora_inicio") != null ?
                                rs.getTimestamp("fecha_hora_inicio").toLocalDateTime() : null,
                        rs.getTimestamp("fecha_hora_fin") != null ?
                                rs.getTimestamp("fecha_hora_fin").toLocalDateTime() : null,
                        rs.getString("comentarios")
                );

                lista.add(m);
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener mantenimientos");
            e.printStackTrace();
        }

        return lista;
    }

    // Obtener mantenimiento por ID
    public Mantenimiento obtenerPorId(int id) {

        String sql = "SELECT * FROM mantenimiento WHERE id = ?";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();

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
                        rs.getTimestamp("fecha_hora_inicio") != null ?
                                rs.getTimestamp("fecha_hora_inicio").toLocalDateTime() : null,
                        rs.getTimestamp("fecha_hora_fin") != null ?
                                rs.getTimestamp("fecha_hora_fin").toLocalDateTime() : null,
                        rs.getString("comentarios")
                );
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar mantenimiento");
            e.printStackTrace();
        }

        return null;
    }

    // Insertar mantenimiento
    public boolean insertar(Mantenimiento m) {

        String sql = "INSERT INTO mantenimiento " +
                "(fecha, residente, categoria, prioridad, estado, tecnico, descripcion, ubicacion, fecha_hora_inicio, fecha_hora_fin, comentarios) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, m.getFecha());
            stmt.setString(2, m.getResidente());
            stmt.setString(3, m.getCategoria());
            stmt.setString(4, m.getPrioridad());
            stmt.setString(5, m.getEstado());
            stmt.setString(6, m.getTecnico());
            stmt.setString(7, m.getDescripcion());
            stmt.setString(8, m.getUbicacion());

            if (m.getFechaHoraInicio() != null)
                stmt.setTimestamp(9, Timestamp.valueOf(m.getFechaHoraInicio()));
            else
                stmt.setTimestamp(9, null);

            if (m.getFechaHoraFin() != null)
                stmt.setTimestamp(10, Timestamp.valueOf(m.getFechaHoraFin()));
            else
                stmt.setTimestamp(10, null);

            stmt.setString(11, m.getComentarios());

            stmt.executeUpdate();

            return true;

        } catch (SQLException e) {
            System.out.println("Error al insertar mantenimiento");
            e.printStackTrace();
        }

        return false;
    }

    // Actualizar mantenimiento
    public boolean actualizar(Mantenimiento m) {

        String sql = "UPDATE mantenimiento SET " +
                "fecha=?, residente=?, categoria=?, prioridad=?, estado=?, tecnico=?, descripcion=?, ubicacion=?, fecha_hora_inicio=?, fecha_hora_fin=?, comentarios=? " +
                "WHERE id=?";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, m.getFecha());
            stmt.setString(2, m.getResidente());
            stmt.setString(3, m.getCategoria());
            stmt.setString(4, m.getPrioridad());
            stmt.setString(5, m.getEstado());
            stmt.setString(6, m.getTecnico());
            stmt.setString(7, m.getDescripcion());
            stmt.setString(8, m.getUbicacion());

            stmt.setTimestamp(9, Timestamp.valueOf(m.getFechaHoraInicio()));
            stmt.setTimestamp(10, Timestamp.valueOf(m.getFechaHoraFin()));

            stmt.setString(11, m.getComentarios());
            stmt.setInt(12, m.getId());

            stmt.executeUpdate();

            return true;

        } catch (SQLException e) {
            System.out.println("Error al actualizar mantenimiento");
            e.printStackTrace();
        }

        return false;
    }

    // Eliminar mantenimiento
    public boolean eliminar(int id) {

        String sql = "DELETE FROM mantenimiento WHERE id=?";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            stmt.executeUpdate();

            return true;

        } catch (SQLException e) {
            System.out.println("Error al eliminar mantenimiento");
            e.printStackTrace();
        }

        return false;
    }

    }