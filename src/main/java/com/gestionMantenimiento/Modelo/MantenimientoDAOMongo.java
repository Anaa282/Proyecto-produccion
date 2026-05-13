package com.gestionMantenimiento.Modelo;

import com.gestionMantenimiento.Util.ConexionMongo;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MantenimientoDAOMongo {

    private MongoCollection<Document> getColeccion() {
        MongoDatabase db = ConexionMongo.getDB();
        return db.getCollection("mantenimiento");
    }

    // Convierte Document de Mongo → objeto Mantenimiento
    private Mantenimiento documentoAMantenimiento(Document doc) {

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime inicio = null;
        LocalDateTime fin = null;

        try {
            inicio = parsearFechaHora(doc.get("fecha_hora_inicio"), formatter);
            fin    = parsearFechaHora(doc.get("fecha_hora_fin"),    formatter);
        } catch (Exception e) {
            System.out.println("Error parseando fecha_hora: " + e.getMessage());
        }

        // ID: usamos el campo "id" si es un número razonable (>0 y < 1 millón)
        int id = 0;
        if (doc.get("id") != null) {
            try {
                int rawId = doc.getInteger("id");
                id = (rawId > 0 && rawId < 1_000_000) ? rawId : Math.abs(rawId % 100000);
            } catch (Exception e) {
                id = doc.getObjectId("_id").hashCode() & 0x7FFFFFFF % 99999 + 1;
            }
        } else if (doc.getObjectId("_id") != null) {
            id = doc.getObjectId("_id").hashCode() & 0x7FFFFFFF;
        }

        return new Mantenimiento(
                id,
                obtenerString(doc, "fecha"),
                obtenerString(doc, "residente"),
                obtenerString(doc, "categoria"),
                obtenerString(doc, "prioridad"),
                obtenerString(doc, "estado"),
                obtenerString(doc, "tecnico"),
                obtenerString(doc, "descripcion"),
                obtenerString(doc, "ubicacion"),
                inicio,
                fin,
                obtenerString(doc, "comentarios")
        );
    }

    /**
     * Lee un campo del Document de forma segura:
     * si es String lo devuelve directo, si es Date lo convierte, si es null devuelve "".
     */
    private String obtenerString(Document doc, String campo) {
        Object valor = doc.get(campo);
        if (valor == null) return "";
        if (valor instanceof String) return (String) valor;
        if (valor instanceof Date) {
            // Convierte java.util.Date → LocalDate → String yyyy-MM-dd
            return ((Date) valor).toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .toString();
        }
        return valor.toString();
    }

    /**
     * Parsea fecha_hora_inicio / fin que pueden ser String o Date de Mongo.
     */
    private LocalDateTime parsearFechaHora(Object valor, DateTimeFormatter formatter) {
        if (valor == null) return null;
        if (valor instanceof String) {
            String s = (String) valor;
            if (s.isEmpty()) return null;
            // Intenta con el formatter recibido; si falla prueba ISO
            try {
                return LocalDateTime.parse(s, formatter);
            } catch (Exception e) {
                return LocalDateTime.parse(s); // ISO-8601
            }
        }
        if (valor instanceof Date) {
            return ((Date) valor).toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        }
        return null;
    }

    // Convierte objeto Mantenimiento → Document de Mongo
    private Document mantenimientoADocumento(Mantenimiento m) {
        Document doc = new Document()
                .append("id",          m.getId())
                .append("fecha",        m.getFecha())
                .append("residente",    m.getResidente())
                .append("categoria",    m.getCategoria())
                .append("prioridad",    m.getPrioridad())
                .append("estado",       m.getEstado())
                .append("tecnico",      m.getTecnico())
                .append("descripcion",  m.getDescripcion())
                .append("ubicacion",    m.getUbicacion())
                .append("comentarios",  m.getComentarios());

        if (m.getFechaHoraInicio() != null)
            doc.append("fecha_hora_inicio", m.getFechaHoraInicio().toString());
        if (m.getFechaHoraFin() != null)
            doc.append("fecha_hora_fin", m.getFechaHoraFin().toString());

        return doc;
    }

    public List<Mantenimiento> obtenerMantenimientos() {
        List<Mantenimiento> lista = new ArrayList<>();
        try {
            for (Document doc : getColeccion().find()) {
                lista.add(documentoAMantenimiento(doc));
            }
            System.out.println(" Cargados " + lista.size() + " registros de MongoDB");
        } catch (Exception e) {
            System.out.println(" Error al obtener de MongoDB: " + e.getMessage());
        }
        return lista;
    }

    public boolean insertar(Mantenimiento m) {
        try {
            // Genera ID secuencial: máximo id existente + 1
            int nuevoId = 1;
            Document ultimo = getColeccion()
                    .find()
                    .sort(new Document("id", -1))
                    .limit(1)
                    .first();
            if (ultimo != null && ultimo.get("id") != null) {
                nuevoId = ultimo.getInteger("id") + 1;
            }
            m.setId(nuevoId);
            getColeccion().insertOne(mantenimientoADocumento(m));
            System.out.println(" Insertado en MongoDB con id=" + nuevoId);
            return true;
        } catch (Exception e) {
            System.out.println(" Error al insertar: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        // En Mongo se elimina por _id (ObjectId); aquí buscamos por campo "id" si lo guardas,
        // o adapta según tu estrategia de IDs
        try {
            getColeccion().deleteOne(Filters.eq("id", id));
            System.out.println(" Eliminado de MongoDB");
            return true;
        } catch (Exception e) {
            System.out.println(" Error al eliminar: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizar(Mantenimiento m) {
        try {
            getColeccion().updateOne(
                    Filters.eq("id", m.getId()),
                    Updates.combine(
                            Updates.set("estado",    m.getEstado()),
                            Updates.set("tecnico",   m.getTecnico()),
                            Updates.set("prioridad", m.getPrioridad()),
                            Updates.set("comentarios", m.getComentarios())
                    )
            );
            System.out.println(" Actualizado en MongoDB");
            return true;
        } catch (Exception e) {
            System.out.println(" Error al actualizar: " + e.getMessage());
            return false;
        }
    }
}