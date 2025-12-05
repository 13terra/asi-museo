package es.museum.asi.repository;

import es.museum.asi.model.domain.Sesion;
import es.museum.asi.model.enums.EstadoSesion;
import es.museum.asi.repository.util.GenericDaoJpa;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class SesionDaoJpa extends GenericDaoJpa implements SesionDao {

  @Override
  public void create(Sesion sesion) {
    entityManager.persist(sesion);
  }

  @Override
  public void update(Sesion sesion) {
    entityManager.merge(sesion);
  }

  @Override
  public void delete(Sesion sesion) {
    entityManager.remove(sesion);
  }

  @Override
  public Collection<Sesion> findAll() {
    return entityManager.createQuery("from Sesion s", Sesion.class).getResultList();
  }

  @Override
  public Sesion findById(Long id) {
    return entityManager.find(Sesion.class, id);
  }

  @Override
  public Collection<Sesion> findByEdicion(Long idEdicion) {
    TypedQuery<Sesion> query = entityManager
      .createQuery("SELECT s FROM Sesion s WHERE s.edicion.idEdicion = :idEdicion", Sesion.class) //se accede al id de la edicion, a través de la edición asociada de la sesion
      .setParameter("idEdicion", idEdicion);
    return query.getResultList();
  }

  @Override
  public Collection<Sesion> findByEstado(EstadoSesion estado) {
    TypedQuery<Sesion> query = entityManager
      .createQuery("SELECT s FROM Sesion WHERE s.estadoSesion = :estado", Sesion.class)
      .setParameter("estado", estado);
    return query.getResultList();
  }

}
