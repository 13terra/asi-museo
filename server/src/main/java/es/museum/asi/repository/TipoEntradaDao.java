package es.museum.asi.repository;

import es.museum.asi.model.domain.TipoEntrada;

import java.util.Collection;

public interface TipoEntradaDao {


  void create(TipoEntrada tipoEntrada);

  void update(TipoEntrada tipoEntrada);

  void delete(TipoEntrada tipoEntrada);

  Collection<TipoEntrada> findAll();

  TipoEntrada findById(Long idTipoEntrada);

  TipoEntrada findByNombre(String nombre);
}
