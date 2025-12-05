package es.museum.asi.repository;

import es.museum.asi.model.domain.Reserva;
import es.museum.asi.model.enums.EstadoReserva;
import es.museum.asi.repository.util.GenericDaoJpa;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class ReservaDaoJpa extends GenericDaoJpa implements ReservaDao {

  @Override
  public void create(Reserva reserva) {
    entityManager.persist(reserva);
  }

  @Override
  public void update(Reserva reserva) {
    entityManager.merge(reserva);
  }

  @Override
  public void delete(Reserva reserva) {
    entityManager.remove(reserva);
  }

  @Override
  public Reserva findById(Long id) {
    return entityManager.find(Reserva.class, id);
  }

  @Override
  public Collection<Reserva> findAll() {
    return entityManager.createQuery("from Reserva", Reserva.class).getResultList();
  }

  @Override
  public Collection<Reserva> findByUser(Long idUser) {
    TypedQuery<Reserva> query = entityManager
      .createQuery("Select r From Reserva r Where r.user.idUser = :idUser Order By r.fechaReserva DESC", Reserva.class) //de nuevas a viejas
      .setParameter("idUser", idUser);
    return query.getResultList();
  }

  @Override
  public Collection<Reserva> findBySesion(Long idSesion) {
    TypedQuery<Reserva> query = entityManager
      .createQuery("Select r From Reserva r Where r.sesion.idSesion = :idSesion", Reserva.class)
      .setParameter("idSesion", idSesion);
    return query.getResultList();
  }

  @Override
  public Collection<Reserva> findByEstado(EstadoReserva estadoReserva) {
    TypedQuery<Reserva> query = entityManager
      .createQuery("Select r From Reserva r Where r.estadoReserva = :estadoReserva", Reserva.class)
      .setParameter("estadoReserva", estadoReserva);
    return query.getResultList();
  }

}
