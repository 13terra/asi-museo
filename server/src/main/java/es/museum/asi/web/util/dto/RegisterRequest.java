package es.museum.asi.web.util.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;

public class RegisterRequest {
  @NotBlank(message = "El login es obligatorio")
  private String login;

  @NotBlank(message = "La contraseña es obligatoria")
  @JsonAlias({"contrasena", "password"})
  private String password;

  @NotBlank(message = "La confirmación de contraseña es obligatoria")
  @JsonAlias({"contrasenaConfirm", "passwordConfirm"})
  private String passwordConfirm;

  public RegisterRequest() {}

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

  public String getPasswordConfirm() {
    return passwordConfirm;
  }

  public void setPasswordConfirm(String passwordConfirm) {
    this.passwordConfirm = passwordConfirm;
  }
}
