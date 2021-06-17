package com.omisoft.hsracer.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import com.omisoft.hsracer.model.OBDData;
import java.util.List;

/**
 * Race DAO
 * Created by dido on 27.04.17.
 */
@Dao
public interface ObdDataDAO extends BaseDAO<OBDData> {

  @Override
  @Insert
  long create(OBDData obdData);

  @Override
  @Insert
  long[] create(OBDData... obdDataEntries);

  @Override
  @Update
  int update(OBDData obdData);

  @Override
  @Delete
  int delete(OBDData... obdData);


  @Override
  @Query("SELECT * FROM OBD_DATA")
  List<OBDData> list();

  @Override
  @Query("SELECT * FROM OBD_DATA WHERE _id=:id")
  OBDData findById(Long id);

  @Override
  @Query("SELECT * FROM OBD_DATA WHERE rest_Id=:id")
  OBDData findByRestId(Long id);

  @Query("SELECT MAX(SPEED) FROM OBD_DATA WHERE race_id=:id")
  Double getMaxSpeedByRaceId(Long id);

  @Query("SELECT AVG(SPEED) FROM OBD_DATA WHERE race_id=:id")
  Double getAverageSpeedByRaceId(Long id);


  @Query("SELECT MAX(RPM) FROM OBD_DATA WHERE race_id=:id")
  Double getMaxRPMByRaceId(Long id);

  @Query("SELECT AVG(RPM) FROM OBD_DATA WHERE race_id=:id")
  Double getAverageRPMByRaceId(Long id);


}
