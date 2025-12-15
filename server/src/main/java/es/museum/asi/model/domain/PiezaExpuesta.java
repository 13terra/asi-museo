package es.museum.asi.model.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pieza_expuesta")
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class PiezaExpuesta {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pieza_expuesta_generator")
  @SequenceGenerator(name = "pieza_expuesta_generator", sequenceName = "pieza_expuesta_seq")
  private Long idPiezaExpuesta;

  @Column(length = 2000)
  private String textoCuratorial;

  @Column(nullable = false)
  private Integer orden;

  @Column(nullable = false)
  private Boolean portada = false;  //se le da este valor por defecto, y luego se escoge una

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private Obra obra;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private Sala sala;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
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
