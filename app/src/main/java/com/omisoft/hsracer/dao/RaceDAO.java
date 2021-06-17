package com.omisoft.hsracer.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import com.omisoft.hsracer.model.Race;
import java.util.List;

/**
 * Race DAO
 * Created by dido on 27.04.17.
 */
@Dao
public interface RaceDAO extends BaseDAO<Race> {

  @Override
  @Insert
  long create(Race profile);

  @Override
  @Insert
  long[] create(Race... races);

  @Override
  @Update
  int update(Race race);

  @Override
  @Delete
  int delete(Race... races);


  @Override
  @Query("SELECT * FROM RACE")
  List<Race> list();

  @Override
  @Query("SELECT * FROM RACE WHERE _id=:id")
  Race findById(Long id);

  @Override
  @Query("SELECT * FROM RACE WHERE rest_Id=:id")
  Race findByRestId(Long id);


}
