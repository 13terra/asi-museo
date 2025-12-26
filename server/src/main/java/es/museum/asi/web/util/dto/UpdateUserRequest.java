package es.museum.asi.web.util.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import es.museum.asi.model.enums.EstadoUser;
import es.museum.asi.model.enums.TipoPermiso;
import es.museum.asi.model.enums.UserAuthority;

/**
 * DTO para actualización de usuario (HU6/HU8).
 */
public class UpdateUserRequest {
  private String login;
  private String password;
  @JsonAlias({"authority", "role"})
  private UserAuthority autoridad;
  private EstadoUser estado;
  @JsonAlias({"permiso", "tipoGestor", "gestorRol"})
  private TipoPermiso permisoGestor;

  public UpdateUserRequest() {
  }

  public String getLogin() { return login; }
  public void setLogin(String login) { this.login = login; }

  public String getPassword() { return password; }
  public void setPassword(String password) { this.password = password; }

  public UserAuthority getAutoridad() { return autoridad; }
  public void setAutoridad(UserAuthority autoridad) { this.autoridad = autoridad; }

  public EstadoUser getEstado() { return estado; }
  public void setEstado(EstadoUser estado) { this.estado = estado; }

  public TipoPermiso getPermisoGestor() { return permisoGestor; }
  public void setPermisoGestor(TipoPermiso permisoGestor) { this.permisoGestor = permisoGestor; }
}
