package es.museum.asi.web.resources;

import java.time.LocalDate;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.museum.asi.model.enums.EstadoEdicion;
import es.museum.asi.model.exception.NotFoundException;
import es.museum.asi.model.exception.OperationNotAllowed;
import es.museum.asi.model.service.EdicionService;
import es.museum.asi.model.service.dto.EdicionDTO;
import es.museum.asi.model.service.dto.EdicionDetalleDTO;
import es.museum.asi.web.exceptions.InvalidPermissionException;
import es.museum.asi.web.util.ErrorDTO;

/**
 * Resource de ediciones (HU20-HU26).
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class EdicionResource {

  @Autowired
  private EdicionService edicionService;

  @PostMapping("/exposiciones/{idExposicion}/ediciones")
  public ResponseEntity<?> create(@PathVariable Long idExposicion,
                                  @RequestParam LocalDate fechaInicio,
                                  @RequestParam LocalDate fechaFin) {
    try {
      return ResponseEntity.status(HttpStatus.CREATED)
        .body(edicionService.create(idExposicion, fechaInicio, fechaFin));
    } catch (Exception e) {
      return handle(e);
    }
  }

  @GetMapping("/exposiciones/{idExposicion}/ediciones")
  public ResponseEntity<?> findByExpo(@PathVariable Long idExposicion) {
    try {
      Collection<EdicionDTO> ediciones = edicionService.findByExpo(idExposicion);
      return ResponseEntity.ok(ediciones);
    } catch (Exception e) {
      return handle(e);
    }
  }

  @GetMapping("/ediciones/{idEdicion}/admin")
  public ResponseEntity<?> findDetalleForAdmin(@PathVariable Long idEdicion) {
    try {
      EdicionDetalleDTO dto = edicionService.findDetalleForAdmin(idEdicion);
      return ResponseEntity.ok(dto);
    } catch (Exception e) {
      return handle(e);
    }
  }

  @GetMapping("/ediciones/{idEdicion}/publico")
  public ResponseEntity<?> findDetallePublic(@PathVariable Long idEdicion) {
    try {
      return ResponseEntity.ok(edicionService.findDetallePublic(idEdicion));
    } catch (Exception e) {
      return handle(e);
    }
  }

  @PutMapping("/ediciones/{idEdicion}")
  public ResponseEntity<?> update(
      @PathVariable Long idEdicion,
      @RequestParam(required = false) LocalDate fechaInicio,
      @RequestParam(required = false) LocalDate fechaFin,
      @RequestParam(required = false) EstadoEdicion estado) {
    try {
      return ResponseEntity.ok(edicionService.update(idEdicion, fechaInicio, fechaFin, estado));
    } catch (Exception e) {
      return handle(e);
    }
  }

  @PutMapping("/ediciones/{idEdicion}/publicar")
  public ResponseEntity<?> publicar(@PathVariable Long idEdicion) {
    try {
      return ResponseEntity.ok(edicionService.publicar(idEdicion));
    } catch (Exception e) {
      return handle(e);
    }
  }

  @PutMapping("/ediciones/{idEdicion}/cancelar")
  public ResponseEntity<?> cancelar(@PathVariable Long idEdicion) {
    try {
      return ResponseEntity.ok(edicionService.cancelar(idEdicion));
    } catch (Exception e) {
      return handle(e);
    }
  }

  @DeleteMapping("/ediciones/{idEdicion}")
  public ResponseEntity<?> delete(@PathVariable Long idEdicion) {
    try {
      edicionService.delete(idEdicion);
      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      return handle(e);
    }
  }

  private ResponseEntity<ErrorDTO> handle(Exception e) {
    if (e instanceof NotFoundException) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDTO(e.getMessage()));
    }
    if (e instanceof InvalidPermissionException) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorDTO(e.getMessage()));
    }
    if (e instanceof OperationNotAllowed) {
      return ResponseEntity.badRequest().body(new ErrorDTO(e.getMessage()));
    }
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorDTO("Error interno"));
  }
}
