package es.museum.asi.model.service.dto;

import es.museum.asi.model.domain.PiezaExpuesta;

public class PiezaExpuestaDTO {
  private Long idPiezaExpuesta;
  private Integer orden;
  private String textoCuratorial;
  private Boolean portada;

  //Obra
  private Long idObra;
  private String tituloObra;
  private String autorObra;
  private String imagenObra;

  // Sala
  private Long idSala;
  private String nombreSala;

  private Long idEdicion;

  public PiezaExpuestaDTO() {}

  public PiezaExpuestaDTO(PiezaExpuesta pieza) {
    this.idPiezaExpuesta = pieza.getIdPiezaExpuesta();
    this.orden = pieza.getOrden();
    this.textoCuratorial = pieza.getTextoCuratorial();
    this.portada = pieza.getPortada();
    this.idObra = pieza.getObra().getIdObra();
    this.tituloObra = pieza.getObra().getTitulo();
    this.autorObra = pieza.getObra().getAutor();
    this.imagenObra = pieza.getObra().getImagen();
    this.idSala = pieza.getSala().getIdSala();
    this.nombreSala = pieza.getSala().getNombre();
    this.idEdicion = pieza.getEdicion().getIdEdicion();
  }

  public Long getIdPiezaExpuesta() {
    return idPiezaExpuesta;
  }

  public void setIdPiezaExpuesta(Long idPiezaExpuesta) {
    this.idPiezaExpuesta = idPiezaExpuesta;
  }

  public Integer getOrden() {
    return orden;
  }

  public void setOrden(Integer orden) {
    this.orden = orden;
  }

  public String getTextoCuratorial() {
    return textoCuratorial;
  }

  public void setTextoCuratorial(String textoCuratorial) {
    this.textoCuratorial = textoCuratorial;
  }

  public Boolean getPortada() {
    return portada;
  }

  public void setPortada(Boolean portada) {
    this.portada = portada;
  }

  public Long getIdObra() {
    return idObra;
  }

  public void setIdObra(Long idObra) {
    this.idObra = idObra;
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

  public Long getIdSala() {
    return idSala;
  }

  public void setIdSala(Long idSala) {
    this.idSala = idSala;
  }

  public String getNombreSala() {
    return nombreSala;
  }

  public void setNombreSala(String nombreSala) {
    this.nombreSala = nombreSala;
  }

  public Long getIdEdicion() {
    return idEdicion;
  }

  public void setIdEdicion(Long idEdicion) {
    this.idEdicion = idEdicion;
  }
}
