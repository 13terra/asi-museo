package es.museum.asi.controller;

import es.museum.asi.model.enums.EstadoReserva;
import es.museum.asi.model.exception.NotFoundException;
import es.museum.asi.model.exception.OperationNotAllowed;
import es.museum.asi.model.service.ReservaService;
import es.museum.asi.model.service.TipoEntradaService;
import es.museum.asi.model.service.dto.*;
import es.museum.asi.web.exceptions.InvalidPermissionException;
import es.museum.asi.web.exceptions.RequestBodyNotValidException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * Controlador REST para gestión de reservas y tipos de entrada
 * Implementa HU50-HU54, HU57-HU58
 */
@RestController
@RequestMapping("/api")
public class ReservaController {

  @Autowired
  private ReservaService reservaService;

  @Autowired
  private TipoEntradaService tipoEntradaService;

  // ==================== TIPO ENTRADA (HU50) ====================

  /**
   * HU50 - Listar tipos de entrada
   * Público: visitantes necesitan ver precios al reservar
   */
  @GetMapping("/tipos-entrada")
  public Collection<TipoEntradaDTO> findAllTiposEntrada() {
    return tipoEntradaService.findAll();
  }

  /**
   * HU50 - Obtener tipo de entrada por ID
   */
  @GetMapping("/tipos-entrada/{id}")
  public TipoEntradaDTO findTipoEntradaById(@PathVariable Long id) throws NotFoundException {
    return tipoEntradaService.findById(id);
  }

  // ==================== RESERVAS (HU51-HU54) ====================

  /**
   * HU51 - Reservar entradas
   * Solo VISITANTE autenticado
   */
  @PostMapping("/reservas")
  public ReservaDetalleDTO reservarEntradas(@Valid @RequestBody ReservarEntradasDTO request, Errors errors)
      throws NotFoundException, OperationNotAllowed, RequestBodyNotValidException {
    if (errors.hasErrors()) {
      throw new RequestBodyNotValidException(errors);
    }
    return reservaService.reservarEntradas(request);
  }

  /**
   * HU52 - Listar mis reservas (visitante autenticado)
   * Con filtro opcional por estado
   */
  @GetMapping("/mis-reservas")
  public Collection<ReservaDTO> findMisReservas(
      @RequestParam(required = false) EstadoReserva estado) {
    return reservaService.findMisReservas(estado);
  }

  /**
   * HU53 - Ver detalle de mi reserva
   * Solo el propietario puede ver su reserva
   */
  @GetMapping("/mis-reservas/{idReserva}")
  public ReservaDetalleDTO findMiReservaDetalle(@PathVariable Long idReserva)
      throws NotFoundException, OperationNotAllowed {
    return reservaService.findMiReservaDetalle(idReserva);
  }

  /**
   * HU54 - Cancelar mi reserva
   * Validaciones: margen mínimo 24h, sesión no cancelada/finalizada
   */
  @PutMapping("/mis-reservas/{idReserva}/cancelar")
  public ReservaDTO cancelarMiReserva(@PathVariable Long idReserva)
      throws NotFoundException, OperationNotAllowed {
    return reservaService.cancelarMiReserva(idReserva);
  }

  // ==================== GESTIÓN RESERVAS (ADMIN/GESTOR) ====================

  /**
   * HU57 - Listar reservas de una edición (ADMIN/GESTOR)
   * Con filtros por sesión, estado, fecha
   */
  @GetMapping("/ediciones/{idEdicion}/reservas")
  public Collection<ReservaDTO> findReservasByEdicion(
      @PathVariable Long idEdicion,
      @RequestParam(required = false) Long idSesion,
      @RequestParam(required = false) EstadoReserva estado,
      @RequestParam(required = false) LocalDateTime fechaDesde,
      @RequestParam(required = false) LocalDateTime fechaHasta)
      throws NotFoundException, InvalidPermissionException {
    return reservaService.findReservasByEdicion(idEdicion, idSesion, estado, fechaDesde, fechaHasta);
  }

  /**
   * HU58 - Ver detalle de reserva (ADMIN/GESTOR)
   */
  @GetMapping("/reservas/{idReserva}")
  public ReservaDetalleDTO findReservaDetalleAdmin(@PathVariable Long idReserva)
      throws NotFoundException, InvalidPermissionException {
    return reservaService.findReservaDetalleAdmin(idReserva);
  }
}
