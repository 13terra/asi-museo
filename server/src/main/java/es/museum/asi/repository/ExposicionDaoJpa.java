package es.museum.asi.repository;

import es.museum.asi.model.domain.Exposicion;
import es.museum.asi.model.enums.EstadoExpo;
import es.museum.asi.repository.util.GenericDaoJpa;
import jakarta.persistence.TypedQuery;

import java.util.Collection;

public class ExposicionDaoJpa extends GenericDaoJpa implements ExposicionDao {

  @Override
  public Collection<Exposicion> findAll() {
    return entityManager.createQuery("SELECT e FROM Exposicion e", Exposicion.class).getResultList();
  }

  @Override
  public Exposicion findById(Long id) {
    return entityManager.find(Exposicion.class, id);
  }

  @Override
  public Collection<Exposicion> findByEstado(EstadoExpo estado) {
    TypedQuery<Exposicion> query = entityManager
      .createQuery("SELECT e FROM Exposicion e WHERE e.estadoExpo = :estado", Exposicion.class)
      .setParameter("estado", estado);
    return query.getResultList();
  }

  @Override
  public void create(Exposicion exposicion) {
    entityManager.persist(exposicion);
  }

  @Override
  public void update(Exposicion exposicion) {
    entityManager.merge(exposicion);
  }

  @Override
  public void delete(Exposicion exposicion) {
    entityManager.remove(exposicion);
  }
}
