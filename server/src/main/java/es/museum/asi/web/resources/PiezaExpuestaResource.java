package es.museum.asi.web.resources;

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

import es.museum.asi.model.exception.NotFoundException;
import es.museum.asi.model.exception.OperationNotAllowed;
import es.museum.asi.model.service.PiezaExpuestaService;
import es.museum.asi.model.service.dto.PiezaExpuestaDTO;
import es.museum.asi.web.exceptions.InvalidPermissionException;
import es.museum.asi.web.util.ErrorDTO;

/**
 * Resource de piezas expuestas (HU27-HU30).
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class PiezaExpuestaResource {

  @Autowired
  private PiezaExpuestaService piezaExpuestaService;

  @PostMapping("/ediciones/{idEdicion}/piezas-expuestas")
  public ResponseEntity<?> create(
      @PathVariable Long idEdicion,
      @RequestParam Long idObra,
      @RequestParam Long idSala,
      @RequestParam(required = false) Integer orden,
      @RequestParam(required = false) String textoCuratorial,
      @RequestParam(required = false, defaultValue = "false") boolean portada) {
    try {
      PiezaExpuestaDTO dto = piezaExpuestaService.create(idObra, idEdicion, idSala, orden, textoCuratorial, portada);
      return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    } catch (Exception e) {
      return handle(e);
    }
  }

  @GetMapping("/ediciones/{idEdicion}/piezas-expuestas")
  public ResponseEntity<?> listByEdicion(@PathVariable Long idEdicion) {
    try {
      Collection<PiezaExpuestaDTO> list = piezaExpuestaService.findByEdicion(idEdicion);
      return ResponseEntity.ok(list);
    } catch (Exception e) {
      return handle(e);
    }
  }

  @GetMapping("/salas/{idSala}/ediciones/{idEdicion}/piezas-expuestas")
  public ResponseEntity<?> listBySala(@PathVariable Long idSala, @PathVariable Long idEdicion) {
    try {
      Collection<PiezaExpuestaDTO> list = piezaExpuestaService.findBySala(idSala, idEdicion);
      return ResponseEntity.ok(list);
    } catch (Exception e) {
      return handle(e);
    }
  }

  @PutMapping("/piezas-expuestas/{idPiezaExpuesta}")
  public ResponseEntity<?> update(
      @PathVariable Long idPiezaExpuesta,
      @RequestParam(required = false) Long idSala,
      @RequestParam(required = false) Integer orden,
      @RequestParam(required = false) String textoCuratorial,
      @RequestParam(required = false) Boolean portada) {
    try {
      PiezaExpuestaDTO dto = piezaExpuestaService.update(idPiezaExpuesta, idSala, orden, textoCuratorial, portada);
      return ResponseEntity.ok(dto);
    } catch (Exception e) {
      return handle(e);
    }
  }

  @DeleteMapping("/piezas-expuestas/{idPiezaExpuesta}")
  public ResponseEntity<?> delete(@PathVariable Long idPiezaExpuesta) {
    try {
      piezaExpuestaService.delete(idPiezaExpuesta);
      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      return handle(e);
    }
  }

  @PutMapping("/piezas-expuestas/{idPiezaExpuesta}/portada")
  public ResponseEntity<?> setPortada(@PathVariable Long idPiezaExpuesta) {
    try {
      piezaExpuestaService.setPortada(idPiezaExpuesta);
      return ResponseEntity.ok().build();
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
