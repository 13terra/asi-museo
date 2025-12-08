package es.museum.asi.web.exceptions;

import es.museum.asi.model.exception.ModelException;

public class ReservaNotCancellableException extends ModelException {
  public ReservaNotCancellableException(String motivo) {
    super("No se puede cancelar la reserva: " + motivo);
  }}
