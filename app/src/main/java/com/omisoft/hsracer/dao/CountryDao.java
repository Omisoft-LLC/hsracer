package com.omisoft.hsracer.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import java.util.List;

/**
 * Country DAO
 * Created by developer on 01.12.17.
 */
@Dao
public interface CountryDao {

  @Query("SELECT c.countries FROM COUNTRIES_REF c")
  List<String> list();
}
