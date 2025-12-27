package es.museum.asi.web.resources;

import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.museum.asi.model.enums.EstadoReserva;
import es.museum.asi.model.exception.NotFoundException;
import es.museum.asi.model.exception.OperationNotAllowed;
import es.museum.asi.model.service.ReservaService;
import es.museum.asi.model.service.dto.ReservaDTO;
import es.museum.asi.model.service.dto.ReservaDetalleDTO;
import es.museum.asi.model.service.dto.ReservarEntradasDTO;
import es.museum.asi.web.exceptions.InvalidPermissionException;
import es.museum.asi.web.exceptions.RequestBodyNotValidException;
import es.museum.asi.web.util.ErrorDTO;
import jakarta.validation.Valid;

/**
 * Resource de reservas (HU51-HU54, HU57-HU58).
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ReservaResource {

  @Autowired
  private ReservaService reservaService;

  @PostMapping("/reservas")
  public ResponseEntity<?> reservar(@Valid @RequestBody ReservarEntradasDTO request, Errors errors) {
    try {
      if (errors.hasErrors()) {
        throw new RequestBodyNotValidException(errors);
      }
      ReservaDetalleDTO dto = reservaService.reservarEntradas(request);
      return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    } catch (RequestBodyNotValidException e) {
      return ResponseEntity.badRequest().body(new ErrorDTO(e.getMessage()));
    } catch (NotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDTO(e.getMessage()));
    } catch (OperationNotAllowed e) {
      return ResponseEntity.badRequest().body(new ErrorDTO(e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorDTO("Error interno"));
    }
  }

  @GetMapping("/mis-reservas")
  public ResponseEntity<Collection<ReservaDTO>> findMisReservas(@RequestParam(required = false) EstadoReserva estado) {
    return ResponseEntity.ok(reservaService.findMisReservas(estado));
  }

  @GetMapping("/mis-reservas/{idReserva}")
  public ResponseEntity<?> findMiDetalle(@PathVariable Long idReserva) {
    try {
      return ResponseEntity.ok(reservaService.findMiReservaDetalle(idReserva));
    } catch (NotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDTO(e.getMessage()));
    } catch (OperationNotAllowed e) {
      return ResponseEntity.badRequest().body(new ErrorDTO(e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorDTO("Error interno"));
    }
  }

  @GetMapping("/mis-reservas/{idReserva}/entradas")
  public ResponseEntity<?> findMisEntradasByReserva(@PathVariable Long idReserva) {
    try {
      return ResponseEntity.ok(reservaService.findMisEntradasByReserva(idReserva));
    } catch (NotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDTO(e.getMessage()));
    } catch (OperationNotAllowed e) {
      return ResponseEntity.badRequest().body(new ErrorDTO(e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorDTO("Error interno"));
    }
  }

  @PutMapping("/mis-reservas/{idReserva}/cancelar")
  public ResponseEntity<?> cancelarMiReserva(@PathVariable Long idReserva) {
    try {
      return ResponseEntity.ok(reservaService.cancelarMiReserva(idReserva));
    } catch (NotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDTO(e.getMessage()));
    } catch (OperationNotAllowed e) {
      return ResponseEntity.badRequest().body(new ErrorDTO(e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorDTO("Error interno"));
    }
  }

  @GetMapping("/ediciones/{idEdicion}/reservas")
  public ResponseEntity<?> findReservasByEdicion(
      @PathVariable Long idEdicion,
      @RequestParam(required = false) Long idSesion,
      @RequestParam(required = false) EstadoReserva estado,
      @RequestParam(required = false) LocalDateTime fechaDesde,
      @RequestParam(required = false) LocalDateTime fechaHasta) {
    try {
      Collection<ReservaDTO> reservas = reservaService.findReservasByEdicion(idEdicion, idSesion, estado, fechaDesde, fechaHasta);
      return ResponseEntity.ok(reservas);
    } catch (NotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDTO(e.getMessage()));
    } catch (InvalidPermissionException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorDTO(e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorDTO("Error interno"));
    }
  }

  @GetMapping("/reservas/{idReserva}")
  public ResponseEntity<?> findReservaDetalleAdmin(@PathVariable Long idReserva) {
    try {
      ReservaDetalleDTO dto = reservaService.findReservaDetalleAdmin(idReserva);
      return ResponseEntity.ok(dto);
    } catch (NotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDTO(e.getMessage()));
    } catch (InvalidPermissionException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorDTO(e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorDTO("Error interno"));
    }
  }
}
