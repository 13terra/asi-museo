package es.museum.asi.repository;

import es.museum.asi.model.domain.Edicion;
import es.museum.asi.repository.util.GenericDaoJpa;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class EdicionDaoJpa extends GenericDaoJpa implements EdicionDao {


  @Override
  public void create(Edicion edicion) {
    entityManager.persist(edicion);
  }
  @Override
  public void update(Edicion edicion) {
    entityManager.merge(edicion);
  }

  @Override
  public void delete(Edicion edicion){
    entityManager.remove(edicion);
  }

  @Override
  public Edicion findById(Long id) {
    return entityManager.find(Edicion.class, id);
  }

  @Override
  public Collection<Edicion> findAll() {
    return entityManager.createQuery("SELECT e FROM Edicion e", Edicion.class)
      .getResultList();
  }

  @Override
  public Collection<Edicion> findByExposicion(Long idExposicion) {
    TypedQuery<Edicion> query = entityManager
      .createQuery("SELECT e FROM Edicion e WHERE e.exposicion. idExposicion = :idExposicion", Edicion.class)
      .setParameter("idExposicion", idExposicion);
    return query.getResultList();
  }



}
