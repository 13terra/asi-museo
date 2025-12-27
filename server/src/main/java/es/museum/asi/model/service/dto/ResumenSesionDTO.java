package es.museum.asi.model.service.dto;

import es.museum.asi.model.domain.Sesion;
import es.museum.asi.model.enums.EstadoReserva;
import es.museum.asi.model.enums.EstadoSesion;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
  private List<SalaOrdenDTO> salas;

  public ResumenSesionDTO() {}

  public ResumenSesionDTO (Sesion sesion) {
    this.idSesion = sesion.getIdSesion();
    this.horaInicio = sesion.getHoraInicio();
    this.horaFin = sesion.getHoraFin();
    this.aforo = sesion.getAforo();
    this.estadoSesion = sesion.getEstadoSesion();

    //Calculamos aforo ocupado
    this.aforoOcupado = sesion.getReservas().stream()
      .filter(r -> r.getEstadoReserva() == EstadoReserva.CONFIRMADA)
      .mapToInt(r -> r.getEntradas() != null ? r.getEntradas().size() : 0)
      .sum();

    this.salas = sesion.getOrdenes().stream()
      .sorted((o1,o2) -> o1.getOrden().compareTo(o2.getOrden()))
      .map(SalaOrdenDTO::new)
      .collect(Collectors.toList());
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

  public List<SalaOrdenDTO> getSalas() {
    return salas;
  }

  public void setSalas(List<SalaOrdenDTO> salas) {
    this.salas = salas;
  }
}
