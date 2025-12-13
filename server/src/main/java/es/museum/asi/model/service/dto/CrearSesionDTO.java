package es.museum.asi.model.service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para crear una sesión (HU31)
 */
public class CrearSesionDTO {
  @NotNull
  private LocalDate fecha;

  @NotNull
  private LocalDateTime horaInicio;

  @NotNull
  private LocalDateTime horaFin;

  @Min(1)
  private Integer aforo;

  @NotNull
  @Size(min = 1, message = "Debe tener al menos una sala")
  private List<Long> idSalas;


  public CrearSesionDTO() {}


  public LocalDate getFecha() {
    return fecha;
  }

  public void setFecha(LocalDate fecha) {
    this.fecha = fecha;
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

  public List<Long> getIdSalas() {
    return idSalas;
  }

  public void setIdSalas(List<Long> idSalas) {
    this.idSalas = idSalas;
  }
}
