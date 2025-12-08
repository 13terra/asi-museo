package es.museum.asi.repository;

import es.museum.asi.model.domain.Obra;
import es.museum.asi.model.domain.Sala;
import es.museum.asi.repository.util.GenericDaoJpa;
import jakarta.persistence.TypedQuery;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class SalaDaoJpa extends GenericDaoJpa implements SalaDao {

  @Override
  public void create(Sala sala) {
    entityManager.persist(sala);
  }

  @Override
  public void update(Sala sala) {
    entityManager.merge(sala);
  }

  @Override
  public void delete(Sala sala) {
    entityManager.remove(sala);
  }

  @Override
  public Sala findById(Long idSala) {
    return entityManager.find(Sala.class, idSala);
  }

  @Override
  public Collection<Sala> findAll() {
    return entityManager.createQuery("Select s from Sala s Order By s.planta, s.nombre", Sala.class).getResultList();
  }

  @Override
  public Sala findByNombre(String nombre) {
    TypedQuery<Sala> query = entityManager
      .createQuery("Select s from Sala s where s.nombre = :nombre", Sala.class)
      .setParameter("nombre", nombre);
    return DataAccessUtils.singleResult(query.getResultList());
  }

  @Override
  public Collection<Sala> findByPlanta(Integer planta) {
    TypedQuery<Sala> query = entityManager
      .createQuery("Select s from Sala s where s.planta = :planta", Sala.class)
      .setParameter("planta", planta);
    return query.getResultList();
  }
}
