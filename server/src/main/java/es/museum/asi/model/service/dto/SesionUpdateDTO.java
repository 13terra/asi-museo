package es.museum.asi.model.service.dto;

import es.museum.asi.model.enums.EstadoSesion;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class SesionUpdateDTO {
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Integer aforo;
    private List<Long> idSalas;
    private EstadoSesion estado;


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

  public EstadoSesion getEstado() {
    return estado;
  }

  public void setEstado(EstadoSesion estado) {
    this.estado = estado;
  }
}
