package es.museum.asi.web.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.museum.asi.model.client.TheMETClient;
import es.museum.asi.model.service.dto.ObraMetDetalleDTO;
import es.museum.asi.model.service.dto.ObraMetSimplificadaDTO;
import es.museum.asi.web.util.ErrorDTO;

/**
 * Resource proxy para The MET (HU48-HU49).
 */
@RestController
@RequestMapping("/api/met")
@CrossOrigin(origins = "*")
public class TheMETResource {

  @Autowired
  private TheMETClient theMETClient;

  @GetMapping("/obras")
  public ResponseEntity<List<ObraMetSimplificadaDTO>> search(@RequestParam String query) {
    List<ObraMetSimplificadaDTO> obras = theMETClient.buscarObras(query);
    return ResponseEntity.ok(obras);
  }

  @GetMapping("/obras/{metId}")
  public ResponseEntity<?> detail(@PathVariable Long metId) {
    ObraMetDetalleDTO detalle = theMETClient.obtenerDetalle(metId);
    if (detalle == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDTO("Obra no encontrada en The MET"));
    }
    return ResponseEntity.ok(detalle);
  }
}
