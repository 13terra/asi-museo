package es.udc.asi.museo_rest.repository.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public abstract class GenericDaoJpa {
  @PersistenceContext
  protected EntityManager entityManager;
}
