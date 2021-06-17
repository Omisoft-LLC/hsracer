package com.omisoft.hsracer.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import com.omisoft.hsracer.model.Profile;
import java.util.List;

/**
 * Profile DAO
 * Created by dido on 27.04.17.
 */
@Dao
public interface ProfileDAO extends BaseDAO<Profile> {

  @Override
  @Insert
  long create(Profile profile);

  @Override
  @Insert
  long[] create(Profile... profile);

  @Override
  @Update
  int update(Profile profile);

  @Override
  @Delete
  int delete(Profile... profile);

  @Override
  @Query("SELECT * FROM PROFILE")
  List<Profile> list();

  @Override
  @Query("SELECT * FROM PROFILE WHERE _id=:id")
  Profile findById(Long id);

  @Override
  @Query("SELECT * FROM PROFILE WHERE rest_Id=:id")
  Profile findByRestId(Long id);

  @Query("SELECT * FROM PROFILE WHERE rest_Id=:id")
  Profile findLocalIdByRestId(Long id);

}
