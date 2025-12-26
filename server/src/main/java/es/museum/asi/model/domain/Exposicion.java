package es.museum.asi.model.domain;

import es.museum.asi.model.enums.EstadoExpo;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "exposicion")
public class Exposicion {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "exposicion_generator")
  @SequenceGenerator(name = "exposicion_generator", sequenceName = "exposicion_seq")
  private Long idExposicion;

  @Column(nullable = false)
  private String titulo;

  @Column(length = 2000)
  private String descripcion;

  @Column
  private String portadaUrl;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EstadoExpo estadoExpo = EstadoExpo.BORRADOR;  //si se usa el constructor de lombok si no inicializamos sería nulo

  @OneToMany(mappedBy = "exposicion")
  private List<Edicion> ediciones =  new ArrayList<>(); // List: mantener orden cronológico + existir múltiples ediciones de la misma expo

  @OneToMany(mappedBy = "exposicion")
  private Set<Gestion> gestiones = new HashSet<>(); //Set evita duplicados y orden no importa

  // Constructor requerido por JPA
  protected Exposicion() {
  }

  public Exposicion(String titulo, String descripcion) {
    this.titulo = titulo;
    this.descripcion = descripcion;
  }

  public Long getIdExposicion() {
    return idExposicion;
  }

  public void setIdExposicion(Long idExposicion) {
    this.idExposicion = idExposicion;
  }

  public String getTitulo() {
    return titulo;
  }

  public void setTitulo(String titulo) {
    this.titulo = titulo;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getPortadaUrl() {
    return portadaUrl;
  }

  public void setPortadaUrl(String portadaUrl) {
    this.portadaUrl = portadaUrl;
  }

  public EstadoExpo getEstadoExpo() {
    return estadoExpo;
  }

  public void setEstadoExpo(EstadoExpo estadoExpo) {
    this.estadoExpo = estadoExpo;
  }

  public List<Edicion> getEdiciones() {
    return ediciones;
  }

  public void setEdiciones(List<Edicion> ediciones) {
    this.ediciones = ediciones;
  }

  public Set<Gestion> getGestiones() {
    return gestiones;
  }

  public void setGestiones(Set<Gestion> gestiones) {
    this.gestiones = gestiones;
  }
}
