package es.museum.asi.model.service.dto;

import es.museum.asi.model.domain.Entrada;
import es.museum.asi.model.enums.EstadoReserva;

import java.time.LocalDateTime;

/**
 * DTO detallado de entrada (HU56)
 * Para mostrar en el museo y validar acceso
 */
public class EntradaDetalleDTO {
  private Long idEntrada;
  private Long idReserva;

  // Datos del asistente
  private String nombrePila;
  private String apellido1;
  private String apellido2;
  private String dni;

  // Tipo de entrada
  private String tipoEntrada;
  private String descripcionTipo;
  private Float precio;

  // Datos de la sesión
  private Long idSesion;
  private LocalDateTime fechaHoraSesion;
  private LocalDateTime horaFinSesion;
  private String nombreExposicion;
  private String nombresSalas;

  // Estado
  private EstadoReserva estadoReserva;
  private LocalDateTime fechaReserva;

  // Datos del comprador (para referencia)
  private String nombreComprador;
  private String emailComprador;

  public EntradaDetalleDTO() {}


  public EntradaDetalleDTO(Entrada entrada) {
    this.idEntrada = entrada.getIdEntrada();
    this.idReserva = entrada.getReserva().getIdReserva();

    // Datos del asistente
    this. nombrePila = entrada.getNombrePila();
    this.apellido1 = entrada.getApellido1();
    this.apellido2 = entrada. getApellido2();
    this.dni = entrada.getDni();

    // Tipo de entrada
    this.tipoEntrada = entrada.getTipoEntrada().getNombre();
    this.descripcionTipo = entrada.getTipoEntrada().getDescripcion();
    this.precio = entrada.getPrecio();

    // Datos de la sesión
    this.idSesion = entrada.getReserva().getSesion().getIdSesion();
    this.fechaHoraSesion = entrada.getReserva().getSesion().getHoraInicio();
    this.horaFinSesion = entrada. getReserva().getSesion().getHoraFin();
    this.nombreExposicion = entrada.getReserva().getSesion().getEdicion().getExposicion().getTitulo();

    // Salas (concatenadas)
    this.nombresSalas = entrada.getReserva().getSesion().getOrdenes().stream()
      .sorted((o1, o2) -> o1.getOrden().compareTo(o2.getOrden()))
      .map(orden -> orden.getSala().getNombre())
      .reduce((s1, s2) -> s1 + ", " + s2)
      .orElse("N/A");

    // Estado
    this.estadoReserva = entrada.getReserva().getEstadoReserva();
    this.fechaReserva = entrada.getReserva().getFechaReserva();

    // Datos del comprador
    this.nombreComprador = entrada.getReserva().getNombrePila() + " " +
      entrada.getReserva().getApellido1();
    this.emailComprador = entrada.getReserva().getEmail();
  }


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

  public String getDescripcionTipo() {
    return descripcionTipo;
  }

  public void setDescripcionTipo(String descripcionTipo) {
    this.descripcionTipo = descripcionTipo;
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

  public LocalDateTime getHoraFinSesion() {
    return horaFinSesion;
  }

  public void setHoraFinSesion(LocalDateTime horaFinSesion) {
    this.horaFinSesion = horaFinSesion;
  }

  public String getNombreExposicion() {
    return nombreExposicion;
  }

  public void setNombreExposicion(String nombreExposicion) {
    this.nombreExposicion = nombreExposicion;
  }

  public String getNombresSalas() {
    return nombresSalas;
  }

  public void setNombresSalas(String nombresSalas) {
    this.nombresSalas = nombresSalas;
  }

  public EstadoReserva getEstadoReserva() {
    return estadoReserva;
  }

  public void setEstadoReserva(EstadoReserva estadoReserva) {
    this.estadoReserva = estadoReserva;
  }

  public LocalDateTime getFechaReserva() {
    return fechaReserva;
  }

  public void setFechaReserva(LocalDateTime fechaReserva) {
    this.fechaReserva = fechaReserva;
  }

  public String getNombreComprador() {
    return nombreComprador;
  }

  public void setNombreComprador(String nombreComprador) {
    this.nombreComprador = nombreComprador;
  }

  public String getEmailComprador() {
    return emailComprador;
  }

  public void setEmailComprador(String emailComprador) {
    this.emailComprador = emailComprador;
  }
}
