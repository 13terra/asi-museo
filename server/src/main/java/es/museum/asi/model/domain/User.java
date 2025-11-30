package es.museum.asi.model.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.museum.asi.model.enums.EstadoUser;
import es.museum.asi.model.enums.UserAuthority;
import jakarta.persistence.*;

@Entity
@Table(name = "theUser")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_generator")
  @SequenceGenerator(name = "user_generator", sequenceName = "user_seq")
  private Long idUser;

  @Column(unique = true, nullable = false)
  private String login;

  @Column(nullable = false)
  private String contrasena;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UserAuthority autoridad;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EstadoUser estado = EstadoUser.ACTIVO;

  @OneToMany(mappedBy = "user") //la relacion está mapeada por 'user' en la clase Reserva
  private List<Reserva> reservas = new ArrayList<>();

  @OneToMany(mappedBy = "user") //la relacion está mapeada por 'user' en la clase Reserva
  private Set<Gestion> gestiones = new HashSet<>();

  public User() {
  }

  public User(String login, String contrasena, UserAuthority autoridad) {
    this.login = login;
    this.contrasena = contrasena;
    this.autoridad = autoridad;
  }


  public Long getIdUser() {
    return idUser;
  }

  public void setIdUser(Long idUser) {
    this.idUser = idUser;
  }

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public String getContrasena() {
    return contrasena;
  }

  public void setContrasena(String contrasena) {
    this.contrasena = contrasena;
  }

  public UserAuthority getAutoridad() {
    return autoridad;
  }

  public void setAutoridad(UserAuthority autoridad) {
    this.autoridad = autoridad;
  }

  public EstadoUser getEstado() {
    return estado;
  }

  public void setEstado(EstadoUser estado) {
    this.estado = estado;
  }

  public List<Reserva> getReservas() {
    return reservas;
  }

  public void setReservas(List<Reserva> reservas) {
    this.reservas = reservas;
  }

  public Set<Gestion> getGestiones() {
    return gestiones;
  }

  public void setGestiones(Set<Gestion> gestiones) {
    this.gestiones = gestiones;
  }
}
