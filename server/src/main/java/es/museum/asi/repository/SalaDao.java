package es.museum.asi.repository;


import es.museum.asi.model.domain.Sala;

import java.util.Collection;

public interface SalaDao {

  void create(Sala sala);

  void update(Sala sala);

  void delete(Sala sala);

  Collection<Sala> findAll();

  Sala findById(Long idSala);

  Sala findByNombre(String nombre);

  Collection<Sala> findByPlanta(Integer planta);

}
