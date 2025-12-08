package es.museum.asi.model.service.dto;

import es.museum.asi.model.domain.Reserva;
import es.museum.asi.model.enums.EstadoReserva;

import java.time.LocalDateTime;

/**
 * DTO resumido de reserva para incluir en listados
 */
public class ResumenReservaDTO {
  private Long idReserva;
  private LocalDateTime fechaReserva;
  private EstadoReserva estado;
  private String nombreExposicion;  // A través de sesion -> edicion -> exposicion
  private String nombreEdicion;
  private LocalDateTime fechaSesion;
  private int numEntradas;

  public ResumenReservaDTO() {
  }

  public ResumenReservaDTO(Reserva reserva) {
    this.idReserva = reserva.getIdReserva();
    this.fechaReserva = reserva.getFechaReserva();
    this. estado = reserva.getEstadoReserva();

    // Navegar a través de las relaciones
    if (reserva. getSesion() != null && reserva.getSesion().getEdicion() != null) {
      this.nombreExposicion = reserva.getSesion().getEdicion().getExposicion(). getTitulo();
      this.fechaSesion = reserva.getSesion().getHoraInicio();
    }
    this.numEntradas = reserva.getEntradas() != null ? reserva.getEntradas().size() : 0;
  }

  public Long getIdReserva() {
    return idReserva;
  }

  public void setIdReserva(Long idReserva) {
    this.idReserva = idReserva;
  }

  public LocalDateTime getFechaReserva() {
    return fechaReserva;
  }

  public void setFechaReserva(LocalDateTime fechaReserva) {
    this.fechaReserva = fechaReserva;
  }

  public EstadoReserva getEstado() {
    return estado;
  }

  public void setEstado(EstadoReserva estado) {
    this.estado = estado;
  }

  public String getNombreExposicion() {
    return nombreExposicion;
  }

  public void setNombreExposicion(String nombreExposicion) {
    this.nombreExposicion = nombreExposicion;
  }

  public String getNombreEdicion() {
    return nombreEdicion;
  }

  public void setNombreEdicion(String nombreEdicion) {
    this.nombreEdicion = nombreEdicion;
  }

  public LocalDateTime getFechaSesion() {
    return fechaSesion;
  }

  public void setFechaSesion(LocalDateTime fechaSesion) {
    this.fechaSesion = fechaSesion;
  }

  public int getNumEntradas() {
    return numEntradas;
  }

  public void setNumEntradas(int numEntradas) {
    this.numEntradas = numEntradas;
  }
}
