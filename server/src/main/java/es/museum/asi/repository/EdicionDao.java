package es.museum.asi.repository;

import es.museum.asi.model.domain.Edicion;
import es.museum.asi.model.domain.Exposicion;

import java.util.Collection;

public interface EdicionDao {

  void create(Edicion edicion);

  void update(Edicion edicion);

  void delete(Edicion edicion);

  Collection<Edicion> findAll();

  Edicion findById(Long id);

  Collection<Edicion> findByExposicion(Long idExposicion);
}
