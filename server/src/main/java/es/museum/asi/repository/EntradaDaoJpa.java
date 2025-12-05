package es.museum.asi.repository;

import es.museum.asi.model.domain.Entrada;
import es.museum.asi.repository.util.GenericDaoJpa;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class EntradaDaoJpa extends GenericDaoJpa implements EntradaDao {

  @Override
  public void create(Entrada entrada) {
    entityManager.persist(entrada);
  }

  @Override
  public void update(Entrada entrada) {
    entityManager.merge(entrada);
  }

  @Override
  public void delete(Entrada entrada) {
    entityManager.remove(entrada);
  }

  @Override
  public Entrada findById(Long id) {
    return entityManager.find(Entrada.class, id);
  }

  @Override
  public Collection<Entrada> findAll() {
    return entityManager.createQuery("from Entrada", Entrada.class).getResultList();
  }

  @Override
  public Collection<Entrada> findByReserva(Long idReserva) {
    TypedQuery<Entrada> query = entityManager
      .createQuery("Select e From Entrada Where e.reserva.idReserva = :idReserva", Entrada.class)
      .setParameter("idReserva", idReserva);
    return query.getResultList();
  }

  @Override
  public Collection<Entrada> findByUser(Long idUser) {
    TypedQuery<Entrada> query = entityManager
      .createQuery("Select e From Entrada e Where e.reserva.user.idUser  = :idUser", Entrada.class) // si se puede hacer así la polla
      .setParameter("idUser", idUser);
    return query.getResultList();
  }

  @Override
  public Collection<Entrada> findByTipoEntrada(Long idTipoEntrada) {
    TypedQuery<Entrada> query = entityManager
      .createQuery("Select e From Entrada e Where e.tipoEntrada.idTipoEntrada = :idTipoEntrada", Entrada.class)
      .setParameter("idTipoEntrada", idTipoEntrada);
    return query.getResultList();
  }

  @Override
  public Collection<Entrada> findBySesion(Long idSesion) {
    TypedQuery<Entrada> query = entityManager
      .createQuery("Select e From Entrada e Where e.reserva.sesion.idSesion = :idSesion", Entrada.class)
      .setParameter("idSesion", idSesion);
    return query.getResultList();
  }
}
