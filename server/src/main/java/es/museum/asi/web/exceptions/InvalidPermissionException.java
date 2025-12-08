package es.museum.asi.web.exceptions;

/**
 * Excepción lanzada cuando un usuario intenta una operación sin permisos
 */

public class InvalidPermissionException extends RuntimeException {
  public InvalidPermissionException(String operacion, String motivo) {
    super("No tiene permisos para " + operacion + ": " + motivo);
  }
}
