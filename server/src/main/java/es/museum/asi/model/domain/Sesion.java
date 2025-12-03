package es.museum.asi.model.domain;

import es.museum.asi.model.enums.EstadoSesion;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sesion")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sesion {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sesion_generator")
  @SequenceGenerator(name = "sesion_generator", sequenceName = "sesion_seq")
  private Long idSesion;

  //AÑADIR CAMBIOS TIPO LISTA DE CAMBIOS + NOTIFICARSELO (TEMA HORAS)

  @Column(nullable = false)
  private LocalDateTime horaInicio;   // 2025-06-01 10:00:00

  @Column(nullable = false)
  private LocalDateTime horaFin;      // 2025-06-01 12:00:00

  @Column(nullable = false)
  private int aforo;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private EstadoSesion estadoSesion = EstadoSesion.DISPONIBLE;  // sino al crear una sesion con el construct lombok se le asigna null

  @OneToMany(mappedBy = "sesion")
  private List<OrdenSalaSesion> ordenes = new ArrayList<>();

  @OneToMany(mappedBy = "sesion")
  private List<Reserva> reservas = new ArrayList<>();

  @OneToMany(mappedBy = "sesion")
  private List<Entrada> entradas = new ArrayList<>();

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private Edicion edicion;

  public Long getIdSesion() {
    return idSesion;
  }

  public void setIdSesion(Long idSesion) {
    this.idSesion = idSesion;
  }

  public LocalDateTime getHoraInicio() {
    return horaInicio;
  }

  public void setHoraInicio(LocalDateTime horaInicio) {
    this.horaInicio = horaInicio;
  }

  public LocalDateTime getHoraFin() {
    return horaFin;
  }

  public void setHoraFin(LocalDateTime horaFin) {
    this.horaFin = horaFin;
  }

  public int getAforo() {
    return aforo;
  }

  public void setAforo(int aforo) {
    this.aforo = aforo;
  }

  public EstadoSesion getEstadoSesion() {
    return estadoSesion;
  }

  public void setEstadoSesion(EstadoSesion estadoSesion) {
    this.estadoSesion = estadoSesion;
  }

  public List<OrdenSalaSesion> getOrdenes() {
    return ordenes;
  }

  public void setOrdenes(List<OrdenSalaSesion> ordenes) {
    this.ordenes = ordenes;
  }

  public List<Reserva> getReservas() {
    return reservas;
  }

  public void setReservas(List<Reserva> reservas) {
    this.reservas = reservas;
  }

  public List<Entrada> getEntradas() {
    return entradas;
  }

  public void setEntradas(List<Entrada> entradas) {
    this.entradas = entradas;
  }

  public Edicion getEdicion() {
    return edicion;
  }

  public void setEdicion(Edicion edicion) {
    this.edicion = edicion;
  }
}
