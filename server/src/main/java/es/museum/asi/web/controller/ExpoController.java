package es.museum.asi.web.controller;

import es.museum.asi.model.enums.EstadoExpo;
import es.museum.asi.model.enums.TipoPermiso;
import es.museum.asi.model.exception.NotFoundException;
import es.museum.asi.model.exception.OperationNotAllowed;
import es.museum.asi.model.service.ExpoService;
import es.museum.asi.model.service.dto.ExposicionDTO;
import es.museum.asi.model.service.dto.ExposicionDetalleDTO;
import es.museum.asi.model.service.dto.GestorPermisoDTO;
import es.museum.asi.web.exceptions.InvalidPermissionException;
import es.museum.asi.web.exceptions.RequestBodyNotValidException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

/**
 * Controlador REST para gestión de exposiciones
 * Implementa HU9-HU19, HU59
 */
@RestController
@RequestMapping("/api/exposiciones")
public class ExpoController {

  @Autowired
  private ExpoService expoService;

  // ==================== LISTADO EXPOSICIONES ====================

  /**
   * HU9 - Listado exposiciones (Vista ADMIN)
   * Muestra TODAS las exposiciones
   */
  @GetMapping("/admin")
  public Collection<ExposicionDTO> findAllForAdmin(
      @RequestParam(required = false, defaultValue = "false") boolean incluirArchivadas) {
    return expoService.findAllForAdmin(incluirArchivadas);
  }

  /**
   * HU10 - Listado exposiciones (Vista GESTOR)
   * Solo exposiciones donde tenga permisos CREADOR/EDITOR
   */
  @GetMapping("/gestor")
  public Collection<ExposicionDTO> findAllForGestor(
      @RequestParam(required = false, defaultValue = "false") boolean incluirArchivadas) {
    return expoService.findAllForGestor(incluirArchivadas);
  }

  /**
   * HU11 - Listado exposiciones públicas
   * Solo ACTIVAS con ediciones publicadas vigentes
   */
  @GetMapping("/publico")
  public Collection<ExposicionDTO> findAllPublic() {
    return expoService.findAllPublic();
  }

  /**
   * HU59 - Buscar exposiciones en catálogo público
   */
  @GetMapping("/publico/buscar")
  public Collection<ExposicionDTO> searchPublic(@RequestParam String termino) {
    return expoService.searchPublic(termino);
  }

  // ==================== CRUD EXPOSICIONES ====================

  /**
   * HU12 - Crear exposición (ADMIN o GESTOR)
   * Si es GESTOR, se le asigna automáticamente permiso CREADOR
   */
  @PostMapping
  public ExposicionDTO create(
      @RequestParam String titulo,
      @RequestParam(required = false) String descripcion) {
    return expoService.create(titulo, descripcion);
  }

  /**
   * HU13 - Detalle exposición (ADMIN/GESTOR)
   * Con información completa de gestión
   */
  @GetMapping("/{idExposicion}/admin")
  public ExposicionDetalleDTO findDetalleForAdmin(@PathVariable Long idExposicion)
      throws NotFoundException, InvalidPermissionException {
    return expoService.findDetalleForAdmin(idExposicion);
  }

  /**
   * HU13 - Detalle exposición (Vista pública)
   * Solo información básica
   */
  @GetMapping("/{idExposicion}/publico")
  public ExposicionDetalleDTO findDetallePublic(@PathVariable Long idExposicion)
      throws NotFoundException, OperationNotAllowed {
    return expoService.findDetallePublic(idExposicion);
  }

  /**
   * HU14 - Editar exposición (ADMIN o GESTOR con permisos)
   * Solo CREADOR/ADMIN pueden modificar el estado
   */
  @PutMapping("/{idExposicion}")
  public ExposicionDTO update(
      @PathVariable Long idExposicion,
      @RequestParam(required = false) String titulo,
      @RequestParam(required = false) String descripcion,
      @RequestParam(required = false) EstadoExpo estado)
      throws NotFoundException, InvalidPermissionException, OperationNotAllowed {
    return expoService.update(idExposicion, titulo, descripcion, estado);
  }

  /**
   * HU15 - Archivar exposición (solo CREADOR o ADMIN)
   * No se puede archivar si tiene ediciones PUBLICADAS activas
   */
  @PutMapping("/{idExposicion}/archivar")
  public ExposicionDTO archivar(@PathVariable Long idExposicion)
      throws NotFoundException, InvalidPermissionException, OperationNotAllowed {
    return expoService.archivar(idExposicion);
  }

  /**
   * Desarchivar exposición (solo CREADOR o ADMIN)
   */
  @PutMapping("/{idExposicion}/desarchivar")
  public ExposicionDTO desarchivar(@PathVariable Long idExposicion)
      throws NotFoundException, InvalidPermissionException {
    return expoService.desarchivar(idExposicion);
  }

  /**
   * HU16 - Eliminar exposición (Solo CREADOR o ADMIN)
   * No se puede eliminar si tiene ediciones con reservas/entradas
   */
  @DeleteMapping("/{idExposicion}")
  public void delete(@PathVariable Long idExposicion)
      throws NotFoundException, InvalidPermissionException, OperationNotAllowed {
    expoService.delete(idExposicion);
  }

  // ==================== GESTIÓN DE PERMISOS (HU17-HU19) ====================

  /**
   * HU17 - Listar permisos de una exposición (Solo CREADOR o ADMIN)
   */
  @GetMapping("/{idExposicion}/permisos")
  public Collection<GestorPermisoDTO> listarPermisos(@PathVariable Long idExposicion)
      throws NotFoundException, InvalidPermissionException {
    return expoService.listarPermisos(idExposicion);
  }

  /**
   * HU18 - Asignar o modificar permiso (Solo CREADOR o ADMIN)
   */
  @PostMapping("/{idExposicion}/permisos")
  public GestorPermisoDTO asignarPermiso(
      @PathVariable Long idExposicion,
      @RequestParam Long idGestor,
      @RequestParam TipoPermiso permiso)
      throws NotFoundException, InvalidPermissionException, OperationNotAllowed {
    return expoService.asignarPermisoDTO(idExposicion, idGestor, permiso);
  }

  /**
   * HU19 - Revocar permiso (Solo CREADOR o ADMIN)
   */
  @DeleteMapping("/{idExposicion}/permisos/{idGestor}")
  public void revocarPermiso(
      @PathVariable Long idExposicion,
      @PathVariable Long idGestor)
      throws NotFoundException, InvalidPermissionException, OperationNotAllowed {
    expoService.revocarPermiso(idExposicion, idGestor);
  }
}
