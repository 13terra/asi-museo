package es.museum.asi.repository.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Clase abstracta para todos los DAOs JPA
 * Proporciona acceso al EntityManager de forma centralizada
 */
public abstract class GenericDaoJpa {
  @PersistenceContext
  protected EntityManager entityManager;
}
