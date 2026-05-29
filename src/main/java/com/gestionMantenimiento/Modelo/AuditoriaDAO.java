package com.gestionMantenimiento.Modelo;

import com.gestionMantenimiento.Util.ConexionMongo;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * DAO para la colección "auditoria" en MongoDB.
 * Registra SIEMPRE en MongoDB, sin importar qué BD principal esté activa.
 */
public class AuditoriaDAO {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Garantiza que MongoDB esté conectado.
     * Si ya estaba conectado, no hace nada; si no, lo conecta automáticamente.
     * La auditoría SIEMPRE se registra en MongoDB, sin importar qué BD principal esté activa.
     */
    private MongoCollection<Document> getColeccion() {
        MongoDatabase db = ConexionMongo.getDB();
        if (db == null) {
            System.out.println(" [AUDITORÍA] Reconectando a MongoDB para auditoría...");
            db = ConexionMongo.conectar();
        }
        if (db == null) {
            throw new IllegalStateException("[AUDITORÍA ERROR] No se pudo conectar a MongoDB");
        }
        return db.getCollection("auditoria");
    }

    /**
     * Registra cualquier cambio relevante en MongoDB.
     * Tipo puede ser: "CREACION", "ACTUALIZACION", "FINALIZACION"
     * 
     * IMPORTANTE: La auditoría SIEMPRE se registra en MongoDB,
     * sin importar si estás usando SQLite, MySQL o MongoDB como BD principal.
     */
    public boolean registrar(Mantenimiento m, String tipo) {
        try {
            Document doc = new Document()
                    .append("tipo_evento",       tipo.toUpperCase())
                    .append("mantenimiento_id",  m.getId())
                    .append("fecha",             m.getFecha())
                    .append("residente",         m.getResidente())
                    .append("categoria",         m.getCategoria())
                    .append("prioridad",         m.getPrioridad())
                    .append("estado",            m.getEstado())
                    .append("tecnico",           m.getTecnico())
                    .append("descripcion",       m.getDescripcion())
                    .append("ubicacion",         m.getUbicacion())
                    .append("comentarios",       m.getComentarios())
                    .append("fecha_evento",      LocalDateTime.now().format(FMT));

            if (m.getFechaHoraInicio() != null)
                doc.append("fecha_hora_inicio", m.getFechaHoraInicio().format(FMT));
            if (m.getFechaHoraFin() != null)
                doc.append("fecha_hora_fin", m.getFechaHoraFin().format(FMT));

            // Horas de resolución solo si está finalizado
            if ("FINALIZACION".equalsIgnoreCase(tipo)) {
                doc.append("horas_resolucion", m.getTiempoSolucionHoras());
            }

            getColeccion().insertOne(doc);
            System.out.println(" ✓ [AUDITORÍA MONGO] " + tipo + " registrado para id=" + m.getId());
            return true;

        } catch (Exception e) {
            System.out.println(" ✗ [AUDITORÍA ERROR] " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Sobrecarga para compatibilidad con código anterior.
     * Por defecto trata el registro como FINALIZACION.
     */
    public boolean registrar(Mantenimiento m) {
        return registrar(m, "FINALIZACION");
    }

    /**
     * Devuelve todos los registros de auditoría desde MongoDB.
     * MongoDB es el repositorio centralizado de toda la auditoría.
     */
    public List<Mantenimiento> obtenerTodos() {
        List<Mantenimiento> lista = new ArrayList<>();
        try {
            for (Document doc : getColeccion().find()) {
                lista.add(documentoAMantenimiento(doc));
            }
            System.out.println(" ✓ [AUDITORÍA] " + lista.size() + " registros cargados desde MongoDB");
        } catch (Exception e) {
            System.out.println(" ✗ [AUDITORÍA ERROR] Al leer auditoría: " + e.getMessage());
        }
        return lista;
    }

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
    public boolean registrarTecnico(Tecnico tecnico, String tipo, String adminUser) {
        try {
            Document doc = new Document()
                    .append("tipo_evento",   tipo.toUpperCase())
                    .append("entidad",       "TECNICO")
                    .append("tecnico_id",    tecnico.getId())
                    .append("nombre",        tecnico.getNombre())
                    .append("apellido",      tecnico.getApellido())
                    .append("especialidad",  tecnico.getEspecialidad())
                    .append("telefono",      tecnico.getTelefono())
                    .append("email",         tecnico.getEmail())
                    .append("estado",        tecnico.getEstado())
                    .append("fecha_ingreso", tecnico.getFechaIngreso() != null
                            ? tecnico.getFechaIngreso().toString() : null)
                    .append("usuario_login", tecnico.getUsuarioLogin())
                    .append("admin_accion",  adminUser)
                    .append("fecha_evento",  LocalDateTime.now().format(FMT));

            getColeccion().insertOne(doc);
            System.out.println(" ✓ [AUDITORÍA MONGO] " + tipo
                    + " → técnico id=" + tecnico.getId()
                    + " | admin=" + adminUser);
            return true;

        } catch (Exception e) {
            System.out.println(" ✗ [AUDITORÍA ERROR] registrarTecnico: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}