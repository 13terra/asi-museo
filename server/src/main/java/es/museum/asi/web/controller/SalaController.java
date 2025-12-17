package es.museum.asi.web.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import es.museum.asi.model.exception.NotFoundException;
import es.museum.asi.model.exception.OperationNotAllowed;
import es.museum.asi.model.service.SalaService;
import es.museum.asi.model.service.dto.SalaDTO;
import es.museum.asi.web.exceptions.RequestBodyNotValidException;
import jakarta.validation.Valid;

/**
 * Controlador REST para gestión de salas
 * Implementa HU37-HU40
 */
@RestController
@RequestMapping("/api/salas")
public class SalaController {

  @Autowired
  private SalaService salaService;

  /**
   * HU38 - GET /api/salas - Listar todas las salas (ADMIN)
   * Soporta filtro opcional por planta
   */
  @GetMapping
  public Collection<SalaDTO> findAll(@RequestParam(required = false) Integer planta) {
    if (planta != null) {
      return salaService.findByPlanta(planta);
    }
    return salaService.findAll();
  }

  /**
   * GET /api/salas/{id} - Obtener detalle de sala (ADMIN)
   */
  @GetMapping("/{id}")
  public SalaDTO findOne(@PathVariable Long id) throws NotFoundException {
    return salaService.findById(id);
  }

  /**
   * HU37 - POST /api/salas - Crear sala (ADMIN)
   */
  @PostMapping
  public SalaDTO create(@RequestBody @Valid SalaDTO salaDTO, Errors errors)
      throws RequestBodyNotValidException, OperationNotAllowed {
    if (errors.hasErrors()) {
      throw new RequestBodyNotValidException(errors);
    }

    return salaService.create(salaDTO.getNombre(), salaDTO.getPlanta());
  }

  /**
   * HU39 - PUT /api/salas/{id} - Actualizar sala (ADMIN)
   */
  @PutMapping("/{id}")
  public SalaDTO update(
      @PathVariable Long id,
      @RequestBody @Valid SalaDTO salaDTO,
      Errors errors)
      throws RequestBodyNotValidException, NotFoundException, OperationNotAllowed {

    if (errors.hasErrors()) {
      throw new RequestBodyNotValidException(errors);
    }

    return salaService.update(id, salaDTO.getNombre(), salaDTO.getPlanta());
  }

  /**
   * HU40 - DELETE /api/salas/{id} - Eliminar sala (ADMIN)
   */
  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id)
      throws NotFoundException, OperationNotAllowed {
    salaService.deleteById(id);
  }

  /**
   * GET /api/salas/{id}/disponible - Verificar si sala puede eliminarse (ADMIN)
   */
  @GetMapping("/{id}/disponible")
  public boolean isDisponibleParaEliminar(@PathVariable Long id)
      throws NotFoundException {
    return salaService.isDisponibleParaEliminar(id);
  }
}
