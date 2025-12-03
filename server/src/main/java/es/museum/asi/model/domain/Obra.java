package es.museum.asi.model.domain;

import es.museum.asi.model.enums.EstadoObra;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "obra")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Obra {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "obra_generator")
  @SequenceGenerator(name = "obra_generator", sequenceName = "obra_seq")
  private Long idObra;

  @Column(nullable = false)
  private String imagen;

  @Column(nullable = false)
  private String titulo;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private EstadoObra estado = EstadoObra.EN_ALMACEN;

  @Column(nullable = false)
  private String dimensiones;

  @Column(nullable = false)
  private String autor;

  @Column(nullable = false)
  private Integer anoCreacion; // capacidad de ser null

  @Column
  private Long idExterno; //no todas las obras vienen de The MET

  @Column(nullable = false)
  private String tecnica;

  @OneToMany(mappedBy = "obra")
  private List<PiezaExpuesta> piezasExpuestas = new ArrayList<>();


  public Long getIdObra() {
    return idObra;
  }

  public void setIdObra(Long idObra) {
    this.idObra = idObra;
  }

  public String getImagen() {
    return imagen;
  }

  public void setImagen(String imagen) {
    this.imagen = imagen;
  }

  public String getTitulo() {
    return titulo;
  }

  public void setTitulo(String titulo) {
    this.titulo = titulo;
  }

  public EstadoObra getEstado() {
    return estado;
  }

  public void setEstado(EstadoObra estado) {
    this.estado = estado;
  }

  public String getDimensiones() {
    return dimensiones;
  }

  public void setDimensiones(String dimensiones) {
    this.dimensiones = dimensiones;
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

  public Long getIdExterno() {
    return idExterno;
  }

  public void setIdExterno(Long idExterno) {
    this.idExterno = idExterno;
  }

  public String getTecnica() {
    return tecnica;
  }

  public void setTecnica(String tecnica) {
    this.tecnica = tecnica;
  }

  public List<PiezaExpuesta> getPiezasExpuestas() {
    return piezasExpuestas;
  }

  public void setPiezasExpuestas(List<PiezaExpuesta> piezasExpuestas) {
    this.piezasExpuestas = piezasExpuestas;
  }
}
