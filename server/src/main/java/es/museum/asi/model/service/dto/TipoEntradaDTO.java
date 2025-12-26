package es.museum.asi.model.service.dto;

import es.museum.asi.model.domain.TipoEntrada;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

public class TipoEntradaDTO {
  private Long idTipoEntrada;

  @NotEmpty
  private String nombre;

  @NotEmpty
  @Positive
  private Float precio;

  private String descripcion;

  public TipoEntradaDTO() {

  }

  public TipoEntradaDTO(TipoEntrada tipoEntrada) {
    this.idTipoEntrada = tipoEntrada.getIdTipoEntrada();
    this.nombre = tipoEntrada.getNombre();
    this.precio = tipoEntrada.getPrecio();
    this.descripcion = tipoEntrada.getDescripcion();
  }

  public Long getIdTipoEntrada() {
    return idTipoEntrada;
  }

  public void setIdTipoEntrada(Long idTipoEntrada) {
    this.idTipoEntrada = idTipoEntrada;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public Float getPrecio() {
    return precio;
  }

  public void setPrecio(Float precio) {
    this.precio = precio;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }
}
