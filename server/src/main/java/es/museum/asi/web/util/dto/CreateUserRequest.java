package es.museum.asi.web.util.dto;

import es.museum.asi.model.enums.UserAuthority;

/**
 * DTO para creación de usuario por ADMIN (HU5).
 */
public class CreateUserRequest {
  private String login;
  private String password;
  private UserAuthority autoridad;

  public CreateUserRequest() {
  }

  public String getLogin() { return login; }
  public void setLogin(String login) { this.login = login; }

  public String getPassword() { return password; }
  public void setPassword(String password) { this.password = password; }

  public UserAuthority getAutoridad() { return autoridad; }
  public void setAutoridad(UserAuthority autoridad) { this.autoridad = autoridad; }
}
