package es.udc.asi.museo_rest.exception;

public class OperationNotAllowed extends ModelException {

  public OperationNotAllowed(String msg) {
    super("Operation not allowed: " + msg);
  }
}
