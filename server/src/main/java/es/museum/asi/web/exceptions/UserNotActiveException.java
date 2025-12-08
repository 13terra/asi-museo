package es.museum.asi.web.exceptions;

import es.museum.asi.model.exception.ModelException;

/**
 * Se lanza cuando se intenta autenticar un usuario inactivo
 */
public class UserNotActiveException extends ModelException {
  public UserNotActiveException(String login) {
    super("El usuario " + login + " está inactivo y no puede acceder al sistema")
  }
}
