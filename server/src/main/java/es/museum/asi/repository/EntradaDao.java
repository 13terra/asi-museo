package es.museum.asi.repository;

import es.museum.asi.model.domain.Entrada;


import java.util.Collection;

public interface EntradaDao {

  void create(Entrada entrada);

  void update(Entrada entrada);

  void delete(Entrada entrada);

  Collection<Entrada> findAll();

  Entrada findById(Long idEntrada);

  Collection<Entrada> findByReserva(Long idReserva);

  Collection<Entrada> findByTipoEntrada(Long  idTipoEntrada);

  Collection<Entrada> findByUser(Long idUser); //a través de reserva

  Collection<Entrada> findBySesion(Long idSesion);

}
