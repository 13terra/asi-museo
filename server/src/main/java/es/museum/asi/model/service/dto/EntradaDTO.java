package es.museum.asi.model.service.dto;

import es.museum.asi.model.enums.EstadoReserva;

import java.time.LocalDateTime;

/**
 * DTO de entrada para listados (HU55)
 */
public class EntradaDTO {
  private Long idEntrada;
  private Long idReserva;

  // Datos del asistente
  private String nombreCompletoAsistente;
  private String dni;

  // Tipo de entrada
  private String tipoEntrada;
  private Float precio;

  // Datos de la sesión
  private Long idSesion;
  private LocalDateTime fechaHoraSesion;
  private String nombreExposicion;
  private Long idEdicion;

  // Estado
  private EstadoReserva estadoReserva;

  public EntradaDTO() {}


  public Long getIdEntrada() {
    return idEntrada;
  }

  public void setIdEntrada(Long idEntrada) {
    this.idEntrada = idEntrada;
  }

  public Long getIdReserva() {
    return idReserva;
  }

  public void setIdReserva(Long idReserva) {
    this.idReserva = idReserva;
  }

  public String getNombreCompletoAsistente() {
    return nombreCompletoAsistente;
  }

  public void setNombreCompletoAsistente(String nombreCompletoAsistente) {
    this.nombreCompletoAsistente = nombreCompletoAsistente;
  }

  public String getDni() {
    return dni;
  }

  public void setDni(String dni) {
    this.dni = dni;
  }

  public String getTipoEntrada() {
    return tipoEntrada;
  }

  public void setTipoEntrada(String tipoEntrada) {
    this.tipoEntrada = tipoEntrada;
  }

  public Float getPrecio() {
    return precio;
  }

  public void setPrecio(Float precio) {
    this.precio = precio;
  }

  public Long getIdSesion() {
    return idSesion;
  }

  public void setIdSesion(Long idSesion) {
    this.idSesion = idSesion;
  }

  public LocalDateTime getFechaHoraSesion() {
    return fechaHoraSesion;
  }

  public void setFechaHoraSesion(LocalDateTime fechaHoraSesion) {
    this.fechaHoraSesion = fechaHoraSesion;
  }

  public String getNombreExposicion() {
    return nombreExposicion;
  }

  public void setNombreExposicion(String nombreExposicion) {
    this.nombreExposicion = nombreExposicion;
  }

  public Long getIdEdicion() {
    return idEdicion;
  }

  public void setIdEdicion(Long idEdicion) {
    this.idEdicion = idEdicion;
  }

  public EstadoReserva getEstadoReserva() {
    return estadoReserva;
  }

  public void setEstadoReserva(EstadoReserva estadoReserva) {
    this.estadoReserva = estadoReserva;
  }
}
