package es.museum.asi.model.service.dto;

/**
 * DTO para recibir obras simplificadas del microservicio MET (HU48)
 * Usado en el listado de búsqueda
 */
public class ObraMetSimplificadaDTO {
  private Long idExterno;
  private String titulo;
  private String autor;
  private String imagen;

  public ObraMetSimplificadaDTO(){}

  public ObraMetSimplificadaDTO(Long idExterno, String titulo, String autor, String imagen) {
    this.idExterno = idExterno;
    this.titulo = titulo;
    this.autor = autor;
    this.imagen = imagen;
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

  public String getImagen() {
    return imagen;
  }

  public void setImagen(String imagen) {
    this.imagen = imagen;
  }
}
