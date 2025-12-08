package es.museum.asi.model.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tipo_entrada")
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class TipoEntrada {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tipoEntrada_generator")
  @SequenceGenerator(name = "tipoEntrada_generator", sequenceName = "tipoEntrada_seq")
  private Long idTipoEntrada;

  @Column(nullable = false)
  private String nombre;

  @Column(nullable = false)
  private Float precio;

  @Column
  private String descripcion;

  @OneToMany(mappedBy = "tipoEntrada")
  private List<Entrada> entradas = new ArrayList<>();


  public Long getIdTipoEntrada() {
    return idTipoEntrada;
  }

  public void setIdTipoEntrada(Long idTipoEntrada) {
    this.idTipoEntrada = idTipoEntrada;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public Float getPrecio() {
    return precio;
  }

  public void setPrecio(Float precio) {
    this.precio = precio;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public List<Entrada> getEntradas() {
    return entradas;
  }

  public void setEntradas(List<Entrada> entradas) {
    this.entradas = entradas;
  }
}
