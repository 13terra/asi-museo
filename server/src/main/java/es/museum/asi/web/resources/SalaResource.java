package es.museum.asi.web.resources;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.museum.asi.model.exception.NotFoundException;
import es.museum.asi.model.exception.OperationNotAllowed;
import es.museum.asi.model.service.SalaService;
import es.museum.asi.model.service.dto.SalaDTO;
import es.museum.asi.web.exceptions.RequestBodyNotValidException;
import es.museum.asi.web.util.ErrorDTO;

/**
 * Resource de salas (HU37-HU40).
 */
@RestController
@RequestMapping("/api/salas")
@CrossOrigin(origins = "*")
public class SalaResource {

  @Autowired
  private SalaService salaService;

  @GetMapping
  public ResponseEntity<Collection<SalaDTO>> findAll(@RequestParam(required = false) Integer planta) {
    if (planta != null) {
      return ResponseEntity.ok(salaService.findByPlanta(planta));
    }
    return ResponseEntity.ok(salaService.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> findOne(@PathVariable Long id) {
    try {
      return ResponseEntity.ok(salaService.findById(id));
    } catch (NotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDTO("Sala no encontrada"));
    }
  }

  @PostMapping
  public ResponseEntity<?> create(@RequestBody SalaDTO salaDTO, Errors errors)
      throws RequestBodyNotValidException {
    if (errors != null && errors.hasErrors()) {
      throw new RequestBodyNotValidException(errors);
    }
    try {
      return ResponseEntity.status(HttpStatus.CREATED)
        .body(salaService.create(salaDTO.getNombre(), salaDTO.getPlanta()));
    } catch (OperationNotAllowed e) {
      return ResponseEntity.badRequest().body(new ErrorDTO(e.getMessage()));
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> update(@PathVariable Long id, @RequestBody SalaDTO salaDTO, Errors errors)
      throws RequestBodyNotValidException {
    if (errors != null && errors.hasErrors()) {
      throw new RequestBodyNotValidException(errors);
    }
    try {
      return ResponseEntity.ok(salaService.update(id, salaDTO.getNombre(), salaDTO.getPlanta()));
    } catch (NotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDTO("Sala no encontrada"));
    } catch (OperationNotAllowed e) {
      return ResponseEntity.badRequest().body(new ErrorDTO(e.getMessage()));
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(@PathVariable Long id) {
    try {
      salaService.deleteById(id);
      return ResponseEntity.noContent().build();
    } catch (NotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDTO("Sala no encontrada"));
    } catch (OperationNotAllowed e) {
      return ResponseEntity.badRequest().body(new ErrorDTO(e.getMessage()));
    }
  }

  @GetMapping("/{id}/disponible")
  public ResponseEntity<?> isDisponible(@PathVariable Long id) {
    try {
      return ResponseEntity.ok(salaService.isDisponibleParaEliminar(id));
    } catch (NotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDTO("Sala no encontrada"));
    }
  }
}
