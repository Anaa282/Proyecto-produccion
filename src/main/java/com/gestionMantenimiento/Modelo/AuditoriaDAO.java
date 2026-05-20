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
     * Si ya estaba conectado, no hace nada; si no, lo conecta.
     */
    private MongoCollection<Document> getColeccion() {
        MongoDatabase db = ConexionMongo.getDB();
        if (db == null) {
            System.out.println(" MongoDB no estaba conectado — conectando para auditoría...");
            db = ConexionMongo.conectar();
        }
        if (db == null) {
            throw new IllegalStateException("No se pudo conectar a MongoDB para auditoría.");
        }
        return db.getCollection("auditoria");
    }

    /**
     * Registra cualquier cambio relevante en la auditoría de MongoDB.
     * Tipo puede ser: "NUEVO", "ACTUALIZADO", "FINALIZADO"
     */
    public boolean registrar(Mantenimiento m, String tipo) {
        try {
            Document doc = new Document()
                    .append("tipo_evento",       tipo)
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
            if ("FINALIZADO".equalsIgnoreCase(tipo)) {
                doc.append("horas_resolucion", m.getTiempoSolucionHoras());
            }

            getColeccion().insertOne(doc);
            System.out.println(" Auditoría [" + tipo + "] registrada para id=" + m.getId());
            return true;

        } catch (Exception e) {
            System.out.println(" Error al registrar auditoría en MongoDB: " + e.getMessage());
            return false;
        }
    }

    /**
     * Sobrecarga para compatibilidad con el código anterior que no pasaba tipo.
     * Por defecto trata el registro como FINALIZADO.
     */
    public boolean registrar(Mantenimiento m) {
        return registrar(m, "FINALIZADO");
    }

    /**
     * Devuelve todos los registros de auditoría.
     */
    public List<Mantenimiento> obtenerTodos() {
        List<Mantenimiento> lista = new ArrayList<>();
        try {
            for (Document doc : getColeccion().find()) {
                lista.add(documentoAMantenimiento(doc));
            }
            System.out.println(" Auditoría: " + lista.size() + " registros cargados.");
        } catch (Exception e) {
            System.out.println(" Error al leer auditoría: " + e.getMessage());
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
}