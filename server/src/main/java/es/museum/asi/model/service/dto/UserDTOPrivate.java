package es.museum.asi.model.service.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import es.museum.asi.model.domain.User;
import es.museum.asi.model.enums.EstadoUser;
import es.museum.asi.model.enums.UserAuthority;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class UserDTOPrivate {
  private Long id;

  @NotEmpty
  @Size(min = 4)
  private String login;

  @NotEmpty
  @JsonAlias({"contrasena", "password"})
  @Size(min = 4)
  private String password;

  private UserAuthority authority;

  public EstadoUser getEstadoUser() {
    return estadoUser;
  }

  public void setEstadoUser(EstadoUser estadoUser) {
    this.estadoUser = estadoUser;
  }

  private EstadoUser estadoUser;

  public UserDTOPrivate() {
  }

  public UserDTOPrivate(User user) {
    this.id = user.getIdUser();
    this.login = user.getLogin();
    // la contraseña no se rellena, nunca se envía al cliente
    this.authority = user.getAutoridad();
    this.estadoUser = user.getEstado();
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

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public UserAuthority getAuthority() {
    return authority;
  }

  public void setAuthority(UserAuthority authority) {
    this.authority = authority;
  }
}
