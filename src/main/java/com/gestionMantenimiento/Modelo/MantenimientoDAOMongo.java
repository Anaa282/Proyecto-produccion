package com.gestionMantenimiento.Modelo;

import com.gestionMantenimiento.Util.ConexionMongo;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MantenimientoDAOMongo {

    private MongoCollection<Document> getColeccion() {
        MongoDatabase db = ConexionMongo.getDB();
        return db.getCollection("mantenimiento");
    }

    // Convierte Document de Mongo → objeto Mantenimiento
    private Mantenimiento documentoAMantenimiento(Document doc) {
        return new Mantenimiento(
                doc.getObjectId("_id").hashCode(),   // id numérico aproximado
                doc.getString("fecha"),
                doc.getString("residente"),
                doc.getString("categoria"),
                doc.getString("prioridad"),
                doc.getString("estado"),
                doc.getString("tecnico"),
                doc.getString("descripcion"),
                doc.getString("ubicacion"),
                doc.get("fecha_hora_inicio") != null
                        ? LocalDateTime.parse(doc.getString("fecha_hora_inicio")) : null,
                doc.get("fecha_hora_fin") != null
                        ? LocalDateTime.parse(doc.getString("fecha_hora_fin")) : null,
                doc.getString("comentarios")
        );
    }

    // Convierte objeto Mantenimiento → Document de Mongo
    private Document mantenimientoADocumento(Mantenimiento m) {
        Document doc = new Document()
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
            System.out.println("✅ Cargados " + lista.size() + " registros de MongoDB");
        } catch (Exception e) {
            System.out.println("❌ Error al obtener de MongoDB: " + e.getMessage());
        }
        return lista;
    }

    public boolean insertar(Mantenimiento m) {
        try {
            getColeccion().insertOne(mantenimientoADocumento(m));
            System.out.println("✅ Insertado en MongoDB");
            return true;
        } catch (Exception e) {
            System.out.println("❌ Error al insertar: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        // En Mongo se elimina por _id (ObjectId); aquí buscamos por campo "id" si lo guardas,
        // o adapta según tu estrategia de IDs
        try {
            getColeccion().deleteOne(Filters.eq("id", id));
            System.out.println("✅ Eliminado de MongoDB");
            return true;
        } catch (Exception e) {
            System.out.println("❌ Error al eliminar: " + e.getMessage());
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
            System.out.println("✅ Actualizado en MongoDB");
            return true;
        } catch (Exception e) {
            System.out.println("❌ Error al actualizar: " + e.getMessage());
            return false;
        }
    }
}