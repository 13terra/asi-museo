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
import es.museum.asi.model.exception.OperationNotAllowed;
import es.museum.asi.model.service.EntradaService;
import es.museum.asi.model.service.dto.EntradaDTO;
import es.museum.asi.model.service.dto.EntradaDetalleDTO;
import es.museum.asi.web.util.ErrorDTO;

/**
 * Resource de entradas (HU55-HU56).
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class EntradaResource {

  @Autowired
  private EntradaService entradaService;

  @GetMapping("/mis-entradas")
  public ResponseEntity<Collection<EntradaDTO>> findMisEntradas() {
    return ResponseEntity.ok(entradaService.findMisEntradas());
  }

  @GetMapping("/mis-entradas/{idEntrada}")
  public ResponseEntity<?> findDetalle(@PathVariable Long idEntrada) {
    try {
      EntradaDetalleDTO dto = entradaService.findMiEntradaDetalle(idEntrada);
      return ResponseEntity.ok(dto);
    } catch (NotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDTO(e.getMessage()));
    } catch (OperationNotAllowed e) {
      return ResponseEntity.badRequest().body(new ErrorDTO(e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorDTO("Error interno"));
    }
  }
}
