package es.museum.asi.repository;

import es.museum.asi.model.domain.TipoEntrada;
import es.museum.asi.repository.util.GenericDaoJpa;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class TipoEntradaDaoJpa extends GenericDaoJpa implements TipoEntradaDao {

  @Override
  public void create(TipoEntrada tipoEntrada) {
    entityManager.persist(tipoEntrada);
  }

  @Override
  public void update(TipoEntrada tipoEntrada) {
    entityManager.merge(tipoEntrada);
  }

  @Override
  public void delete(TipoEntrada tipoEntrada) {
    entityManager.remove(tipoEntrada);
  }

  @Override
  public Collection<TipoEntrada> findAll() {
      return entityManager.createQuery("Select te From TipoEntrada te Order By te.precio", TipoEntrada.class).getResultList();
  }

  @Override
  public TipoEntrada findById(Long idTipoEntrada) {
    return entityManager.find(TipoEntrada.class, idTipoEntrada);
  }

  @Override
  public TipoEntrada findByNombre(String nombre) {
    TypedQuery<TipoEntrada> query = entityManager
      .createQuery("Select te From TipoEntrada te Where te.nombre = :nombre", TipoEntrada.class)
      .setParameter("nombre", nombre);
    return  query.getSingleResult();
  }

}
