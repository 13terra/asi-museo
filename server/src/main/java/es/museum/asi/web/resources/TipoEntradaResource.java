package es.museum.asi.web.resources;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.museum.asi.model.exception.NotFoundException;
import es.museum.asi.model.service.TipoEntradaService;
import es.museum.asi.model.service.dto.TipoEntradaDTO;
import es.museum.asi.web.util.ErrorDTO;

/**
 * Resource para tipos de entrada (HU50).
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TipoEntradaResource {

  @Autowired
  private TipoEntradaService tipoEntradaService;

  @GetMapping("/tipos-entrada")
  public ResponseEntity<Collection<TipoEntradaDTO>> findAll() {
    return ResponseEntity.ok(tipoEntradaService.findAll());
  }

  @GetMapping("/tipos-entrada/{id}")
  public ResponseEntity<?> findById(@PathVariable Long id) {
    try {
      return ResponseEntity.ok(tipoEntradaService.findById(id));
    } catch (NotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDTO(e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorDTO("Error interno"));
    }
  }
}
