package es.museum.asi.repository;

import es.museum.asi.model.domain.Obra;
import es.museum.asi.model.enums.EstadoObra;
import es.museum.asi.repository.util.GenericDaoJpa;
import jakarta.persistence.TypedQuery;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class ObraDaoJpa extends GenericDaoJpa implements ObraDao {

  @Override
  public void create(Obra obra) {
    entityManager.persist(obra);
  }

  @Override
  public void update(Obra obra) {
    entityManager.merge(obra);
  }

  @Override
  public void delete(Obra obra) {
    entityManager.remove(obra);
  }

  @Override
  public Collection<Obra> findAll() {
    return entityManager.createQuery("Select o from Obra o", Obra.class).getResultList();
  }

  @Override
  public Obra findById(Long id) {
    return entityManager.find(Obra.class, id);
  }

  @Override
  public Collection<Obra> findByAutor(String autor) {
    TypedQuery<Obra> query = entityManager
      .createQuery("Select o From Obra o Where o.autor LIKE :autor", Obra.class)    // usamos LIKE para búsqueda parcial
      .setParameter("autor", "%" + autor + "%");
    return query.getResultList();
  }

  @Override
  public Collection<Obra> findByEstado(EstadoObra estado) {
    TypedQuery<Obra> query = entityManager
      .createQuery("Select o From Obra o Where o.estadoObra = :estado", Obra.class)
      .setParameter("estado", estado);
    return query.getResultList();
  }

  @Override
  public Obra findByIdExterno(Long idExterno) {
   TypedQuery<Obra> query = entityManager
     .createQuery("Select o From Obra o Where o.idExterno = :idExterno", Obra.class)
     .setParameter("idExterno", idExterno);
   return DataAccessUtils.singleResult(query.getResultList());
  }

}
