package es.museum.asi.model.client;

import es.museum.asi.model.service.dto.ObraMetDetalleDTO;
import es.museum.asi.model.service.dto.ObraMetSimplificadaDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Cliente HTTP para consumir el microservicio
 *
 * Este cliente actúa como proxy entre el backend principal y el microservicio,
 * siguiendo el patrón de arquitectura definido en el diagrama de componentes.
 */

@Component
public class TheMETClient {

  private static final Logger logger = LoggerFactory.getLogger(TheMETClient.class);

  private final RestTemplate restTemplate;

  @Value("${met.service.url:http://localhost:8081}")
  private String metServiceUrl;

  /**
   * Constructor que inicializa RestTemplate
   * En producción podría inyectarse desde configuración
   */

  public TheMETClient() {
    this.restTemplate = new RestTemplate();
  }

  /**
   * HU48 - Buscar obras en The MET por término de búsqueda
   *
   * Consume: GET {met.service.url/api/met/obras? query={termino}}
   *
   */
  public List<ObraMetSimplificadaDTO> buscarObras(String query) {
    if (query == null || query.trim().isEmpty()) {
      logger.warn("Query vacía en buscarObras, devolviendo lista vacía.");
      return List.of();
    }

    try {
      String url = String.format("%s/api/met/obras?query=%s", metServiceUrl, query);
      logger.info("Llamando a microservicio MET para buscar:  '{}'", query);

      ResponseEntity<List<ObraMetSimplificadaDTO>> response = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<ObraMetSimplificadaDTO>>() {});

      List<ObraMetSimplificadaDTO> obras = response.getBody();
      int numObras = obras != null ? obras.size() : 0;

      logger.info("Microservicio MET devolvió {} obras para query '{}'", numObras, query);

      return obras != null ?  obras : List.of();

    } catch (HttpClientErrorException e) {
      logger.error("Error HTTP {} al buscar en microservicio MET:  {}",
        e.getStatusCode(), e.getMessage());
      return List.of();

    } catch (RestClientException e) {
      logger.error("Error de comunicación con microservicio MET:  {}", e.getMessage());
      logger.warn("Verifica que el microservicio esté corriendo en {}", metServiceUrl);
      return List.of();
    }
  }


  /**
   * HU49 - Obtener detalle completo de una obra de The MET
   * Consume: GET {met.service.url}/api/met/obras/{metId}
   *
   * Este detalle se usa para precargar el formulario de creación de obra
   * (el usuario puede editar antes de guardar con HU43). */
  public ObraMetDetalleDTO obtenerDetalle(Long metId) {
    if (metId == null) {
      logger.warn("metId null en obtenerDetalle");
      return null;
    }

    try {
      String url = String.format("%s/api/met/obras/%d", metServiceUrl, metId);
      logger.info("Obteniendo detalle de obra MET ID: {}", metId);

      ResponseEntity<ObraMetDetalleDTO> response = restTemplate.getForEntity(url, ObraMetDetalleDTO.class
      );
      ObraMetDetalleDTO detalle = response.getBody();

      if (detalle != null) {
        logger.info("Detalle obtenido:  '{}' por {}", detalle.getTitulo(), detalle.getAutor());
      } else {
        logger.warn("Microservicio devolvió body null para MET ID {}", metId);
      }

      return detalle;

    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        logger.warn("Obra MET ID {} no encontrada en The MET", metId);
      } else {
        logger.error("Error HTTP {} al obtener detalle MET ID {}: {}", e.getStatusCode(), metId, e.getMessage());
      }
      return null;

    } catch (RestClientException e) {
      logger.error("Error de comunicación con microservicio al obtener MET ID {}: {}", metId, e.getMessage());
    }

    return null;
  }

}
