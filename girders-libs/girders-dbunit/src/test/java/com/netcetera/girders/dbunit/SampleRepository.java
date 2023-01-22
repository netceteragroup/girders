package com.netcetera.girders.dbunit;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


/**
 * Repository for managing sample entities.
 */
@Repository
@Transactional
public class SampleRepository {

  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Returns a sample with a given name.
   * 
   * @param name the name
   * @return the sample with given name in this repository
   */
  public Sample findSampleByName(String name) {
    return (Sample) entityManager.createQuery("from Sample where name = :name")
        .setParameter("name", name)
        .getSingleResult();
  }

}
