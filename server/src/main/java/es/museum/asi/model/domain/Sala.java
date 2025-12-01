package es.museum.asi.model.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sala")
public class Sala {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sala_generator")
  @SequenceGenerator(name = "sala_generator", sequenceName = "sala_seq")
  private Long idSala;

  @Column(nullable = false)
  private String nombre;

  @Column
  private int planta;

  @OneToMany(mappedBy = "sala")
  private List<PiezaExpuesta> piezasExpuestas = new ArrayList<>();

  @OneToMany(mappedBy = "sala")
  private List<OrdenSalaSesion> ordenes = new ArrayList<>();

  public Long getIdSala() {
    return idSala;
  }

  public void setIdSala(Long idSala) {
    this.idSala = idSala;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public int getPlanta() {
    return planta;
  }

  public void setPlanta(int planta) {
    this.planta = planta;
  }

  public List<PiezaExpuesta> getPiezasExpuestas() {
    return piezasExpuestas;
  }

  public void setPiezasExpuestas(List<PiezaExpuesta> piezasExpuestas) {
    this.piezasExpuestas = piezasExpuestas;
  }

  public List<OrdenSalaSesion> getOrdenes() {
    return ordenes;
  }

  public void setOrdenes(List<OrdenSalaSesion> ordenes) {
    this.ordenes = ordenes;
  }
}
