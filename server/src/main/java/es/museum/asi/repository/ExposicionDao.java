package es.museum.asi.repository;

import es.museum.asi.model.domain.Exposicion;

import java.util.Collection;

public interface ExposicionDao {

  Collection<Exposicion> findAll();

  Exposicion findById(Long id);

  /**
   * Busca exposiciones por estado.
   * @param estado Estado de la exposición (BORRADOR, ACTIVA, ARCHIVADA)
   * @return Colección de exposiciones
   */
  Collection<Exposicion> findByEstado(String estado);

  void create(Exposicion exposicion);

  void update(Exposicion exposicion);

  void delete(Exposicion exposicion);
}
