package es.museum.asi.model.repository;

import java.util.Collection;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import es.museum.asi.model.domain.User;
import es.museum.asi.model.repository.util.GenericDaoJpa;
import jakarta.persistence.TypedQuery;

@Repository
public class UserDaoJpa extends GenericDaoJpa implements UserDao {

  @Override
  public Collection<User> findAll() {
    return entityManager.createQuery("from User", User.class).getResultList();
  }

  @Override
  public User findById(Long id) {
    return entityManager.find(User.class, id);
  }

  @Override
  public User findByLogin(String login) {
    TypedQuery<User> query = entityManager.createQuery("from User u where u.login = :login", User.class)
        .setParameter("login", login);
    return DataAccessUtils.singleResult(query.getResultList());
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
