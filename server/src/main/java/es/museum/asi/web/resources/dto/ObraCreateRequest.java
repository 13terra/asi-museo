package es.museum.asi.web.resources.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import es.museum.asi.model.enums.EstadoObra;

public class ObraCreateRequest {
  private String titulo;
  private String autor;
  @JsonAlias({"anioCreacion", "añoCreacion"})
  private Integer anioCreacion;
  private String tecnica;
  private String dimensiones;
  /** URL de imagen (por ejemplo, desde MET) */
  private String imagen;
  private Long idExterno;
  private EstadoObra estado;

  public String getTitulo() { return titulo; }
  public void setTitulo(String titulo) { this.titulo = titulo; }

  public String getAutor() { return autor; }
  public void setAutor(String autor) { this.autor = autor; }

  public Integer getAnioCreacion() { return anioCreacion; }
  public void setAnioCreacion(Integer anioCreacion) { this.anioCreacion = anioCreacion; }

  public String getTecnica() { return tecnica; }
  public void setTecnica(String tecnica) { this.tecnica = tecnica; }

  public String getDimensiones() { return dimensiones; }
  public void setDimensiones(String dimensiones) { this.dimensiones = dimensiones; }

  public String getImagen() { return imagen; }
  public void setImagen(String imagen) { this.imagen = imagen; }

  public Long getIdExterno() { return idExterno; }
  public void setIdExterno(Long idExterno) { this.idExterno = idExterno; }

  public EstadoObra getEstado() { return estado; }
  public void setEstado(EstadoObra estado) { this.estado = estado; }
}
