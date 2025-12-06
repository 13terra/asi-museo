package es.museum.asi.repository;

import es.museum.asi.model.domain.OrdenSalaSesion;

import java.util.Collection;

public interface OrdenSalaSesionDao {

  void create(OrdenSalaSesion ordenSalaSesion);

  void update(OrdenSalaSesion ordenSalaSesion);

  void delete(OrdenSalaSesion ordenSalaSesion);

  Collection<OrdenSalaSesion> findAll();

  OrdenSalaSesion findById(Long id);

  Collection<OrdenSalaSesion> findBySala(Long idSala);

  Collection<OrdenSalaSesion> findBySesion(Long idSesion);

  OrdenSalaSesion findBySesionAndSala(Long idSesion, Long idSala);

}
