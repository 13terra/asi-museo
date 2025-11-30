package es.museum.asi.repository;

import java.util.Collection;

import es.museum.asi.model.User;

public interface UserDao {

  Collection<User> findAll();

  User findById(Long id);

  User findByLogin(String login);

  void create(User user);

  void update(User user);

  void delete(User user);

}
