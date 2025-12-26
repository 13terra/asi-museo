package es.museum.asi.model.service.dto;

import es.museum.asi.model.domain.Sesion;
import es.museum.asi.model.enums.EstadoReserva;
import es.museum.asi.model.enums.EstadoSesion;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO Básico para listados
 */
public class SesionDTO {
  private Long idSesion;

  @NotNull
  private LocalDateTime horaInicio;

  @NotNull
  private LocalDateTime horaFin;

  private int aforo;
  private int aforoOcupado;
  private EstadoSesion estado;

  // Info contexto
  private Long idEdicion;
  private String nombreExposicion;
  private List<String> nombresSalas;
  private List<Long> idSalas;

  public SesionDTO(Sesion sesion) {
    this.idSesion = sesion.getIdSesion();
    this.horaInicio = sesion.getHoraInicio();
    this.horaFin = sesion.getHoraFin();
    this.aforo = sesion.getAforo();
    this.estado = sesion.getEstadoSesion();
    this.idEdicion = sesion.getEdicion().getIdEdicion();
    this.nombreExposicion = sesion.getEdicion().getExposicion().getTitulo();

    // Calcular aforo ocupado
    this.aforoOcupado = sesion.getReservas().stream()
      .filter(r -> r.getEstadoReserva() == EstadoReserva.CONFIRMADA)
      .mapToInt(r -> r.getEntradas() != null ? r.getEntradas().size() : 0)
      .sum();

    // Obtener nombres de salas ordenadas
    this.nombresSalas = sesion.getOrdenes().stream()
      .sorted((o1, o2) -> o1.getOrden().compareTo(o2.getOrden()))
      .map(orden -> orden.getSala().getNombre())
      .collect(Collectors.toList());

    // Obtener ids de salas ordenadas
    this.idSalas = sesion.getOrdenes().stream()
      .sorted((o1, o2) -> o1.getOrden().compareTo(o2.getOrden()))
      .map(orden -> orden.getSala().getIdSala())
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

  public int getAforo() {
    return aforo;
  }

  public void setAforo(int aforo) {
    this.aforo = aforo;
  }

  public int getAforoOcupado() {
    return aforoOcupado;
  }

  public void setAforoOcupado(int aforoOcupado) {
    this.aforoOcupado = aforoOcupado;
  }

  public EstadoSesion getEstado() {
    return estado;
  }

  public void setEstado(EstadoSesion estado) {
    this.estado = estado;
  }

  public Long getIdEdicion() {
    return idEdicion;
  }

  public void setIdEdicion(Long idEdicion) {
    this.idEdicion = idEdicion;
  }

  public String getNombreExposicion() {
    return nombreExposicion;
  }

  public void setNombreExposicion(String nombreExposicion) {
    this.nombreExposicion = nombreExposicion;
  }

  public List<String> getNombresSalas() {
    return nombresSalas;
  }

  public void setNombresSalas(List<String> nombresSalas) {
    this.nombresSalas = nombresSalas;
  }

  public List<Long> getIdSalas() {
    return idSalas;
  }

  public void setIdSalas(List<Long> idSalas) {
    this.idSalas = idSalas;
  }
}
