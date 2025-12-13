package es.museum.asi.model.service.dto;

import es.museum.asi.model.domain.Reserva;
import es.museum.asi.model.enums.EstadoReserva;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO detallado de reserva (HU53, HU58)
 */
public class ReservaDetalleDTO {
  private Long idReserva;
  private LocalDateTime fechaReserva;
  private EstadoReserva estado;

  //Datos comprador
  private String nombrePila;
  private String apellido1;
  private String apellido2;
  private String email;
  private String telefono;
  private String pais;

  // Datos de la sesión
  private Long idSesion;
  private LocalDateTime fechaHoraSesion;
  private String nombreExposicion;
  private Long idEdicion;

  // Entradas
  private List<ResumenEntradaDTO> entradas;
  private Float importeTotal;
  private int numEntradas;

  public ReservaDetalleDTO() {}


  public ReservaDetalleDTO(Reserva reserva, boolean incluirEntradas) {
    this.idReserva = reserva.getIdReserva();
    this.fechaReserva = reserva.getFechaReserva();
    this.estado = reserva.getEstadoReserva();
    this.nombrePila = reserva.getNombrePila();
    this.apellido1 = reserva. getApellido1();
    this.apellido2 = reserva.getApellido2();
    this.email = reserva.getEmail();
    this.telefono = reserva.getTelefono();
    this.pais = reserva.getPais();
    this.idSesion = reserva.getSesion().getIdSesion();
    this.fechaHoraSesion = reserva.getSesion().getHoraInicio();
    this.nombreExposicion = reserva.getSesion().getEdicion().getExposicion().getTitulo();
    this.idEdicion = reserva. getSesion().getEdicion().getIdEdicion();

    if (incluirEntradas) {
      this.entradas = reserva.getEntradas().stream()
        .map(ResumenEntradaDTO::new)
        .collect(Collectors.toList());

      this.numEntradas = this.entradas.size();
      this.importeTotal = this. entradas.stream()
        .map(ResumenEntradaDTO::getPrecio)
        .reduce(0f, Float::sum);
    }
  }


  public Long getIdReserva() {
    return idReserva;
  }

  public void setIdReserva(Long idReserva) {
    this.idReserva = idReserva;
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

  public String getNombrePila() {
    return nombrePila;
  }

  public void setNombrePila(String nombrePila) {
    this.nombrePila = nombrePila;
  }

  public String getApellido1() {
    return apellido1;
  }

  public void setApellido1(String apellido1) {
    this.apellido1 = apellido1;
  }

  public String getApellido2() {
    return apellido2;
  }

  public void setApellido2(String apellido2) {
    this.apellido2 = apellido2;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getTelefono() {
    return telefono;
  }

  public void setTelefono(String telefono) {
    this.telefono = telefono;
  }

  public String getPais() {
    return pais;
  }

  public void setPais(String pais) {
    this.pais = pais;
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

  public List<ResumenEntradaDTO> getEntradas() {
    return entradas;
  }

  public void setEntradas(List<ResumenEntradaDTO> entradas) {
    this.entradas = entradas;
  }

  public Float getImporteTotal() {
    return importeTotal;
  }

  public void setImporteTotal(Float importeTotal) {
    this.importeTotal = importeTotal;
  }

  public int getNumEntradas() {
    return numEntradas;
  }

  public void setNumEntradas(int numEntradas) {
    this.numEntradas = numEntradas;
  }
}
