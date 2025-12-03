package es.museum.asi.repository;

import es.museum.asi.model.domain.User;
import es.museum.asi.repository.util.GenericDaoJpa;
import jakarta.persistence.TypedQuery;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class UserDaoJpa extends GenericDaoJpa implements UserDao {

  @Override
  public Collection<User> findAll() {
    return entityManager.createQuery("select u from User u", User.class).getResultList();
  }

  @Override
  public User findById(Long id) {
    return entityManager.find(User.class, id);
  }

  // hecho igual que en el notebook
  @Override
  public User findByLogin(String login) {
    TypedQuery<User> query = entityManager.createQuery("select u from User u where u.login = :login", User.class)
      .setParameter("login", login);
    return DataAccessUtils.singleResult(query.getResultList()); //maneja 0 o 1 resultado (null si no hay)
  }

  @Override
  public void create(User user) {
    entityManager.persist(user);
  }

  @Override
  public void update(User user) {
    entityManager.merge(user);
  }

  @Override
  public void delete(User user) {
    entityManager.remove(user);
  }

}
