package es.museum.asi.model.service.dto;


import es.museum.asi.model.domain.Edicion;
import es.museum.asi.model.enums.EstadoEdicion;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * DTO básico para listados
 */
public class EdicionDTO {
  private Long idEdicion;

  @NotNull
  private LocalDate fechaInicio;
  @NotNull
  private LocalDate fechaFin;

  private String nombre;
  private EstadoEdicion estadoEdicion;
  private String tituloExpo;
  private Integer numSesiones;
  private Integer numPiezas;

  public EdicionDTO() {}

  public EdicionDTO(Edicion edicion) {
    this.idEdicion = edicion.getIdEdicion();
    this.fechaInicio = edicion.getFechaInicio();
    this.fechaFin = edicion.getFechaFin();
    this.nombre = edicion.getNombre();
    this.estadoEdicion = edicion.getEstado();
    this.tituloExpo = edicion.getExposicion().getTitulo();
    this.numSesiones = edicion.getSesiones() != null ?  edicion.getSesiones().size() : 0;
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

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public EstadoEdicion getEstadoEdicion() {
    return estadoEdicion;
  }

  public void setEstadoEdicion(EstadoEdicion estadoEdicion) {
    this.estadoEdicion = estadoEdicion;
  }

  public String getTituloExpo() {
    return tituloExpo;
  }

  public void setTituloExpo(String tituloExpo) {
    this.tituloExpo = tituloExpo;
  }

  public Integer getNumSesiones() {
    return numSesiones;
  }

  public void setNumSesiones(Integer numSesiones) {
    this.numSesiones = numSesiones;
  }

  public Integer getNumPiezas() {
    return numPiezas;
  }

  public void setNumPiezas(Integer numPiezas) {
    this.numPiezas = numPiezas;
  }
}
