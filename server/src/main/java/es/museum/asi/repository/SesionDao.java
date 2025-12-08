package es.museum.asi.repository;

import es.museum.asi.model.domain.Sesion;
import es.museum.asi.model.enums.EstadoSesion;

import java.util.Collection;

public interface SesionDao {

  void  create(Sesion sesion);

  void update(Sesion sesion);

  void delete(Sesion sesion);

  Collection<Sesion> findAll();

  Sesion findById(Long id);

  Collection<Sesion> findByEdicion(Long idEdicion);

  Collection<Sesion> findByEstado(EstadoSesion estado);

  Collection<Sesion> findBySala(Long idSala);
}
