package es.museum.asi.repository;

import es.museum.asi.model.domain.Gestion;
import es.museum.asi.model.enums.TipoPermiso;
import org.hibernate.boot.internal.GenerationStrategyInterpreter;

import java.util.Collection;

public interface GestionDao {

  void create (Gestion gestion);

  void update (Gestion gestion);

  void delete (Gestion gestion);

  Collection<Gestion> findAll();

  Gestion findById(Long idGestion);

  //Busca todas gestiones/permisos de un usuario
  Collection<Gestion> findByUser(Long idUser);

  //Busca todas gestiones/permisos de una expo
  Collection<Gestion> findByExpo(Long idExposicion);

  Gestion findByUserAndExpo(Long idUser, Long idExposicion);

  Collection<Gestion> findByExpoAndPermiso(Long idExposicion, TipoPermiso tipoPermiso);
}
