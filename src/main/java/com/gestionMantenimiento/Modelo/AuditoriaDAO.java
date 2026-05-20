package com.gestionMantenimiento.Modelo;

import com.gestionMantenimiento.Util.ConexionBD;
import com.gestionMantenimiento.Util.ConexionMongo;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * DAO para la auditoría que funciona con MySQL, SQLite o MongoDB.
 * Registra automáticamente en la BD activa.
 */
public class AuditoriaDAO {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Registra un evento de auditoría en la BD activa Y siempre en MongoDB
     * Esto asegura que todos los eventos queden centralizados en MongoDB
     */
    public boolean registrar(Mantenimiento m) {
        return registrar(m, "FINALIZACION");
    }

    /**
     * Registra un evento de auditoría con tipo especificado
     * @param m Mantenimiento a registrar
     * @param tipo CREACION, ACTUALIZACION, FINALIZACION
     */
    public boolean registrar(Mantenimiento m, String tipo) {
        boolean resultadoLocal = true;
        boolean resultadoMongo = true;

        String tipoConexion = ConexionBD.getTipo();
        
        // 1. Registra en la BD actual (si es SQL)
        if ("MongoDB".equalsIgnoreCase(tipoConexion)) {
            // Si ya es MongoDB, solo registra una vez
            resultadoMongo = registrarMongo(m, tipo);
        } else {
            // Si es MySQL o SQLite, registra en ambas
            resultadoLocal = registrarSQL(m, tipo);
            resultadoMongo = registrarMongo(m, tipo);
        }

        return resultadoLocal && resultadoMongo;
    }

    /**
     * Registra en MongoDB
     */
    private boolean registrarMongo(Mantenimiento m, String tipo) {
        try {
            MongoDatabase db = ConexionMongo.getDB();
            if (db == null) throw new IllegalStateException("MongoDB no está conectado.");
            
            MongoCollection<Document> coleccion = db.getCollection("auditoria");
            
            Document doc = new Document()
                    .append("mantenimiento_id",  m.getId())
                    .append("tipo_evento",       tipo.toUpperCase())
                    .append("fecha_evento",      LocalDateTime.now().format(FMT))
                    .append("fecha",             m.getFecha())
                    .append("residente",         m.getResidente())
                    .append("categoria",         m.getCategoria())
                    .append("prioridad",         m.getPrioridad())
                    .append("estado",            m.getEstado())
                    .append("tecnico",           m.getTecnico())
                    .append("descripcion",       m.getDescripcion())
                    .append("ubicacion",         m.getUbicacion())
                    .append("comentarios",       m.getComentarios());

            if (m.getFechaHoraInicio() != null)
                doc.append("fecha_hora_inicio", m.getFechaHoraInicio().format(FMT));
            if (m.getFechaHoraFin() != null)
                doc.append("fecha_hora_fin", m.getFechaHoraFin().format(FMT));

            if ("FINALIZACION".equalsIgnoreCase(tipo)) {
                doc.append("horas_resolucion", m.getTiempoSolucionHoras());
                doc.append("fecha_finalizacion", LocalDateTime.now().format(FMT));
            }

            coleccion.insertOne(doc);
            System.out.println(" ✓ Auditoría registrada en MongoDB [" + tipo + "] para mantenimiento id=" + m.getId());
            return true;
        } catch (Exception e) {
            System.out.println(" ✗ Error al registrar auditoría en MongoDB: " + e.getMessage());
            return false;
        }
    }

    /**
     * Registra en SQL (MySQL o SQLite)
     */
    private boolean registrarSQL(Mantenimiento m, String tipo) {
        Connection conn = null;
        try {
            conn = ConexionBD.nuevaConexion();
            if (conn == null) {
                System.out.println(" ✗ Error: No hay conexión a BD SQL");
                return false;
            }

            String sql = "INSERT INTO auditoria " +
                    "(mantenimiento_id, tipo_evento, fecha_evento, fecha, residente, categoria, prioridad, " +
                    "estado, tecnico, descripcion, ubicacion, comentarios, fecha_hora_inicio, fecha_hora_fin, " +
                    "horas_resolucion, fecha_finalizacion) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement stmt = conn.prepareStatement(sql);
            LocalDateTime ahora = LocalDateTime.now();
            
            stmt.setInt(1, m.getId());
            stmt.setString(2, tipo.toUpperCase());
            stmt.setTimestamp(3, Timestamp.valueOf(ahora));
            stmt.setString(4, m.getFecha() != null ? m.getFecha() : "");
            stmt.setString(5, m.getResidente() != null ? m.getResidente() : "");
            stmt.setString(6, m.getCategoria() != null ? m.getCategoria() : "");
            stmt.setString(7, m.getPrioridad() != null ? m.getPrioridad() : "");
            stmt.setString(8, m.getEstado() != null ? m.getEstado() : "");
            stmt.setString(9, m.getTecnico() != null ? m.getTecnico() : "");
            stmt.setString(10, m.getDescripcion() != null ? m.getDescripcion() : "");
            stmt.setString(11, m.getUbicacion() != null ? m.getUbicacion() : "");
            stmt.setString(12, m.getComentarios() != null ? m.getComentarios() : "");
            
            if (m.getFechaHoraInicio() != null) {
                stmt.setTimestamp(13, Timestamp.valueOf(m.getFechaHoraInicio()));
            } else {
                stmt.setNull(13, Types.TIMESTAMP);
            }

            if (m.getFechaHoraFin() != null) {
                stmt.setTimestamp(14, Timestamp.valueOf(m.getFechaHoraFin()));
            } else {
                stmt.setNull(14, Types.TIMESTAMP);
            }

            // Horas de resolución
            if ("FINALIZACION".equalsIgnoreCase(tipo)) {
                stmt.setLong(15, m.getTiempoSolucionHoras());
                stmt.setTimestamp(16, Timestamp.valueOf(ahora));
            } else {
                stmt.setNull(15, Types.BIGINT);
                stmt.setNull(16, Types.TIMESTAMP);
            }

            int filas = stmt.executeUpdate();
            stmt.close();
            String bdActual = ConexionBD.getTipo() != null ? ConexionBD.getTipo() : "SQL";
            System.out.println(" ✓ Auditoría registrada en " + bdActual + " [" + tipo + "] para mantenimiento id=" + m.getId());
            return filas > 0;

        } catch (SQLException e) {
            System.out.println(" ✗ Error al registrar auditoría en SQL: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { /* ignore */ }
            }
        }
    }

    /**
     * Obtiene todos los registros de auditoría desde MongoDB (fuente centralizada)
     * Todos los eventos de cualquier BD quedan registrados en MongoDB
     */
    public List<Mantenimiento> obtenerTodos() {
        // Siempre obtiene desde MongoDB (es la fuente centralizada)
        return obtenerTodosMongo();
    }

    /**
     * Obtiene todos los registros de auditoría desde MongoDB
     */
    private List<Mantenimiento> obtenerTodosMongo() {
        List<Mantenimiento> lista = new ArrayList<>();
        try {
            MongoDatabase db = ConexionMongo.getDB();
            if (db == null) throw new IllegalStateException("MongoDB no está conectado.");
            
            MongoCollection<Document> coleccion = db.getCollection("auditoria");
            
            for (Document doc : coleccion.find()) {
                lista.add(documentoAMantenimiento(doc));
            }
            System.out.println(" ✓ Auditoría centralizada en MongoDB: " + lista.size() + " registros cargados.");
        } catch (Exception e) {
            System.out.println(" ✗ Error al leer auditoría de MongoDB: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Obtiene auditoría desde SQL (respaldo si MongoDB no está disponible)
     * NOTA: El método obtenerTodos() siempre obtiene desde MongoDB
     */
    private List<Mantenimiento> obtenerTodosSQL() {
        List<Mantenimiento> lista = new ArrayList<>();
        String sql = "SELECT * FROM auditoria ORDER BY fecha_evento DESC";
        
        Connection conn = null;
        try {
            conn = ConexionBD.nuevaConexion();
            if (conn == null) {
                System.out.println(" ✗ Error: No hay conexión a BD SQL");
                return lista;
            }

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(new Mantenimiento(
                        rs.getInt("mantenimiento_id"),
                        rs.getString("fecha") != null ? rs.getString("fecha") : "",
                        rs.getString("residente") != null ? rs.getString("residente") : "",
                        rs.getString("categoria") != null ? rs.getString("categoria") : "",
                        rs.getString("prioridad") != null ? rs.getString("prioridad") : "",
                        rs.getString("estado") != null ? rs.getString("estado") : "",
                        rs.getString("tecnico") != null ? rs.getString("tecnico") : "",
                        rs.getString("descripcion") != null ? rs.getString("descripcion") : "",
                        rs.getString("ubicacion") != null ? rs.getString("ubicacion") : "",
                        convertirFecha(rs.getObject("fecha_hora_inicio")),
                        convertirFecha(rs.getObject("fecha_hora_fin")),
                        rs.getString("comentarios") != null ? rs.getString("comentarios") : ""
                ));
            }

            rs.close();
            stmt.close();
            System.out.println(" ✓ Auditoría SQL (respaldo): " + lista.size() + " registros cargados.");

        } catch (SQLException e) {
            System.out.println(" ✗ Error al leer auditoría de SQL: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { /* ignore */ }
            }
        }
        
        return lista;
    }

    /**
     * Convierte resultado SQL a LocalDateTime
     */
    private LocalDateTime convertirFecha(Object obj) {
        if (obj == null) return null;

        try {
            if (obj instanceof Timestamp) {
                return ((Timestamp) obj).toLocalDateTime();
            }
        } catch (Exception e) {
            System.out.println(" Error convirtiendo fecha: " + obj);
        }
        return null;
    }

    /**
     * Convierte Document MongoDB a Mantenimiento
     */
    private Mantenimiento documentoAMantenimiento(Document doc) {
        LocalDateTime inicio = parsearFechaHora(doc.get("fecha_hora_inicio"));
        LocalDateTime fin    = parsearFechaHora(doc.get("fecha_hora_fin"));

        int id = 0;
        if (doc.get("mantenimiento_id") != null) {
            id = doc.getInteger("mantenimiento_id");
        }

        return new Mantenimiento(
                id,
                str(doc, "fecha"),
                str(doc, "residente"),
                str(doc, "categoria"),
                str(doc, "prioridad"),
                str(doc, "estado"),
                str(doc, "tecnico"),
                str(doc, "descripcion"),
                str(doc, "ubicacion"),
                inicio,
                fin,
                str(doc, "comentarios")
        );
    }

    private String str(Document doc, String campo) {
        Object v = doc.get(campo);
        if (v == null) return "";
        if (v instanceof String) return (String) v;
        if (v instanceof Date) {
            return ((Date) v).toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate().toString();
        }
        return v.toString();
    }

    private LocalDateTime parsearFechaHora(Object valor) {
        if (valor == null) return null;
        if (valor instanceof String) {
            String s = (String) valor;
            if (s.isEmpty()) return null;
            try { return LocalDateTime.parse(s, FMT); }
            catch (Exception e) {
                try { return LocalDateTime.parse(s); }
                catch (Exception ex) { return null; }
            }
        }
        if (valor instanceof Date) {
            return ((Date) valor).toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        }
        return null;
    }
}










