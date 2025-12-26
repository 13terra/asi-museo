package es.museum.asi.web.util.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import es.museum.asi.model.enums.TipoPermiso;
import es.museum.asi.model.enums.UserAuthority;

/**
 * DTO para creación de usuario por ADMIN (HU5).
 */
public class CreateUserRequest {
  private String login;
  private String password;
  @JsonAlias({"authority", "role"})
  private UserAuthority autoridad;
  @JsonAlias({"permiso", "tipoGestor", "gestorRol"})
  private TipoPermiso permisoGestor;

  public CreateUserRequest() {
  }

  public String getLogin() { return login; }
  public void setLogin(String login) { this.login = login; }

  public String getPassword() { return password; }
  public void setPassword(String password) { this.password = password; }

  public UserAuthority getAutoridad() { return autoridad; }
  public void setAutoridad(UserAuthority autoridad) { this.autoridad = autoridad; }

  public TipoPermiso getPermisoGestor() { return permisoGestor; }
  public void setPermisoGestor(TipoPermiso permisoGestor) { this.permisoGestor = permisoGestor; }
}
