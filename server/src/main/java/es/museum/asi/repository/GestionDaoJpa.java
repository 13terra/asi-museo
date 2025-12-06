package es.museum.asi.repository;

import es.museum.asi.model.domain.Gestion;
import es.museum.asi.model.enums.TipoPermiso;
import es.museum.asi.repository.util.GenericDaoJpa;
import jakarta.persistence.TypedQuery;
import org.springframework.dao.support.DataAccessUtils;

import java.util.Collection;

public class GestionDaoJpa extends GenericDaoJpa implements GestionDao {

  @Override
  public void create(Gestion gestion) {
    entityManager.persist(gestion);
  }

  @Override
  public void update(Gestion gestion) {
    entityManager.merge(gestion);
  }

  @Override
  public void delete(Gestion gestion) {
    entityManager.remove(gestion);
  }

  @Override
  public Gestion findById(Long idGestion) {
    return entityManager.find(Gestion.class, idGestion);
  }

  @Override
  public Collection<Gestion> findAll() {
    return entityManager.createQuery("from Gestion", Gestion.class).getResultList();
  }

  @Override
  public Collection<Gestion> findByExpo(Long idExposicion) {
    TypedQuery<Gestion> query = entityManager
      .createQuery("Select g From Gestion g Where g.exposicion.idExposicion = :idExposicion ", Gestion.class)
      .setParameter("idExposicion", idExposicion);
    return query.getResultList();
  }

  @Override
  public Collection<Gestion> findByUser(Long idUser) {
    TypedQuery<Gestion> query = entityManager
      .createQuery("Select g From Gestion g Where g.user.idUser = :idUser ", Gestion.class)
      .setParameter("idUser", idUser);
    return query.getResultList();
  }

  // útil para verificar permisos a la hora de las validaciones (quién puede hacer qué)
  @Override
  public Gestion findByUserAndExpo(Long idUser, Long idExposicion) {
    TypedQuery<Gestion> query = entityManager
      .createQuery("Select g From Gestion g Where g.exposicion.idExposicion = :idExposicion And g.user.idUser = :idUser", Gestion.class)
      .setParameter("idExposicion", idExposicion)
      .setParameter("idUser", idUser);
    return DataAccessUtils.singleResult(query.getResultList());
  }

  // esto para listar gestores  por tipo
  @Override
  public Collection<Gestion> findByExpoAndPermiso(Long idExposicion, TipoPermiso permiso) {
    TypedQuery<Gestion> query = entityManager
      .createQuery("Select g From Gestion g Where g.exposicion.idExposicion = :idExposicion And g.permiso = :permiso", Gestion.class)
      .setParameter("idExposicion", idExposicion)
      .setParameter("permiso", permiso);
    return query.getResultList();
  }

}
