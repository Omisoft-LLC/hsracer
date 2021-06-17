package com.omisoft.hsracer.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import java.util.List;

/**
 * Cars DAO
 * Created by developer on 01.12.17.
 */
@Dao
public interface CarsDAO {

  @Query("SELECT DISTINCT C.make FROM CARS_REF C WHERE c.type=:type ORDER BY c.make")
  List<String> returnMakes(String type);

  @Query("SELECT c.model FROM CARS_REF c WHERE c.make=:make AND c.type=:type")
  List<String> listWithModelsFromAChosenMake(String make, String type);

}
