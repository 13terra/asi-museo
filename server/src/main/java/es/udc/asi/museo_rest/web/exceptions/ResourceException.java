package es.udc.asi.museo_rest.web.exceptions;

public class ResourceException extends Exception {
  public ResourceException(String errorMsg) {
    super(errorMsg);
  }
}
