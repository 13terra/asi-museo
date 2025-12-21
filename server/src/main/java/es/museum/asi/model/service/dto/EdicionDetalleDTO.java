package es.museum.asi.model.service.dto;

import es.museum.asi.model.domain.Edicion;
import es.museum.asi.model.enums.EstadoEdicion;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO detallado para HU21
 */
public class EdicionDetalleDTO {
  private Long idEdicion;
  private LocalDate fechaInicio;
  private LocalDate fechaFin;
  private EstadoEdicion estadoEdicion;

  // Info expo padre
  private Long idExpo;
  private String tituloExpo;

  // Resumen ejecutivo sólo para admin/gestor
  private Integer numPiezas;
  private Integer numSesiones;
  private Integer numSalasAsignadas;
  private Integer numReservas;
  private Integer numEntradas;

  // Listados resumidos sólo para admin/gestor
  private List<ResumenSesionDTO> sesiones;
  private List<ResumenPiezaExpuestaDTO> piezasExpuestas;

  public EdicionDetalleDTO() {}

  public EdicionDetalleDTO(Edicion edicion, boolean incluirInfoGestion) {
    this.idEdicion = edicion.getIdEdicion();
    this.fechaInicio = edicion.getFechaInicio();
    this.fechaFin = edicion.getFechaFin();
    this.estadoEdicion = edicion.getEstado();
    this.idExpo = edicion.getExposicion().getIdExposicion();
    this.tituloExpo = edicion.getExposicion().getTitulo();

    if (incluirInfoGestion) {
      this.numPiezas = edicion.getPiezasExpuestas() != null ? edicion.getPiezasExpuestas().size() : 0;
      this.numSesiones = edicion.getSesiones() != null ? edicion.getSesiones().size() : 0;

      // Contar salas únicas a través de sesiones
      this.numSalasAsignadas = (int) edicion.getSesiones().stream()
        .flatMap(sesion -> sesion.getOrdenes().stream())
        .map(orden -> orden.getSala().getIdSala())
        .distinct()
        .count();

      this.numReservas = edicion.getSesiones().stream()
        .mapToInt(sesion -> sesion.getReservas() != null ? sesion.getReservas().size() : 0)
        .sum();

      this.numEntradas = edicion.getSesiones().stream()
        .flatMap(sesion -> sesion.getReservas().stream())
        .mapToInt(reserva -> reserva.getEntradas() != null ? reserva.getEntradas().size() : 0)
        .sum();

      // Mapeamos listas resumidas
      this.sesiones = edicion.getSesiones().stream()
        .map(ResumenSesionDTO::new)
        .collect(Collectors.toList());
    }

    this.piezasExpuestas = edicion.getPiezasExpuestas().stream()
      .map(ResumenPiezaExpuestaDTO::new)
      .collect(Collectors.toList());
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

  public Long getIdExpo() {
    return idExpo;
  }

  public void setIdExpo(Long idExpo) {
    this.idExpo = idExpo;
  }

  public String getTituloExpo() {
    return tituloExpo;
  }

  public void setTituloExpo(String tituloExpo) {
    this.tituloExpo = tituloExpo;
  }

  public Integer getNumPiezas() {
    return numPiezas;
  }

  public void setNumPiezas(Integer numPiezas) {
    this.numPiezas = numPiezas;
  }

  public Integer getNumSesiones() {
    return numSesiones;
  }

  public void setNumSesiones(Integer numSesiones) {
    this.numSesiones = numSesiones;
  }

  public Integer getNumSalasAsignadas() {
    return numSalasAsignadas;
  }

  public void setNumSalasAsignadas(Integer numSalasAsignadas) {
    this.numSalasAsignadas = numSalasAsignadas;
  }

  public Integer getNumReservas() {
    return numReservas;
  }

  public void setNumReservas(Integer numReservas) {
    this.numReservas = numReservas;
  }

  public Integer getNumEntradas() {
    return numEntradas;
  }

  public void setNumEntradas(Integer numEntradas) {
    this.numEntradas = numEntradas;
  }

  public List<ResumenSesionDTO> getSesiones() {
    return sesiones;
  }

  public void setSesiones(List<ResumenSesionDTO> sesiones) {
    this.sesiones = sesiones;
  }

  public List<ResumenPiezaExpuestaDTO> getPiezasExpuestas() {
    return piezasExpuestas;
  }

  public void setPiezasExpuestas(List<ResumenPiezaExpuestaDTO> piezasExpuestas) {
    this.piezasExpuestas = piezasExpuestas;
  }
}
