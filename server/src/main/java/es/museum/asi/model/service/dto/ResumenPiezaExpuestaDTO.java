package es.museum.asi.model.service.dto;

import es.museum.asi.model.domain.PiezaExpuesta;

public class ResumenPiezaExpuestaDTO {
  private Long idPiezaExpuesta;
  private String tituloObra;
  private String autorObra;
  private String imagenObra;
  private String nombreSala;
  private Integer orden;
  private Boolean portada;

  public ResumenPiezaExpuestaDTO() {}

  public ResumenPiezaExpuestaDTO(PiezaExpuesta piezaExpuesta) {
    this.idPiezaExpuesta =  piezaExpuesta.getIdPiezaExpuesta();
    this.tituloObra = piezaExpuesta.getObra().getTitulo();
    this.autorObra = piezaExpuesta.getObra().getAutor();
    this.imagenObra = piezaExpuesta.getObra().getImagen();
    this.nombreSala = piezaExpuesta.getSala().getNombre();
    this.orden = piezaExpuesta.getOrden();
    this.portada = piezaExpuesta.getPortada();
  }


  public Long getIdPiezaExpuesta() {
    return idPiezaExpuesta;
  }

  public void setIdPiezaExpuesta(Long idPiezaExpuesta) {
    this.idPiezaExpuesta = idPiezaExpuesta;
  }

  public String getTituloObra() {
    return tituloObra;
  }

  public void setTituloObra(String tituloObra) {
    this.tituloObra = tituloObra;
  }

  public String getAutorObra() {
    return autorObra;
  }

  public void setAutorObra(String autorObra) {
    this.autorObra = autorObra;
  }

  public String getImagenObra() {
    return imagenObra;
  }

  public void setImagenObra(String imagenObra) {
    this.imagenObra = imagenObra;
  }

  public String getNombreSala() {
    return nombreSala;
  }

  public void setNombreSala(String nombreSala) {
    this.nombreSala = nombreSala;
  }

  public Integer getOrden() {
    return orden;
  }

  public void setOrden(Integer orden) {
    this.orden = orden;
  }

  public Boolean getPortada() {
    return portada;
  }

  public void setPortada(Boolean portada) {
    this.portada = portada;
  }
}
