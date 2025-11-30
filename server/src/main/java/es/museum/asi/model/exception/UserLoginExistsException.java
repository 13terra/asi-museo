package es.museum.asi.model.exception;

public class UserLoginExistsException extends ModelException {
  public UserLoginExistsException(String login) {
    super("User login " + login + " already exists");
  }
}
