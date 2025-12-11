package es.museum.asi.model.service.dto;

import es.museum.asi.model.domain.Sesion;
import es.museum.asi.model.enums.EstadoReserva;
import es.museum.asi.model.enums.EstadoSesion;

import java.time.LocalDateTime;

/**
 * DTO Resumido de Sesión (para listados)
 */
public class ResumenSesionDTO {
  private Long idSesion;
  private LocalDateTime horaInicio;
  private LocalDateTime horaFin;
  private Integer aforo;
  private Integer aforoOcupado;
  private EstadoSesion estadoSesion;

  public ResumenSesionDTO() {}

  public ResumenSesionDTO (Sesion sesion) {
    this.idSesion = sesion.getIdSesion();
    this.horaInicio = sesion.getHoraInicio();
    this.horaFin = sesion.getHoraFin();
    this.aforo = sesion.getAforo();
    this.estadoSesion = estadoSesion;

    //Calculamos aforo ocupado
    this.aforoOcupado = sesion.getReservas().stream()
      .filter(r -> r.getEstadoReserva() == EstadoReserva.CONFIRMADA)
      .mapToInt(r -> r.getEntradas() != null ? r.getEntradas().size() : 0)
      .sum();
  }

  public Long getIdSesion() {
    return idSesion;
  }

  public void setIdSesion(Long idSesion) {
    this.idSesion = idSesion;
  }

  public LocalDateTime getHoraInicio() {
    return horaInicio;
  }

  public void setHoraInicio(LocalDateTime horaInicio) {
    this.horaInicio = horaInicio;
  }

  public LocalDateTime getHoraFin() {
    return horaFin;
  }

  public void setHoraFin(LocalDateTime horaFin) {
    this.horaFin = horaFin;
  }

  public Integer getAforo() {
    return aforo;
  }

  public void setAforo(Integer aforo) {
    this.aforo = aforo;
  }

  public Integer getAforoOcupado() {
    return aforoOcupado;
  }

  public void setAforoOcupado(Integer aforoOcupado) {
    this.aforoOcupado = aforoOcupado;
  }

  public EstadoSesion getEstadoSesion() {
    return estadoSesion;
  }

  public void setEstadoSesion(EstadoSesion estadoSesion) {
    this.estadoSesion = estadoSesion;
  }
}
