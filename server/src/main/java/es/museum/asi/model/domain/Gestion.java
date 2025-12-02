package es.museum.asi.model.domain;


import es.museum.asi.model.enums.TipoPermiso;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "gestion")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Gestion {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gestion_generator")
  @SequenceGenerator(name = "gestion_generator", sequenceName = "gestion_seq")
  private Long idGestion;

  @Column(nullable = false)
  private Long idUser;

  @Column(nullable = false)
  private Long idExposicion;

  @Column(nullable = false)
  private TipoPermiso permiso;

  @ManyToOne(fetch = FetchType.LAZY)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  private Exposicion exposicion;

  public Long getIdGestion() {
    return idGestion;
  }

  public void setIdGestion(Long idGestion) {
    this.idGestion = idGestion;
  }

  public Long getIdUser() {
    return idUser;
  }

  public void setIdUser(Long idUser) {
    this.idUser = idUser;
  }

  public Long getIdExposicion() {
    return idExposicion;
  }

  public void setIdExposicion(Long idExposicion) {
    this.idExposicion = idExposicion;
  }

  public TipoPermiso getPermiso() {
    return permiso;
  }

  public void setPermiso(TipoPermiso permiso) {
    this.permiso = permiso;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Exposicion getExposicion() {
    return exposicion;
  }

  public void setExposicion(Exposicion exposicion) {
    this.exposicion = exposicion;
  }
}
