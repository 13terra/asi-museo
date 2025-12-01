package es.museum.asi.model.domain;

import es.museum.asi.model.enums.EstadoReserva;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reserva")
public class Reserva {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reserva_generator")
  @SequenceGenerator(name = "reserva_generator", sequenceName = "reserva_seq")
  private Long idReserva;

  @Column(nullable = false)
  private String dni;

  @Column(nullable = false)
  private String nombrePila;

  //supongo que habrá que poner un maxLength
  @Column(nullable = false)
  private String apellido1;

  @Column
  private String apellido2;

  @Column(nullable = false)
  private LocalDate fechaReserva;

  @Column(nullable = false)
  private String telefono;

  @Column(nullable = false)
  private String pais;

  @Column(nullable = false)
  private EstadoReserva estadoReserva;

  @Column
  private String email;

  @OneToMany(mappedBy = "reserva")
  private List<Entrada> entradas = new ArrayList<>();

  // que significa optional=false?
  @ManyToOne(fetch = FetchType.LAZY)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  private Sesion sesion;

  public Long getIdReserva() {
    return idReserva;
  }

  public void setIdReserva(Long idReserva) {
    this.idReserva = idReserva;
  }

  public String getDni() {
    return dni;
  }

  public void setDni(String dni) {
    this.dni = dni;
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

  public LocalDate getFechaReserva() {
    return fechaReserva;
  }

  public void setFechaReserva(LocalDate fechaReserva) {
    this.fechaReserva = fechaReserva;
  }

  public String getTelefono() {
    return telefono;
  }

  public void setTelefono(String telefono) {
    this.telefono = telefono;
  }

  public String getPais() {
    return pais;
  }

  public void setPais(String pais) {
    this.pais = pais;
  }

  public EstadoReserva getEstadoReserva() {
    return estadoReserva;
  }

  public void setEstadoReserva(EstadoReserva estadoReserva) {
    this.estadoReserva = estadoReserva;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public List<Entrada> getEntradas() {
    return entradas;
  }

  public void setEntradas(List<Entrada> entradas) {
    this.entradas = entradas;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Sesion getSesion() {
    return sesion;
  }

  public void setSesion(Sesion sesion) {
    this.sesion = sesion;
  }
}
