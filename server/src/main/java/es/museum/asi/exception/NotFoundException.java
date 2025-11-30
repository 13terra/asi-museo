package es.museum.asi.exception;

public class NotFoundException extends ModelException {
  public NotFoundException(String id, Class<?> clazz) {
    super("Not found " + clazz.getSimpleName() + " with id " + id);
  }
}
