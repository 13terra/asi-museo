package es.museum.asi.model.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "entrada")
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Entrada {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "entrada_generator")
  @SequenceGenerator(name = "entrada_generator", sequenceName = "entrada_seq")
  private Long idEntrada;

  @Column(nullable = false)
  private String nombrePila;

  @Column(nullable = false)
  private String apellido1;

  @Column
  private String apellido2;

  @Column(nullable = false)
  private String dni;

  @Column(nullable = false)
  private Float precio;

  //tiene que haber tipoEntrada sí o sí --> por eso optional = false
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private TipoEntrada tipoEntrada;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private Reserva reserva;

  public Float getPrecio() {
    return precio;
  }

  public void setPrecio(Float precio) {
    this.precio = precio;
  }
  public Long getIdEntrada() {
    return idEntrada;
  }

  public void setIdEntrada(Long idEntrada) {
    this.idEntrada = idEntrada;
  }

  public String getNombrePila() {
    return nombrePila;
  }

  public void setNombrePila(String nombrePila) {
    this.nombrePila = nombrePila;
  }

  public String getApellido1() {
    return apellido1;
  }

  public void setApellido1(String apellido1) {
    this.apellido1 = apellido1;
  }

  public String getApellido2() {
    return apellido2;
  }

  public void setApellido2(String apellido2) {
    this.apellido2 = apellido2;
  }

  public String getDni() {
    return dni;
  }

  public void setDni(String dni) {
    this.dni = dni;
  }

  public TipoEntrada getTipoEntrada() {
    return tipoEntrada;
  }

  public void setTipoEntrada(TipoEntrada tipoEntrada) {
    this.tipoEntrada = tipoEntrada;
  }

  public Reserva getReserva() {
    return reserva;
  }

  public void setReserva(Reserva reserva) {
    this.reserva = reserva;
  }

}
