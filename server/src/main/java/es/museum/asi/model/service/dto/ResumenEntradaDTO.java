package es.museum.asi.model.service.dto;

import es.museum.asi.model.domain.Entrada;


/**
 * DTO resumido de entrada (para listados dentro de reservas)
 */
public class ResumenEntradaDTO {
  private Long idEntrada;
  private String nombreCompletoAsistente;
  private String dni;
  private String tipoEntrada;
  private Float precio;

  public ResumenEntradaDTO() {
  }

  public ResumenEntradaDTO(Entrada entrada) {
    this.idEntrada = entrada.getIdEntrada();
    this.nombreCompletoAsistente = entrada.getNombrePila() + " " + entrada.getApellido1();
    if (entrada.getApellido2() != null && !entrada.getApellido2().isEmpty()) {
      this.nombreCompletoAsistente += " " + entrada.getApellido2();
    }
    this.dni = entrada.getDni();
    this.tipoEntrada = entrada.getTipoEntrada().getNombre();
    this.precio = entrada.getPrecio();
  }

  public Long getIdEntrada() {
    return idEntrada;
  }

  public void setIdEntrada(Long idEntrada) {
    this.idEntrada = idEntrada;
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
}

