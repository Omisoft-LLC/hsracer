package com.omisoft.hsracer.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;
import com.omisoft.hsracer.model.Car;
import java.util.List;

/**
 * Holds carName dao
 * Created by dido on 27.04.17.
 */
@Dao
public interface CarDAO extends BaseDAO<Car> {

  @Override
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  long create(Car car);

  @Override
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  long[] create(Car... car);

  @Override
  @Update
  int update(Car car);

  @Override
  @Delete
  int delete(Car... car);

  @Query("DELETE FROM CAR WHERE profile_id=:id")
  int deleteAll(Long id);

  @Override
  @Query("SELECT * FROM CAR")
  List<Car> list();

  @Query("SELECT * FROM CAR WHERE profile_id=:id")
  List<Car> listById(Long id);

  @Query("SELECT * FROM CAR WHERE profile_id=:id")
  Cursor listCursor(Long id);

  @Query("SELECT * FROM CAR")
  Cursor listCursor();

  @Override
  @Query("SELECT * FROM CAR WHERE rest_Id=:id")
  Car findByRestId(Long id);

  @Override
  @Query("SELECT * FROM CAR WHERE _id=:id")
  Car findById(Long id);

}
