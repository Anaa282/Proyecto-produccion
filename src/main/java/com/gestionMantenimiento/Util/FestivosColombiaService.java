package com.gestionMantenimiento.Util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servicio que obtiene los festivos de Colombia desde la API de Nager
 * y permite calcular días hábiles entre dos fechas.
 *
 * API utilizada: https://date.nager.at/api/v3/PublicHolidays/{year}/CO
 *
 * Los festivos se cachean por año para evitar llamadas repetidas.
 */
public class FestivosColombiaService {

    private static final Logger LOG = Logger.getLogger(FestivosColombiaService.class.getName());
    private static final String API_URL = "https://date.nager.at/api/v3/PublicHolidays/%d/CO";

    // Cache: año -> conjunto de fechas festivas
    private static final ConcurrentHashMap<Integer, Set<LocalDate>> cache = new ConcurrentHashMap<>();

    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Instancia singleton
    private static FestivosColombiaService instancia;

    private FestivosColombiaService() {}

    public static synchronized FestivosColombiaService getInstance() {
        if (instancia == null) {
            instancia = new FestivosColombiaService();
        }
        return instancia;
    }

    /**
     * Obtiene los festivos de Colombia para el año indicado.
     * Si ya fueron consultados antes, devuelve el resultado en caché.
     *
     * @param year año a consultar
     * @return conjunto de fechas festivas (puede estar vacío si falla la API)
     */
    public Set<LocalDate> obtenerFestivos(int year) {
        return cache.computeIfAbsent(year, this::fetchFestivosDesdeAPI);
    }

    /**
     * Llama a la API de Nager y parsea los festivos para el año dado.
     */
    private Set<LocalDate> fetchFestivosDesdeAPI(int year) {
        String url = String.format(API_URL, year);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                LOG.warning("API de festivos retornó código: " + response.statusCode()
                        + " para el año " + year);
                return Collections.emptySet();
            }

            JsonNode nodos = objectMapper.readTree(response.body());
            Set<LocalDate> festivos = new HashSet<>();

            for (JsonNode nodo : nodos) {
                String fechaStr = nodo.get("date").asText();
                festivos.add(LocalDate.parse(fechaStr));
            }

            LOG.info("Festivos cargados para " + year + ": " + festivos.size() + " días");
            return Collections.unmodifiableSet(festivos);

        } catch (Exception e) {
            LOG.log(Level.WARNING, "No se pudo obtener festivos para " + year
                    + ". Se usará solo fines de semana.", e);
            return Collections.emptySet();
        }
    }

    /**
     * Verifica si una fecha es día hábil (no es fin de semana ni festivo en Colombia).
     *
     * @param fecha fecha a verificar
     * @return true si es día hábil
     */
    public boolean esDiaHabil(LocalDate fecha) {
        DayOfWeek dia = fecha.getDayOfWeek();
        if (dia == DayOfWeek.SATURDAY || dia == DayOfWeek.SUNDAY) {
            return false;
        }
        Set<LocalDate> festivosDelAnio = obtenerFestivos(fecha.getYear());
        return !festivosDelAnio.contains(fecha);
    }

    /**
     * Calcula la cantidad de días hábiles entre dos fechas (inicio y fin inclusive),
     * excluyendo fines de semana y festivos colombianos.
     *
     * Si inicio o fin son null, retorna 0.
     * Si fin es anterior a inicio, retorna 0.
     *
     * @param inicio fecha de inicio
     * @param fin    fecha de fin
     * @return número de días hábiles
     */
    public long calcularDiasHabiles(LocalDate inicio, LocalDate fin) {
        if (inicio == null || fin == null) return 0;
        if (fin.isBefore(inicio)) return 0;

        long diasHabiles = 0;
        LocalDate cursor = inicio;

        while (!cursor.isAfter(fin)) {
            if (esDiaHabil(cursor)) {
                diasHabiles++;
            }
            cursor = cursor.plusDays(1);
        }

        return diasHabiles;
    }

    /**
     * Precarga los festivos de los años dados en background para
     * evitar latencia en el primer uso.
     *
     * @param years años a precargar
     */
    public void precargarAnios(int... years) {
        Thread hilo = new Thread(() -> {
            for (int year : years) {
                obtenerFestivos(year);
            }
        }, "festivos-precarga");
        hilo.setDaemon(true);
        hilo.start();
    }

    /**
     * Limpia el caché (útil en tests o si se necesita forzar recarga).
     */
    public void limpiarCache() {
        cache.clear();
    }
}