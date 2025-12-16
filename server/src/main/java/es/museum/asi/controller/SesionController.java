package es.museum.asi.controller;

import es.museum.asi.model.enums.EstadoSesion;
import es.museum.asi.model.exception.NotFoundException;
import es.museum.asi.model.exception.OperationNotAllowed;
import es.museum.asi.model.service.SesionService;
import es.museum.asi.model.service.dto.SalaOrdenDTO;
import es.museum.asi.model.service.dto.SesionDTO;
import es.museum.asi.model.service.dto.SesionDetalleDTO;
import es.museum.asi.model.service.dto.SesionUpdateDTO;
import es.museum.asi.web.exceptions.InvalidPermissionException;
import es.museum.asi.web.exceptions.RequestBodyNotValidException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

/**
 * Controlador REST para gestión de sesiones
 * Implementa HU31-HU36, HU41-HU42
 */
@RestController
@RequestMapping("/api")
public class SesionController {

  @Autowired
  private SesionService sesionService;

  // ==================== CRUD SESIONES ====================

  /**
   * HU31 - Crear sesión
   * ADMIN/GESTOR con permisos
   * No se puede crear sin asignarle al menos una sala
   */
  @PostMapping("/ediciones/{idEdicion}/sesiones")
  public SesionDTO create(
      @PathVariable Long idEdicion,
      @RequestParam LocalDate fecha,
      @RequestParam LocalTime horaInicio,
      @RequestParam LocalTime horaFin,
      @RequestParam int aforo,
      @RequestParam List<Long> idSalas)
      throws NotFoundException, InvalidPermissionException, OperationNotAllowed {
    return sesionService.create(idEdicion, fecha, horaInicio, horaFin, aforo, idSalas);
  }

  /**
   * HU32 - Listar sesiones de una edición
   * ADMIN/GESTOR con permisos
   * Con filtros por fecha, sala y estado
   */
  @GetMapping("/ediciones/{idEdicion}/sesiones")
  public Collection<SesionDTO> findByEdicion(
      @PathVariable Long idEdicion,
      @RequestParam(required = false) LocalDate fecha,
      @RequestParam(required = false) Long idSala,
      @RequestParam(required = false) EstadoSesion estado)
      throws NotFoundException, InvalidPermissionException, OperationNotAllowed {
    return sesionService.findByEdicion(idEdicion, fecha, idSala, estado);
  }

  /**
   * HU33 - Ver detalle sesión (ADMIN/GESTOR)
   * Con información completa de gestión
   */
  @GetMapping("/sesiones/{idSesion}/admin")
  public SesionDetalleDTO findDetalle(@PathVariable Long idSesion)
      throws NotFoundException, InvalidPermissionException, OperationNotAllowed {
    return sesionService.findDetalle(idSesion);
  }

  /**
   * Ver detalle sesión (Vista pública)
   * Solo si edición publicada y vigente
   */
  @GetMapping("/sesiones/{idSesion}/publico")
  public SesionDetalleDTO findDetallePublic(@PathVariable Long idSesion)
      throws NotFoundException, InvalidPermissionException {
    return sesionService.findDetallePublic(idSesion);
  }

  /**
   * HU34 - Editar sesión
   * ADMIN/GESTOR con permisos
   */
  @PutMapping("/sesiones/{idSesion}")
  public SesionDTO update(
      @PathVariable Long idSesion,
      @Valid @RequestBody SesionUpdateDTO dto,
      Errors errors)
      throws NotFoundException, InvalidPermissionException, OperationNotAllowed, RequestBodyNotValidException {
    if (errors.hasErrors()) {
      throw new RequestBodyNotValidException(errors);
    }
    return sesionService.update(idSesion, dto);
  }

  /**
   * HU35 - Cancelar sesión
   * ADMIN/GESTOR con permisos
   * Las reservas pasan a estado cancelado automáticamente
   */
  @PutMapping("/sesiones/{idSesion}/cancelar")
  public SesionDTO cancelar(@PathVariable Long idSesion)
      throws NotFoundException, InvalidPermissionException, OperationNotAllowed {
    return sesionService.cancelar(idSesion);
  }

  /**
   * HU36 - Eliminar sesión
   * ADMIN/GESTOR con permisos
   * Solo se pueden eliminar sesiones SIN reservas
   */
  @DeleteMapping("/sesiones/{idSesion}")
  public void delete(@PathVariable Long idSesion)
      throws NotFoundException, InvalidPermissionException, OperationNotAllowed {
    sesionService.delete(idSesion);
  }

  // ==================== GESTIÓN DE SALAS (HU41-HU42) ====================

  /**
   * Listar salas asignadas a una sesión (ordenadas)
   */
  @GetMapping("/sesiones/{idSesion}/salas")
  public List<SalaOrdenDTO> listarSalasAsignadas(@PathVariable Long idSesion)
      throws NotFoundException, InvalidPermissionException {
    return sesionService.listarSalasAsignadas(idSesion);
  }

  /**
   * HU41 - Asignar sala(s) a sesión
   * ADMIN/GESTOR con permisos
   */
  @PostMapping("/sesiones/{idSesion}/salas")
  public void asignarSala(
      @PathVariable Long idSesion,
      @RequestParam List<Long> idSalas)
      throws NotFoundException, InvalidPermissionException, OperationNotAllowed {
    sesionService.asignarSala(idSesion, idSalas);
  }

  /**
   * HU42 - Desasignar sala de sesión
   * ADMIN/GESTOR con permisos
   * Solo si no hay piezas expuestas en esa sala
   */
  @DeleteMapping("/sesiones/{idSesion}/salas/{idSala}")
  public void desasignarSala(
      @PathVariable Long idSesion,
      @PathVariable Long idSala)
      throws NotFoundException, InvalidPermissionException, OperationNotAllowed {
    sesionService.desasignarSala(idSesion, idSala);
  }
}
