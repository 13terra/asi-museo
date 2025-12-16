package es.museum.asi.controller;

import es.museum.asi.model.enums.EstadoEdicion;
import es.museum.asi.model.exception.NotFoundException;
import es.museum.asi.model.exception.OperationNotAllowed;
import es.museum.asi.model.service.EdicionService;
import es.museum.asi.model.service.dto.EdicionDTO;
import es.museum.asi.model.service.dto.EdicionDetalleDTO;
import es.museum.asi.web.exceptions.InvalidPermissionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collection;

/**
 * Controlador REST para gestión de ediciones
 * Implementa HU20-HU26
 */
@RestController
@RequestMapping("/api")
public class EdicionController {

  @Autowired
  private EdicionService edicionService;

  // ==================== CRUD EDICIONES ====================

  /**
   * HU20 - Crear edición
   * Solo ADMIN o GESTOR con permisos
   * Nace en estado BORRADOR
   */
  @PostMapping("/exposiciones/{idExposicion}/ediciones")
  public EdicionDTO create(
      @PathVariable Long idExposicion,
      @RequestParam LocalDate fechaInicio,
      @RequestParam LocalDate fechaFin)
      throws NotFoundException, InvalidPermissionException, OperationNotAllowed {
    return edicionService.create(idExposicion, fechaInicio, fechaFin);
  }

  /**
   * Listado de ediciones de una exposición (para ADMIN/GESTOR)
   */
  @GetMapping("/exposiciones/{idExposicion}/ediciones")
  public Collection<EdicionDTO> findByExpo(@PathVariable Long idExposicion)
      throws NotFoundException, InvalidPermissionException {
    return edicionService.findByExpo(idExposicion);
  }

  /**
   * HU21 - Detalle edición (ADMIN/GESTOR)
   * Con info completa de gestión
   */
  @GetMapping("/ediciones/{idEdicion}/admin")
  public EdicionDetalleDTO findDetalleForAdmin(@PathVariable Long idEdicion)
      throws NotFoundException, InvalidPermissionException {
    return edicionService.findDetalleForAdmin(idEdicion);
  }

  /**
   * HU21 - Detalle edición (Vista pública)
   * Solo si publicada y vigente
   */
  @GetMapping("/ediciones/{idEdicion}/publico")
  public EdicionDetalleDTO findDetallePublic(@PathVariable Long idEdicion)
      throws NotFoundException, OperationNotAllowed {
    return edicionService.findDetallePublic(idEdicion);
  }

  /**
   * HU22 - Editar edición
   * EDITOR puede editar fechas
   * Solo CREADOR/ADMIN pueden cambiar el estado
   */
  @PutMapping("/ediciones/{idEdicion}")
  public EdicionDTO update(
      @PathVariable Long idEdicion,
      @RequestParam(required = false) LocalDate fechaInicio,
      @RequestParam(required = false) LocalDate fechaFin,
      @RequestParam(required = false) EstadoEdicion estado)
      throws NotFoundException, InvalidPermissionException, OperationNotAllowed {
    return edicionService.update(idEdicion, fechaInicio, fechaFin, estado);
  }

  /**
   * HU23 - Publicar edición
   * Solo CREADOR/ADMIN
   * Validaciones: 1 sala, 1 sesión DISPONIBLE, 1 pieza expuesta
   */
  @PutMapping("/ediciones/{idEdicion}/publicar")
  public EdicionDTO publicar(@PathVariable Long idEdicion)
      throws NotFoundException, InvalidPermissionException, OperationNotAllowed {
    return edicionService.publicar(idEdicion);
  }

  /**
   * HU25 - Cancelar edición
   * Solo CREADOR o ADMIN
   * Sesiones y reservas se cancelan automáticamente
   */
  @PutMapping("/ediciones/{idEdicion}/cancelar")
  public EdicionDTO cancelar(@PathVariable Long idEdicion)
      throws NotFoundException, InvalidPermissionException, OperationNotAllowed {
    return edicionService.cancelar(idEdicion);
  }

  /**
   * HU26 - Eliminar edición
   * Solo CREADOR o ADMIN
   * Solo se puede eliminar si está en BORRADOR
   */
  @DeleteMapping("/ediciones/{idEdicion}")
  public void delete(@PathVariable Long idEdicion)
      throws NotFoundException, InvalidPermissionException, OperationNotAllowed {
    edicionService.delete(idEdicion);
  }
}
