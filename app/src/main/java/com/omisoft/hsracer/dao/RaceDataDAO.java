package com.omisoft.hsracer.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import com.omisoft.hsracer.model.RaceDataEntity;
import java.util.List;

/**
 * Race DAO
 * Created by dido on 27.04.17.
 */
@Dao
public interface RaceDataDAO extends BaseDAO<RaceDataEntity> {

  @Override
  @Insert
  long create(RaceDataEntity profile);

  @Override
  @Insert
  long[] create(RaceDataEntity... races);

  @Override
  @Update
  int update(RaceDataEntity race);

  @Override
  @Delete
  int delete(RaceDataEntity... races);


  @Override
  @Query("SELECT * FROM RACE_DATA")
  List<RaceDataEntity> list();

  @Override
  @Query("SELECT * FROM RACE_DATA WHERE _id=:id")
  RaceDataEntity findById(Long id);

  @Override
  @Query("SELECT * FROM RACE_DATA WHERE rest_Id=:id")
  RaceDataEntity findByRestId(Long id);

  @Query("SELECT MAX(GPSSPEED) FROM RACE_DATA WHERE race_id=:id")
  Double getMaxSpeedByRaceId(Long id);

  @Query("SELECT AVG(GPSSPEED) FROM RACE_DATA WHERE race_id=:id")
  Double getAverageSpeedByRaceId(Long id);


}
