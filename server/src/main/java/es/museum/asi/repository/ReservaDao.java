package es.museum.asi.repository;


import es.museum.asi.model.domain.Reserva;
import es.museum.asi.model.enums.EstadoReserva;

import java.util.Collection;

public interface ReservaDao {

  void create(Reserva reserva);

  void  update(Reserva reserva);

  void delete(Reserva reserva);

  Collection<Reserva> findAll();

  Reserva findById(Long id);

  Collection<Reserva> findByUser(Long idUser);

  Collection<Reserva> findBySesion(Long idSesion);

  Collection<Reserva> findByEstado(EstadoReserva estado);

}
