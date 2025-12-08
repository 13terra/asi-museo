package es.museum.asi.web.exceptions;

import es.museum.asi.model.exception.ModelException;

public class AforoCompletoException extends ModelException {
  public AforoCompletoException(Long idSesion) {
    super("La sesión " + idSesion + " no tiene aforo disponible");
  }
}
