package es.museum.asi.model.service.dto;


import es.museum.asi.model.domain.OrdenSalaSesion;

/**
 * DTO para representar una sala con su orden en una sesión
 */
public class SalaOrdenDTO {
  private Long idSala;
  private String nombre;
  private Integer planta;
  private Integer orden;

  public SalaOrdenDTO() {}

  public SalaOrdenDTO(OrdenSalaSesion ordenSalaSesion) {
    this.idSala = ordenSalaSesion.getSala().getIdSala();
    this.nombre = ordenSalaSesion.getSala().getNombre();
    this.planta = ordenSalaSesion.getSala().getPlanta();
    this.orden =  ordenSalaSesion.getOrden();
  }

  public Long getIdSala() {
    return idSala;
  }

  public void setIdSala(Long idSala) {
    this.idSala = idSala;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public Integer getPlanta() {
    return planta;
  }

  public void setPlanta(Integer planta) {
    this.planta = planta;
  }

  public Integer getOrden() {
    return orden;
  }

  public void setOrden(Integer orden) {
    this.orden = orden;
  }
}
