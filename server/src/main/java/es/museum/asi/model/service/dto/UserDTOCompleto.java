package es.museum.asi.model.service.dto;

import es.museum.asi.model.domain.User;
import es.museum.asi.model.enums.EstadoUser;
import es.museum.asi.model.enums.UserAuthority;

/**
 * DTO completo con toda la información de un usuario (para ADMIN)
 */
public class UserDTOCompleto {
  private Long idUser;
  private String login;
  private UserAuthority autoridad;
  private EstadoUser estado;
  private int numReservas;
  private int numExposicionesGestionadas;

  public UserDTOCompleto() {
  }

  public UserDTOCompleto(User user) {
    this.idUser = user.getIdUser();
    this.login = user.getLogin();
    this.autoridad = user.getAutoridad();
    this.estado = user.getEstado();
    this.numReservas = user.getReservas() != null ? user.getReservas().size() : 0;
    this.numExposicionesGestionadas = user.getGestiones() != null ? user.getGestiones().size() : 0;
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

  public int getNumReservas() {
    return numReservas;
  }

  public void setNumReservas(int numReservas) {
    this.numReservas = numReservas;
  }

  public int getNumExposicionesGestionadas() {
    return numExposicionesGestionadas;
  }

  public void setNumExposicionesGestionadas(int numExposicionesGestionadas) {
    this.numExposicionesGestionadas = numExposicionesGestionadas;
  }
}
