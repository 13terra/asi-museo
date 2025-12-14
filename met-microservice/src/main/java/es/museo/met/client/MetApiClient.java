package es.museo.met.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Cliente HTTP para interactuar con The Met Collection API
 */
@Component
public class MetApiClient {

    private static final Logger logger = LoggerFactory.getLogger(MetApiClient.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${met.api.base-url: https://collectionapi.metmuseum.org/public/collection/v1}")
    private String baseUrl;


    /**
     * Buscar obras por término
     * Endpoint: /search? q={query}
     *
     * @param query Término de búsqueda
     * @return Lista de IDs de objetos que coinciden
     */
    public List<Long> searchObjects(String query) {
        try {
            String url = String.format("%s/search?q=%s", baseUrl, query);
            logger.info("Buscando en MET:  {}", url);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("objectIDs")) {
                @SuppressWarnings("unchecked")
                List<Integer> objectIds = (List<Integer>) response.get("objectIDs");

                if (objectIds != null) {
                    // Convertir Integer a Long y limitar resultados
                    return objectIds.stream()
                            .limit(50) // Limitar a 50 resultados para no saturar
                            .map(Integer::longValue)
                            .toList();
                }
            }

            logger.warn("Sin resultados para query: {}", query);
            return Collections.emptyList();

        } catch (RestClientException e) {
            logger.error("Error al buscar en MET API: {}", e.getMessage());
            return Collections.emptyList();
        }
    }


    /**
     * Obtener detalle de un objeto por su ID
     * Endpoint: /objects/{objectID}
     *
     * @param objectId ID del objeto en The MET
     * @return Mapa con los metadatos del objeto
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getObjectById(Long objectId) {
        try {
            String url = String.format("%s/objects/%d", baseUrl, objectId);
            logger.info("Obteniendo objeto MET: {}", url);

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null) {
                return response;
            }

            logger.warn("Objeto no encontrado: {}", objectId);
            return Collections.emptyMap();

        } catch (RestClientException e) {
            logger.error("Error al obtener objeto {} de MET API: {}", objectId, e. getMessage());
            return Collections.emptyMap();
        }
    }

}
