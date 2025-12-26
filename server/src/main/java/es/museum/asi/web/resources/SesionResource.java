package es.museum.asi.web.resources;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

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
import es.museum.asi.web.util.ErrorDTO;

/**
 * Resource de sesiones (HU31-HU36, HU41-HU42).
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class SesionResource {

  @Autowired
  private SesionService sesionService;

  @PostMapping("/ediciones/{idEdicion}/sesiones")
  public ResponseEntity<?> create(
      @PathVariable Long idEdicion,
      @RequestParam(required = false) LocalDate fecha,
      @RequestParam(required = false) LocalTime horaInicio,
      @RequestParam(required = false) LocalTime horaFin,
      @RequestParam(required = false) Integer aforo,
      @RequestParam(required = false) List<Long> idSalas,
      @RequestBody(required = false) SesionCreateRequest body) {
    try {
      LocalDate finalFecha = coalesce(fecha, body != null ? body.getFecha() : null);
      LocalTime finalHoraInicio = coalesce(horaInicio, body != null ? body.getHoraInicio() : null);
      LocalTime finalHoraFin = coalesce(horaFin, body != null ? body.getHoraFin() : null);
      Integer finalAforo = coalesce(aforo, body != null ? body.getAforo() : null);
      List<Long> finalSalas = firstNonEmpty(idSalas, body != null ? body.getIdSalas() : null);

      if (finalFecha == null || finalHoraInicio == null || finalHoraFin == null || finalAforo == null || finalSalas == null) {
        return ResponseEntity.badRequest().body(new ErrorDTO("Debe indicar fecha, horaInicio, horaFin, aforo e idSalas."));
      }

      SesionDTO dto = sesionService.create(idEdicion, finalFecha, finalHoraInicio, finalHoraFin, finalAforo, finalSalas);
      return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    } catch (Exception e) {
      return handle(e);
    }
  }

  @GetMapping("/ediciones/{idEdicion}/sesiones")
  public ResponseEntity<?> findByEdicion(
      @PathVariable Long idEdicion,
      @RequestParam(required = false) LocalDate fecha,
      @RequestParam(required = false) Long idSala,
      @RequestParam(required = false) EstadoSesion estado) {
    try {
      Collection<SesionDTO> sesiones = sesionService.findByEdicion(idEdicion, fecha, idSala, estado);
      return ResponseEntity.ok(sesiones);
    } catch (Exception e) {
      return handle(e);
    }
  }

  @GetMapping("/ediciones/{idEdicion}/sesiones/publico")
  public ResponseEntity<?> findPublicByEdicion(
      @PathVariable Long idEdicion,
      @RequestParam(required = false) LocalDate fecha,
      @RequestParam(required = false) Long idSala) {
    try {
      // Público solo ve sesiones DISPONIBLES (o COMPLETA, pero no CANCELADA/BORRADOR si hubiera)
      // Asumimos que público quiere ver sesiones para reservar.
      Collection<SesionDTO> sesiones = sesionService.findPublicByEdicion(idEdicion, fecha, idSala);
      return ResponseEntity.ok(sesiones);
    } catch (Exception e) {
      return handle(e);
    }
  }

  @GetMapping("/sesiones/{idSesion}/admin")
  public ResponseEntity<?> findDetalle(@PathVariable Long idSesion) {
    try {
      SesionDetalleDTO dto = sesionService.findDetalle(idSesion);
      return ResponseEntity.ok(dto);
    } catch (Exception e) {
      return handle(e);
    }
  }

  @GetMapping("/sesiones/{idSesion}/publico")
  public ResponseEntity<?> findDetallePublic(@PathVariable Long idSesion) {
    try {
      return ResponseEntity.ok(sesionService.findDetallePublic(idSesion));
    } catch (Exception e) {
      return handle(e);
    }
  }

  @PutMapping("/sesiones/{idSesion}")
  public ResponseEntity<?> update(@PathVariable Long idSesion,
                                  @RequestBody SesionUpdateDTO dto,
                                  Errors errors) throws RequestBodyNotValidException {
    if (errors != null && errors.hasErrors()) {
      throw new RequestBodyNotValidException(errors);
    }
    try {
      return ResponseEntity.ok(sesionService.update(idSesion, dto));
    } catch (Exception e) {
      return handle(e);
    }
  }

  @PutMapping("/sesiones/{idSesion}/cancelar")
  public ResponseEntity<?> cancelar(@PathVariable Long idSesion) {
    try {
      return ResponseEntity.ok(sesionService.cancelar(idSesion));
    } catch (Exception e) {
      return handle(e);
    }
  }

  @DeleteMapping("/sesiones/{idSesion}")
  public ResponseEntity<?> delete(@PathVariable Long idSesion) {
    try {
      sesionService.delete(idSesion);
      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      return handle(e);
    }
  }

  @GetMapping("/sesiones/{idSesion}/salas")
  public ResponseEntity<?> listarSalas(@PathVariable Long idSesion) {
    try {
      List<SalaOrdenDTO> salas = sesionService.listarSalasAsignadas(idSesion);
      return ResponseEntity.ok(salas);
    } catch (Exception e) {
      return handle(e);
    }
  }

  @PostMapping("/sesiones/{idSesion}/salas")
  public ResponseEntity<?> asignarSala(@PathVariable Long idSesion,
                                       @RequestParam(required = false) List<Long> idSalas,
                                       @RequestBody(required = false) SesionSalasRequest body) {
    try {
      List<Long> finalSalas = firstNonEmpty(idSalas, body != null ? body.getIdSalas() : null);
      sesionService.asignarSala(idSesion, finalSalas);
      return ResponseEntity.status(HttpStatus.CREATED).build();
    } catch (Exception e) {
      return handle(e);
    }
  }

  @DeleteMapping("/sesiones/{idSesion}/salas/{idSala}")
  public ResponseEntity<?> desasignarSala(@PathVariable Long idSesion, @PathVariable Long idSala) {
    try {
      sesionService.desasignarSala(idSesion, idSala);
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

  private <T> T coalesce(T first, T second) {
    return first != null ? first : second;
  }

  private List<Long> firstNonEmpty(List<Long> first, List<Long> second) {
    if (first != null && !first.isEmpty()) {
      return first;
    }
    return second;
  }

  public static class SesionCreateRequest {
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Integer aforo;
    private List<Long> idSalas;

    public LocalDate getFecha() {
      return fecha;
    }

    public void setFecha(LocalDate fecha) {
      this.fecha = fecha;
    }

    public LocalTime getHoraInicio() {
      return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
      this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
      return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
      this.horaFin = horaFin;
    }

    public Integer getAforo() {
      return aforo;
    }

    public void setAforo(Integer aforo) {
      this.aforo = aforo;
    }

    public List<Long> getIdSalas() {
      return idSalas;
    }

    public void setIdSalas(List<Long> idSalas) {
      this.idSalas = idSalas;
    }
  }

  public static class SesionSalasRequest {
    private List<Long> idSalas;

    public List<Long> getIdSalas() {
      return idSalas;
    }

    public void setIdSalas(List<Long> idSalas) {
      this.idSalas = idSalas;
    }
  }
}
