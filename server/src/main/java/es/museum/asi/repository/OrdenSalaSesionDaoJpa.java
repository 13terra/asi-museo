package es.museum.asi.repository;

import es.museum.asi.model.domain.OrdenSalaSesion;
import es.museum.asi.repository.util.GenericDaoJpa;
import jakarta.persistence.TypedQuery;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class OrdenSalaSesionDaoJpa extends GenericDaoJpa implements OrdenSalaSesionDao {

  @Override
  public void create(OrdenSalaSesion ordenSalaSesion) {
    entityManager.persist(ordenSalaSesion);
  }

  @Override
  public void update(OrdenSalaSesion ordenSalaSesion) {
    entityManager.merge(ordenSalaSesion);
  }

  @Override
  public void delete(OrdenSalaSesion ordenSalaSesion) {
    entityManager.remove(ordenSalaSesion);
  }

  @Override
  public Collection<OrdenSalaSesion> findAll() {
    return entityManager.createQuery("from OrdenSalaSesion", OrdenSalaSesion.class).getResultList();
  }

  @Override
  public OrdenSalaSesion findById(Long id) {
    return entityManager.find(OrdenSalaSesion.class, id);
  }

  @Override
  public OrdenSalaSesion findBySesionAndSala(Long idSesion, Long idSala) {
    TypedQuery<OrdenSalaSesion> query = entityManager
      .createQuery("Select o From OrdenSalaSesion o Where o.sesion.idSesion = :idSesion And o.sala.idSala = :idSala", OrdenSalaSesion.class)
      .setParameter("idSesion", idSesion)
      .setParameter("idSala", idSala);
    return DataAccessUtils.singleResult(query.getResultList());
  }

  @Override
  public Collection<OrdenSalaSesion> findBySala(Long idSala) {
    TypedQuery<OrdenSalaSesion> query = entityManager
      .createQuery("Select o From OrdenSalaSesion o Where o.sala.idSala = :idSala", OrdenSalaSesion.class)
      .setParameter("idSala", idSala);
    return query.getResultList();
  }

  @Override
  public Collection<OrdenSalaSesion> findBySesion(Long idSesion) {
    TypedQuery<OrdenSalaSesion> query = entityManager
      .createQuery("Select o From OrdenSalaSesion o Where o.sesion.idSesion = :idSesion Order By o.orden", OrdenSalaSesion.class)
      .setParameter("idSesion", idSesion);
    return query.getResultList();
  }

}
