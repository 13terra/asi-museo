package es.museum.asi.model.domain;

import es.museum.asi.model.enums.EstadoSesion;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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


  @Column(nullable = false)
  private LocalDate horaInicio;

  @Column(nullable = false)
  private LocalDate horaFin;

  @Column(nullable = false)
  private int aforo;

  @Column(nullable = false)
  private EstadoSesion estadoSesion;

  @OneToMany(mappedBy = "sesion")
  private List<OrdenSalaSesion> ordenes = new ArrayList<>();

  @OneToMany(mappedBy = "sesion")
  private List<Reserva> reservas = new ArrayList<>();

  @OneToMany(mappedBy = "sesion")
  private List<Entrada> entradas = new ArrayList<>();

  @ManyToOne(fetch = FetchType.LAZY)
  private Edicion edicion;

  public Sesion() {}




}
