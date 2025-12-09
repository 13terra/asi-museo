package es.museum.asi.web.exceptions;

import es.museum.asi.model.exception.ModelException;

/**
 * Excepción lanzada cuando un usuario intenta una operación sin permisos
 */

public class InvalidPermissionException extends ModelException {
  public InvalidPermissionException(String operacion, String motivo) {
    super("No tiene permisos para " + operacion + ": " + motivo);
  }
}
