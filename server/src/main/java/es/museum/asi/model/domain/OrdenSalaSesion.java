package es.museum.asi.model.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orden_sala_sesion")
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class OrdenSalaSesion {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orden_sala_sesion_generator")
  @SequenceGenerator(name = "orden_sala_sesion_generator", sequenceName = "orden_sala_sesion_seq")
  private Long id;

  @Column(name = "orden")
  private Integer orden;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private Sesion sesion;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private Sala sala;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Integer getOrden() {
    return orden;
  }

  public void setOrden(Integer orden) {
    this.orden = orden;
  }

  public Sesion getSesion() {
    return sesion;
  }

  public void setSesion(Sesion sesion) {
    this.sesion = sesion;
  }

  public Sala getSala() {
    return sala;
  }

  public void setSala(Sala sala) {
    this.sala = sala;
  }
}
