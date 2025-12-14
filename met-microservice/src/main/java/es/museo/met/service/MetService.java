package es.museo.met.service;

import es.museo.met.client.MetApiClient;
import es.museo.met.dto.ObraDetalleDTO;
import es.museo.met.dto.ObraSimplificadaDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Servicio para procesar datos de The MET API
 */
@Service
public class MetService {
    private static final Logger logger = LoggerFactory.getLogger(MetService.class);

    @Autowired
    private MetApiClient metApiClient;

    /**
     * HU48 - Buscar obras por término
     * Devuelve lista simplificada con:  idExterno, titulo, autor, imagen
     */
    public List<ObraSimplificadaDTO> buscarObras(String query) {

        if (query == null || query.trim().isEmpty()) {
            logger.warn("Query vacía, devolviendo lista vacía");
            return List.of();
        }

        //Primero obtenemos los IDs de objetos que coinciden
        List<Long> objectIds = metApiClient.searchObjects(query);

        //Comprobamos si tenemos alguno
        if (objectIds.isEmpty()) {
            logger.info("Sin resultados para query: {}", query);
            return List.of();
        }

        logger.info("Encontrados {} objetos para query: {}", objectIds.size(), query);

        //Después obtenemos los detalles de cada objeto (limitamos a 20 para no saturar)
        List<ObraSimplificadaDTO> obras = new ArrayList<>();
        int limit = Math.min(objectIds.size(), 20);

        for (int i = 0; i < limit; i++) {
            Long objectId = objectIds.get(i);
            Map<String, Object> objectData = metApiClient.getObjectById(objectId);

            if (! objectData.isEmpty()) {
                ObraSimplificadaDTO obra = mapToObraSimplificada(objectData);
                if (obra != null) {
                    obras.add(obra);
                }
            }
        }

        logger.info("Devolviendo {} obras simplificadas", obras.size());
        return obras;
    }

    /**
     * HU49 - Obtener detalle completo de una obra
     * Devuelve:  idExterno, titulo, autor, añoCreacion, dimensiones, imagen, tecnica
     */
    public ObraDetalleDTO obtenerDetalle(Long metId) {
        Map<String, Object> objectData = metApiClient.getObjectById(metId);

        if (objectData. isEmpty()) {
            logger.warn("Objeto MET no encontrado: {}", metId);
            return null;
        }

        return mapToObraDetalle(objectData);
    }



    // ============== MAPPERS ==============

    /**
     * Mapear respuesta de MET API a ObraSimplificadaDTO
     */
    private ObraSimplificadaDTO mapToObraSimplificada(Map<String, Object> data) {
        try {
            ObraSimplificadaDTO obra = new ObraSimplificadaDTO();

            obra.setIdExterno(getLong(data, "objectID"));
            obra.setTitulo(getString(data, "title"));
            obra.setAutor(getString(data, "artistDisplayName"));
            obra.setImagen(getString(data, "primaryImageSmall")); // Imagen pequeña para listados

            // Solo incluir si tiene título
            if (obra.getTitulo() == null || obra.getTitulo().trim().isEmpty()) {
                return null;
            }

            return obra;
        } catch (Exception e) {
            logger.error("Error al mapear obra simplificada: {}", e.getMessage());
            return null;
        }
    }


    /**
     * Mapear respuesta de MET API a ObraDetalleDTO
     */
    private ObraDetalleDTO mapToObraDetalle(Map<String, Object> data) {
        ObraDetalleDTO obra = new ObraDetalleDTO();

        obra.setIdExterno(getLong(data, "objectID"));
        obra.setTitulo(getString(data, "title"));
        obra.setAutor(getString(data, "artistDisplayName"));

        // Año de creación
        String objectDate = getString(data, "objectDate");
        obra.setAnoCreacion(extractYear(objectDate));

        // Dimensiones
        obra.setDimensiones(getString(data, "dimensions"));

        // Imagen (usar la de alta resolución)
        obra.setImagen(getString(data, "primaryImage"));

        // Técnica (en MET se llama "medium")
        obra.setTecnica(getString(data, "medium"));

        return obra;
    }


    // ============== MÉTODOS AUXILIARES ==============

    /**
     * Extraer año de un string como "1889" o "ca. 1889" o "1889-1890"
     */
    private Integer extractYear(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }

        try {
            // Buscar primer número de 4 dígitos
            String cleaned = dateString.replaceAll("[^0-9]", " ");
            String[] parts = cleaned.trim().split("\\s+");

            for (String part : parts) {
                if (part.length() == 4) {
                    return Integer.parseInt(part);
                }
            }
        } catch (Exception e) {
            logger.warn("No se pudo extraer año de:  {}", dateString);
        }

        return null;
    }


    private String getString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }

    private Long getLong(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return null;
    }

}
