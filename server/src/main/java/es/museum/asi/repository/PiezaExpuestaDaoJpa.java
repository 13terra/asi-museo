package es.museum.asi.repository;

import es.museum.asi.model.domain.PiezaExpuesta;
import es.museum.asi.repository.util.GenericDaoJpa;
import jakarta.persistence.TypedQuery;
import org.hibernate.boot.model.TypeDefinition;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Type;
import java.util.Collection;

@Repository
public class PiezaExpuestaDaoJpa extends GenericDaoJpa implements  PiezaExpuestaDao {

  @Override
  public void create(PiezaExpuesta piezaExpuesta) {
    entityManager.persist(piezaExpuesta);
  }

  @Override
  public void update(PiezaExpuesta piezaExpuesta) {
    entityManager.merge(piezaExpuesta);
  }

  @Override
  public void delete(PiezaExpuesta piezaExpuesta) {
    entityManager.remove(piezaExpuesta);
  }

  @Override
  public Collection<PiezaExpuesta> findAll() {
    return entityManager.createQuery("Select pe From PiezaExpuesta pe", PiezaExpuesta.class).getResultList();
  }

  @Override
  public PiezaExpuesta findById(Long id) {
    return entityManager.find(PiezaExpuesta.class, id);
  }

  @Override
  public Collection<PiezaExpuesta> findByEdicion(Long idEdicion) {
    //una pe siempre tiene una ed asociada
    TypedQuery<PiezaExpuesta> query = entityManager
      .createQuery("Select pe From PiezaExpuesta pe Where pe.edicion.idEdicion = :idEdicion", PiezaExpuesta.class)
      .setParameter("idEdicion", idEdicion);
    return query.getResultList();
  }

  @Override
  public Collection<PiezaExpuesta> findBySala(Long idSala) {
    TypedQuery<PiezaExpuesta> query = entityManager
      .createQuery("Select pe From PiezaExpuesta pe Where pe.sala.idSala = :idSala Order By pe.orden", PiezaExpuesta.class)
      .setParameter("idSala", idSala);
    return query.getResultList();
  }

  @Override
  public PiezaExpuesta findPortadaByEdicion(Long idEdicion) {
    TypedQuery<PiezaExpuesta> query = entityManager
      .createQuery("Select pe From PiezaExpuesta pe Where pe.edicion.idEdicion = :idEdicion And pe.portada = true", PiezaExpuesta.class)
      .setParameter("idEdicion", idEdicion);
    return DataAccessUtils.singleResult(query.getResultList());
  }
}
