package es.museum.asi.model.service.dto;

import es.museum.asi.model.domain.Gestion;
import es.museum.asi.model.enums.TipoPermiso;

/**
 * DTO para mostrar gestor con su permiso
 */

public class GestorPermisoDTO {
  private Long idUser;
  private String login;
  private TipoPermiso permiso;

  public GestorPermisoDTO() {}

  public GestorPermisoDTO(Gestion gestion) {
    this.idUser = gestion.getUser().getIdUser();
    this.login = gestion.getUser().getLogin();
    this.permiso = gestion.getPermiso();
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

  public TipoPermiso getPermiso() {
    return permiso;
  }

  public void setPermiso(TipoPermiso permiso) {
    this.permiso = permiso;
  }
}
