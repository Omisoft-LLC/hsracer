package com.omisoft.hsracer.dao;

import java.util.List;

/**
 * Base DAO Interface
 * Created by dido on 27.04.17.
 */

public interface BaseDAO<T> {

  /**
   * Create entity and return id of object
   */
  long create(T t);

  /**
   * Create multiple entities
   */
  long[] create(T... t);


  /**
   * Update entity
   */
  int update(T t);

  /**
   * Delete entity
   */
  int delete(T... t);

  /**
   * List all entities
   */
  List<T> list();

  /**
   * Find entity by id
   */
  T findById(Long id);

  /**
   * Finds entity by rest id
   */
  T findByRestId(Long id);


}
