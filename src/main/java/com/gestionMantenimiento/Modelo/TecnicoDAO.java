package com.gestionMantenimiento.Modelo;

import com.gestionMantenimiento.Util.ConexionBD;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TecnicoDAO {

    // ─── Ajusta estos valores según tu configuración ───────────────────────────
    private static final String MONGO_URI  = "mongodb://localhost:27017";
    private static final String MONGO_DB   = "gestionMantenimiento";
    private static final String MONGO_COL  = "tecnicos";
    // ───────────────────────────────────────────────────────────────────────────

    // ════════════════════════════════════════════════════════════════════════════
    //  MySQL / SQLite  (reutiliza ConexionBD igual que MantenimientoDAO)
    // ════════════════════════════════════════════════════════════════════════════

    /** Devuelve todos los técnicos de la base relacional (MySQL o SQLite). */
    public List<Tecnico> obtenerTecnicos() {

        List<Tecnico> lista = new ArrayList<>();
        String sql = "SELECT * FROM tecnicos";

        try (Connection conn = ConexionBD.nuevaConexion();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearFila(rs));
            }

            System.out.println("Técnicos cargados: " + lista.size());

        } catch (SQLException e) {
            System.out.println("Error al obtener técnicos");
            e.printStackTrace();
        }

        return lista;
    }

    /** Devuelve solo los técnicos con estado = 'Activo'. */
    public List<Tecnico> obtenerTecnicosActivos() {

        List<Tecnico> lista = new ArrayList<>();
        String sql = "SELECT * FROM tecnicos WHERE estado = 'Activo'";

        try (Connection conn = ConexionBD.nuevaConexion();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearFila(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener técnicos activos");
            e.printStackTrace();
        }

        return lista;
    }

    public Tecnico obtenerPorId(int id) {

        String sql = "SELECT * FROM tecnicos WHERE id = ?";

        try (Connection conn = ConexionBD.nuevaConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapearFila(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar técnico por id");
            e.printStackTrace();
        }

        return null;
    }

    public boolean insertar(Tecnico t) {

        String sql = "INSERT INTO tecnicos " +
                "(nombre, apellido, especialidad, telefono, email, estado, fecha_ingreso, usuario_login) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.nuevaConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setParams(stmt, t);
            int filas = stmt.executeUpdate();
            System.out.println("Técnico insertado. Filas: " + filas);
            return filas > 0;

        } catch (SQLException e) {
            System.out.println("Error al insertar técnico");
            e.printStackTrace();
        }

        return false;
    }

    public boolean actualizar(Tecnico t) {

        String sql = "UPDATE tecnicos SET " +
                "nombre=?, apellido=?, especialidad=?, telefono=?, email=?, " +
                "estado=?, fecha_ingreso=?, usuario_login=? " +
                "WHERE id=?";

        try (Connection conn = ConexionBD.nuevaConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setParams(stmt, t);
            stmt.setInt(9, t.getId());
            int filas = stmt.executeUpdate();
            System.out.println("Técnico actualizado. Filas: " + filas);
            return filas > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar técnico");
            e.printStackTrace();
        }

        return false;
    }

    public boolean eliminar(int id) {

        String sql = "DELETE FROM tecnicos WHERE id=?";

        try (Connection conn = ConexionBD.nuevaConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int filas = stmt.executeUpdate();
            System.out.println("Técnico eliminado. Filas: " + filas);
            return filas > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar técnico");
            e.printStackTrace();
        }

        return false;
    }

    // ── Helpers relacionales ──────────────────────────────────────────────────

    private Tecnico mapearFila(ResultSet rs) throws SQLException {

        LocalDate fechaIngreso = null;
        String fechaStr = rs.getString("fecha_ingreso");
        if (fechaStr != null && !fechaStr.isBlank()) {
            try { fechaIngreso = LocalDate.parse(fechaStr.substring(0, 10)); }
            catch (Exception ignored) {}
        }

        return new Tecnico(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("especialidad"),
                rs.getString("telefono"),
                rs.getString("email"),
                rs.getString("estado"),
                fechaIngreso,
                rs.getString("usuario_login")
        );
    }

    /** Asigna los 8 parámetros comunes (sin id) al PreparedStatement. */
    private void setParams(PreparedStatement stmt, Tecnico t) throws SQLException {
        stmt.setString(1, t.getNombre());
        stmt.setString(2, t.getApellido());
        stmt.setString(3, t.getEspecialidad());
        stmt.setString(4, t.getTelefono());
        stmt.setString(5, t.getEmail());
        stmt.setString(6, t.getEstado());
        stmt.setString(7, t.getFechaIngreso() != null ? t.getFechaIngreso().toString() : null);
        stmt.setString(8, t.getUsuarioLogin());
    }

    // ════════════════════════════════════════════════════════════════════════════
    //  MongoDB
    // ════════════════════════════════════════════════════════════════════════════

    /** Devuelve todos los técnicos guardados en MongoDB. */
    public List<Tecnico> obtenerTecnicosMongo() {

        List<Tecnico> lista = new ArrayList<>();

        try (MongoClient client = MongoClients.create(MONGO_URI)) {

            MongoDatabase db  = client.getDatabase(MONGO_DB);
            MongoCollection<Document> col = db.getCollection(MONGO_COL);

            for (Document doc : col.find()) {
                lista.add(docToTecnico(doc));
            }

            System.out.println("Técnicos cargados desde MongoDB: " + lista.size());

        } catch (Exception e) {
            System.out.println("Error al obtener técnicos de MongoDB");
            e.printStackTrace();
        }

        return lista;
    }

    /** Inserta un técnico en MongoDB. */
    public boolean insertarMongo(Tecnico t) {

        try (MongoClient client = MongoClients.create(MONGO_URI)) {

            MongoDatabase db  = client.getDatabase(MONGO_DB);
            MongoCollection<Document> col = db.getCollection(MONGO_COL);

            col.insertOne(tecnicoToDoc(t));
            System.out.println("Técnico insertado en MongoDB");
            return true;

        } catch (Exception e) {
            System.out.println("Error al insertar técnico en MongoDB");
            e.printStackTrace();
        }

        return false;
    }

    // ── Helpers Mongo ─────────────────────────────────────────────────────────

    private Tecnico docToTecnico(Document doc) {

        LocalDate fechaIngreso = null;
        String fechaStr = doc.getString("fecha_ingreso");
        if (fechaStr != null && !fechaStr.isBlank()) {
            try { fechaIngreso = LocalDate.parse(fechaStr.substring(0, 10)); }
            catch (Exception ignored) {}
        }

        return new Tecnico(
                0,                                      // MongoDB no usa int id
                doc.getString("nombre"),
                doc.getString("apellido"),
                doc.getString("especialidad"),
                doc.getString("telefono"),
                doc.getString("email"),
                doc.getString("estado"),
                fechaIngreso,
                doc.getString("usuario_login")
        );
    }

    private Document tecnicoToDoc(Tecnico t) {
        return new Document("nombre",        t.getNombre())
                .append("apellido",      t.getApellido())
                .append("especialidad",  t.getEspecialidad())
                .append("telefono",      t.getTelefono())
                .append("email",         t.getEmail())
                .append("estado",        t.getEstado())
                .append("fecha_ingreso", t.getFechaIngreso() != null ? t.getFechaIngreso().toString() : null)
                .append("usuario_login", t.getUsuarioLogin());
    }
}