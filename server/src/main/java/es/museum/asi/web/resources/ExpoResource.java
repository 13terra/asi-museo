package es.museum.asi.web.resources;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import es.museum.asi.model.enums.EstadoExpo;
import es.museum.asi.model.enums.TipoPermiso;
import es.museum.asi.model.exception.NotFoundException;
import es.museum.asi.model.exception.OperationNotAllowed;
import es.museum.asi.model.service.ExpoService;
import es.museum.asi.model.service.dto.ExposicionDTO;
import es.museum.asi.model.service.dto.ExposicionDetalleDTO;
import es.museum.asi.model.service.dto.GestorPermisoDTO;
import es.museum.asi.web.exceptions.InvalidPermissionException;
import es.museum.asi.web.util.ErrorDTO;

/**
 * Resource de exposiciones (HU9-HU19, HU59).
 */
@RestController
@RequestMapping("/api/exposiciones")
@CrossOrigin(origins = "*")
public class ExpoResource {

  private static final Logger logger = LoggerFactory.getLogger(ExpoResource.class);

  @Autowired
  private ExpoService expoService;

  // ==================== LISTADOS ====================

  @GetMapping("/admin")
  public ResponseEntity<Collection<ExposicionDTO>> findAllForAdmin(
      @RequestParam(defaultValue = "false") boolean incluirArchivadas) {
    return ResponseEntity.ok(expoService.findAllForAdmin(incluirArchivadas));
  }

  @GetMapping("/gestor")
  public ResponseEntity<Collection<ExposicionDTO>> findAllForGestor(
      @RequestParam(defaultValue = "false") boolean incluirArchivadas) {
    return ResponseEntity.ok(expoService.findAllForGestor(incluirArchivadas));
  }

  @GetMapping("/publico")
  public ResponseEntity<Collection<ExposicionDTO>> findAllPublic() {
    return ResponseEntity.ok(expoService.findAllPublic());
  }

  @GetMapping("/publico/buscar")
  public ResponseEntity<Collection<ExposicionDTO>> searchPublic(@RequestParam String termino) {
    return ResponseEntity.ok(expoService.searchPublic(termino));
  }

  // ==================== CRUD ====================

  @PostMapping
  public ResponseEntity<ExposicionDTO> create(
      @RequestParam String titulo,
      @RequestParam(required = false) String descripcion) {
    return ResponseEntity.status(HttpStatus.CREATED).body(expoService.create(titulo, descripcion));
  }

  @GetMapping("/{idExposicion}/admin")
  public ResponseEntity<?> findDetalleForAdmin(@PathVariable Long idExposicion) {
    try {
      return ResponseEntity.ok(expoService.findDetalleForAdmin(idExposicion));
    } catch (NotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDTO("Exposición no encontrada"));
    } catch (InvalidPermissionException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorDTO(e.getMessage()));
    }
  }

  @GetMapping("/{idExposicion}/publico")
  public ResponseEntity<?> findDetallePublic(@PathVariable Long idExposicion) {
    try {
      ExposicionDetalleDTO dto = expoService.findDetallePublic(idExposicion);
      return ResponseEntity.ok(dto);
    } catch (NotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDTO("Exposición no encontrada"));
    } catch (OperationNotAllowed e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDTO(e.getMessage()));
    }
  }

  @PutMapping("/{idExposicion}")
  public ResponseEntity<?> update(
      @PathVariable Long idExposicion,
      @RequestParam(required = false) String titulo,
      @RequestParam(required = false) String descripcion,
      @RequestParam(required = false) EstadoExpo estado) {
    try {
      return ResponseEntity.ok(expoService.update(idExposicion, titulo, descripcion, estado));
    } catch (NotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDTO("Exposición no encontrada"));
    } catch (InvalidPermissionException | OperationNotAllowed e) {
      return ResponseEntity.badRequest().body(new ErrorDTO(e.getMessage()));
    }
  }

  @PutMapping("/{idExposicion}/archivar")
  public ResponseEntity<?> archivar(@PathVariable Long idExposicion) {
    try {
      return ResponseEntity.ok(expoService.archivar(idExposicion));
    } catch (Exception e) {
      return handleException(e, "archivar");
    }
  }

  @PutMapping("/{idExposicion}/desarchivar")
  public ResponseEntity<?> desarchivar(@PathVariable Long idExposicion) {
    try {
      return ResponseEntity.ok(expoService.desarchivar(idExposicion));
    } catch (Exception e) {
      return handleException(e, "desarchivar");
    }
  }

  @DeleteMapping("/{idExposicion}")
  public ResponseEntity<?> delete(@PathVariable Long idExposicion) {
    try {
      expoService.delete(idExposicion);
      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      return handleException(e, "eliminar");
    }
  }

  // ==================== PERMISOS (HU17-19) ====================

  @GetMapping("/{idExposicion}/permisos")
  public ResponseEntity<?> listarPermisos(@PathVariable Long idExposicion) {
    try {
      Collection<GestorPermisoDTO> permisos = expoService.listarPermisos(idExposicion);
      return ResponseEntity.ok(permisos);
    } catch (Exception e) {
      return handleException(e, "listar permisos");
    }
  }

  @PostMapping("/{idExposicion}/permisos")
  public ResponseEntity<?> asignarPermiso(
      @PathVariable Long idExposicion,
      @RequestParam Long idGestor,
      @RequestParam TipoPermiso permiso) {
    try {
      return ResponseEntity.status(HttpStatus.CREATED)
        .body(expoService.asignarPermisoDTO(idExposicion, idGestor, permiso));
    } catch (Exception e) {
      return handleException(e, "asignar permiso");
    }
  }

  @DeleteMapping("/{idExposicion}/permisos/{idGestor}")
  public ResponseEntity<?> revocarPermiso(
      @PathVariable Long idExposicion,
      @PathVariable Long idGestor) {
    try {
      expoService.revocarPermiso(idExposicion, idGestor);
      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      return handleException(e, "revocar permiso");
    }
  }

  private ResponseEntity<ErrorDTO> handleException(Exception e, String operacion) {
    logger.warn("Error al {}: {}", operacion, e.getMessage());
    if (e instanceof NotFoundException) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDTO("Recurso no encontrado"));
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
