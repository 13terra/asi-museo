package es.museum.asi.model.service.dto;

import es.museum.asi.model.domain.User;
import es.museum.asi.model.enums.EstadoUser;
import es.museum.asi.model.enums.TipoPermiso;
import es.museum.asi.model.enums.UserAuthority;

public class UserDTOPublic {
  private Long id;
  private String login;
  private UserAuthority autoridad;
  private EstadoUser estado;
  private TipoPermiso permisoGestor;

  public UserDTOPublic() {
  }

  public UserDTOPublic(User user) {
    this.id = user.getIdUser();
    this.login = user.getLogin();
    this.autoridad = user.getAutoridad();
    this.estado = user.getEstado();
    this.permisoGestor = user.getPermisoGestor();
  }


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
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

  public TipoPermiso getPermisoGestor() {
    return permisoGestor;
  }

  public void setPermisoGestor(TipoPermiso permisoGestor) {
    this.permisoGestor = permisoGestor;
  }
}
