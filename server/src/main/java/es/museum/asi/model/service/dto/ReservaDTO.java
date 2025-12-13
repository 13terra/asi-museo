package es.museum.asi.model.service.dto;

import es.museum.asi.model.domain.Entrada;
import es.museum.asi.model.domain.Reserva;
import es.museum.asi.model.enums.EstadoReserva;

import java.time.LocalDateTime;

/**
 * DTO básico para listados
 */
public class ReservaDTO {
  private Long idReserva;
  private String nombreComprador;
  private String emailComprador;
  private int numEntradas;
  private Float importeTotal;
  private LocalDateTime fechaReserva;
  private EstadoReserva estado;

  //Contexto
  private Long idSesion;
  private LocalDateTime fechaSesion;
  private String nombreExposicion;

  public ReservaDTO() {}

  public ReservaDTO(Reserva reserva) {
    this.idReserva = reserva.getIdReserva();
    this.nombreComprador = reserva.getNombrePila() + " " + reserva.getApellido1();
    this.emailComprador = reserva. getEmail();
    this.numEntradas = reserva.getEntradas() != null ? reserva.getEntradas().size() : 0;
    this.fechaReserva = reserva. getFechaReserva();
    this.estado = reserva. getEstadoReserva();
    this.idSesion = reserva.getSesion().getIdSesion();
    this.fechaSesion = reserva.getSesion().getHoraInicio();
    this.nombreExposicion = reserva.getSesion().getEdicion().getExposicion().getTitulo();

    // Calcular importe total
    this.importeTotal = reserva. getEntradas().stream()
      .map(Entrada::getPrecio)
      .reduce(0f, Float::sum);
  }


  public Long getIdReserva() {
    return idReserva;
  }

  public void setIdReserva(Long idReserva) {
    this.idReserva = idReserva;
  }

  public String getNombreComprador() {
    return nombreComprador;
  }

  public void setNombreComprador(String nombreComprador) {
    this.nombreComprador = nombreComprador;
  }

  public String getEmailComprador() {
    return emailComprador;
  }

  public void setEmailComprador(String emailComprador) {
    this.emailComprador = emailComprador;
  }

  public int getNumEntradas() {
    return numEntradas;
  }

  public void setNumEntradas(int numEntradas) {
    this.numEntradas = numEntradas;
  }

  public Float getImporteTotal() {
    return importeTotal;
  }

  public void setImporteTotal(Float importeTotal) {
    this.importeTotal = importeTotal;
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

  public Long getIdSesion() {
    return idSesion;
  }

  public void setIdSesion(Long idSesion) {
    this.idSesion = idSesion;
  }

  public LocalDateTime getFechaSesion() {
    return fechaSesion;
  }

  public void setFechaSesion(LocalDateTime fechaSesion) {
    this.fechaSesion = fechaSesion;
  }

  public String getNombreExposicion() {
    return nombreExposicion;
  }

  public void setNombreExposicion(String nombreExposicion) {
    this.nombreExposicion = nombreExposicion;
  }
}
