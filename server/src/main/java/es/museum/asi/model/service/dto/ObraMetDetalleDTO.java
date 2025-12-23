package es.museum.asi.model.service.dto;

/**
 * DTO para recibir el detalle completo de una obra
 * desde el microservicio MET
 * Usado para precargar formulario creación
 */
public class ObraMetDetalleDTO {
  private Long idExterno;
  private String titulo;
  private String autor;
  private Integer anoCreacion;
  private String dimensiones;
  private String imagen;
  private String tecnica;

  // Constructor vacío
  public ObraMetDetalleDTO() {
  }

  public ObraMetDetalleDTO(Long idExterno, String titulo, String autor, Integer anoCreacion,
                           String dimensiones, String imagen, String tecnica) {
    this.idExterno = idExterno;
    this.titulo = titulo;
    this.autor = autor;
    this.anoCreacion = anoCreacion;
    this.dimensiones = dimensiones;
    this.imagen = imagen;
    this.tecnica = tecnica;
  }


  public Long getIdExterno() {
    return idExterno;
  }

  public void setIdExterno(Long idExterno) {
    this.idExterno = idExterno;
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

  public String getTecnica() {
    return tecnica;
  }

  public void setTecnica(String tecnica) {
    this.tecnica = tecnica;
  }
}
