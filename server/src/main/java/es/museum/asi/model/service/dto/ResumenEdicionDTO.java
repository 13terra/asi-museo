package es.museum.asi.model.service.dto;

import es.museum.asi.model.domain.Edicion;
import es.museum.asi.model.enums.EstadoEdicion;

import java.time.LocalDate;

public class ResumenEdicionDTO {
  private Long idEdicion;
  private LocalDate fechaInicio;
  private LocalDate fechaFin;
  private EstadoEdicion estadoEdicion;
  private int numSesiones;
  private int numPiezas;

  public ResumenEdicionDTO() {}

  public ResumenEdicionDTO(Edicion edicion) {
    this.idEdicion = edicion.getIdEdicion();
    this.fechaInicio = edicion.getFechaInicio();
    this.fechaFin = edicion.getFechaFin();
    this.estadoEdicion = edicion.getEstado();
    this.numSesiones = edicion.getSesiones() != null ? edicion.getSesiones().size() : 0;
    this.numPiezas = edicion.getPiezasExpuestas() != null ? edicion.getPiezasExpuestas().size() : 0;
  }


  public Long getIdEdicion() {
    return idEdicion;
  }

  public void setIdEdicion(Long idEdicion) {
    this.idEdicion = idEdicion;
  }

  public LocalDate getFechaInicio() {
    return fechaInicio;
  }

  public void setFechaInicio(LocalDate fechaInicio) {
    this.fechaInicio = fechaInicio;
  }

  public LocalDate getFechaFin() {
    return fechaFin;
  }

  public void setFechaFin(LocalDate fechaFin) {
    this.fechaFin = fechaFin;
  }

  public EstadoEdicion getEstadoEdicion() {
    return estadoEdicion;
  }

  public void setEstadoEdicion(EstadoEdicion estadoEdicion) {
    this.estadoEdicion = estadoEdicion;
  }

  public int getNumSesiones() {
    return numSesiones;
  }

  public void setNumSesiones(int numSesiones) {
    this.numSesiones = numSesiones;
  }

  public int getNumPiezas() {
    return numPiezas;
  }

  public void setNumPiezas(int numPiezas) {
    this.numPiezas = numPiezas;
  }
}
