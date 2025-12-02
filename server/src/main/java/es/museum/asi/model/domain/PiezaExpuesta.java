package es.museum.asi.model.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pieza_expuesta")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PiezaExpuesta {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pieza_expuesta_generator")
  @SequenceGenerator(name = "pieza_expuesta_generator", sequenceName = "pieza_expuesta_seq")
  private Long idPiezaExpuesta;

  @Column(nullable = false)
  private String textoCuratorial;

  @Column(nullable = false)
  private String orden;

  @Column(nullable = false)
  private Boolean portada;

  @ManyToOne(fetch = FetchType.LAZY)
  private Obra obra;

  @ManyToOne(fetch = FetchType.LAZY)
  private Sala sala;

  @ManyToOne(fetch = FetchType.LAZY)
  private Edicion edicion;


  public Long getIdPiezaExpuesta() {
    return idPiezaExpuesta;
  }

  public void setIdPiezaExpuesta(Long idPiezaExpuesta) {
    this.idPiezaExpuesta = idPiezaExpuesta;
  }

  public String getTextoCuratorial() {
    return textoCuratorial;
  }

  public void setTextoCuratorial(String textoCuratorial) {
    this.textoCuratorial = textoCuratorial;
  }

  public String getOrden() {
    return orden;
  }

  public void setOrden(String orden) {
    this.orden = orden;
  }

  public Boolean getPortada() {
    return portada;
  }

  public void setPortada(Boolean portada) {
    this.portada = portada;
  }

  public Obra getObra() {
    return obra;
  }

  public void setObra(Obra obra) {
    this.obra = obra;
  }

  public Sala getSala() {
    return sala;
  }

  public void setSala(Sala sala) {
    this.sala = sala;
  }

  public Edicion getEdicion() {
    return edicion;
  }

  public void setEdicion(Edicion edicion) {
    this.edicion = edicion;
  }
}
