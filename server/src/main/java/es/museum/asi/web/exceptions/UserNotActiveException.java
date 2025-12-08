package es.museum.asi.web.exceptions;

import org.springframework.security.core.AuthenticationException;

/**
 * Se lanza cuando se intenta autenticar un usuario inactivo
 */
public class UserNotActiveException extends AuthenticationException {
  public UserNotActiveException(String msg) {
    super(msg);
  }
}
