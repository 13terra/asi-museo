package es.museum.asi.repository;

import es.museum.asi.model.domain.Exposicion;
import es.museum.asi.model.enums.EstadoExpo;

import java.util.Collection;

public interface ExposicionDao {

  Collection<Exposicion> findAll();

  Exposicion findById(Long id);

  Collection<Exposicion> findByEstado(EstadoExpo estado);

  void create(Exposicion exposicion);

  void update(Exposicion exposicion);

  void delete(Exposicion exposicion);
}
