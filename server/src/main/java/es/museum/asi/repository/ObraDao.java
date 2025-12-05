package es.museum.asi.repository;


import es.museum.asi.model.domain.Obra;
import es.museum.asi.model.enums.EstadoObra;

import java.util.Collection;

public interface ObraDao {

  void create(Obra obra);

  void update(Obra obra);

  void delete(Obra obra);

  Collection<Obra> findAll();

  Obra findById(Long idObra);

  Collection<Obra> findByAutor(String autor);

  Collection<Obra> findByEstado(EstadoObra estado);

  Obra findByIdExterno(Long idExterno);
}
