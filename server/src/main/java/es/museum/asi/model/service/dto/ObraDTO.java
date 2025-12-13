package es.museum.asi.model.service.dto;

import es.museum.asi.model.domain.Obra;
import es.museum.asi.model.enums.EstadoObra;

/**
 * DTO de obra para listados (HU44)
 */
public class ObraDTO {
  private Long idObra;
  private String titulo;
  private String autor;
  private Integer anoCreacion;
  private String tecnica;
  private String dimensiones;
  private String imagen;
  private EstadoObra estado;
  private Long idExterno; // ID de The MET (si aplica)

  public ObraDTO() {}

  public ObraDTO(Obra obra) {
    this.idObra = obra.getIdObra();
    this.titulo = obra. getTitulo();
    this.autor = obra.getAutor();
    this.anoCreacion = obra.getAnoCreacion();
    this.tecnica = obra.getTecnica();
    this.dimensiones = obra.getDimensiones();
    this.imagen = obra.getImagen();
    this.estado = obra.getEstado();
    this.idExterno = obra.getIdExterno();
  }

  public Long getIdObra() {
    return idObra;
  }

  public void setIdObra(Long idObra) {
    this.idObra = idObra;
  }

  public String getTitulo() {
    return titulo;
  }

  public void setTitulo(String titulo) {
    this.titulo = titulo;
  }

  public String getAutor() {
    return autor;
  }

  public void setAutor(String autor) {
    this.autor = autor;
  }

  public Integer getAnoCreacion() {
    return anoCreacion;
  }

  public void setAnoCreacion(Integer anoCreacion) {
    this.anoCreacion = anoCreacion;
  }

  public String getTecnica() {
    return tecnica;
  }

  public void setTecnica(String tecnica) {
    this.tecnica = tecnica;
  }

  public String getDimensiones() {
    return dimensiones;
  }

  public void setDimensiones(String dimensiones) {
    this.dimensiones = dimensiones;
  }

  public String getImagen() {
    return imagen;
  }

  public void setImagen(String imagen) {
    this.imagen = imagen;
  }

  public EstadoObra getEstado() {
    return estado;
  }

  public void setEstado(EstadoObra estado) {
    this.estado = estado;
  }

  public Long getIdExterno() {
    return idExterno;
  }

  public void setIdExterno(Long idExterno) {
    this.idExterno = idExterno;
  }
}
