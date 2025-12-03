package es.museum.asi.repository;

import es.museum.asi.model.domain.User;

import java.util.Collection;

public interface UserDao {

  Collection<User> findAll();

  User findById(Long id);

  User findByLogin(String login);

  void create(User user);

  void update(User user);

  void delete(User user);
}
