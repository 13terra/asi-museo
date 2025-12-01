package es.museum.asi.model.domain;

import es.museum.asi.model.enums.EstadoEdicion;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "edicion")
public class Edicion {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "edicion_generator")
  @SequenceGenerator(name = "edicion_generator", sequenceName = "edicion_seq")
  private Long idEdicion;

  @Column(nullable = false)
  private LocalDate fechaInicio;

  @Column(nullable = false)
  private LocalDate fechaFin;

  private EstadoEdicion estado = EstadoEdicion.BORRADOR;

  // optional? supongo que false
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private Exposicion exposicion;

  @OneToMany(mappedBy = "edicion")
  private List<Sesion> sesiones =  new ArrayList<>(); // List: mantener orden cronológico + existir múltiples ediciones de la misma expo

  @OneToMany(mappedBy = "edicion")
  private List<PiezaExpuesta> piezasExpuestas = new ArrayList<>();

  public Edicion() {
    super();
  }

  public Long getIdEdicion() {
    return idEdicion;
  }

  public void setIdEdicion(Long idEdicion) {
    this.idEdicion = idEdicion;
  }

  public LocalDate getFechaInicio() {
    return fechaInicio;
  }

  public void setFechaInicio(LocalDate fechaInicio) {
    this.fechaInicio = fechaInicio;
  }

  public LocalDate getFechaFin() {
    return fechaFin;
  }

  public void setFechaFin(LocalDate fechaFin) {
    this.fechaFin = fechaFin;
  }

  public EstadoEdicion getEstado() {
    return estado;
  }

  public void setEstado(EstadoEdicion estado) {
    this.estado = estado;
  }

  public Exposicion getExposicion() {
    return exposicion;
  }

  public void setExposicion(Exposicion exposicion) {
    this.exposicion = exposicion;
  }

  public List<Sesion> getSesiones() {
    return sesiones;
  }

  public void setSesiones(List<Sesion> sesiones) {
    this.sesiones = sesiones;
  }

  public List<PiezaExpuesta> getPiezasExpuestas() {
    return piezasExpuestas;
  }

  public void setPiezasExpuestas(List<PiezaExpuesta> piezasExpuestas) {
    this.piezasExpuestas = piezasExpuestas;
  }
}
