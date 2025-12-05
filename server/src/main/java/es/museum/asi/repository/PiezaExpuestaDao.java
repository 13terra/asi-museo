package es.museum.asi.repository;

import es.museum.asi.model.domain.PiezaExpuesta;

import java.util.Collection;

public interface PiezaExpuestaDao {

  void create(PiezaExpuesta piezaExpuesta);

  void update(PiezaExpuesta piezaExpuesta);

  void delete(PiezaExpuesta piezaExpuesta);

  Collection<PiezaExpuesta> findAll();

  PiezaExpuesta findById(Long id);

  Collection<PiezaExpuesta> findByEdicion(Long idEdicion);

  Collection<PiezaExpuesta> findBySala(Long idSala);

  PiezaExpuesta findPortadaByEdicion(Long idEdicion);
}
