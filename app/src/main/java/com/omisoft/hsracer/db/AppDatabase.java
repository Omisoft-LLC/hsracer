package com.omisoft.hsracer.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import com.omisoft.hsracer.dao.CarDAO;
import com.omisoft.hsracer.dao.ObdDataDAO;
import com.omisoft.hsracer.dao.ProfileDAO;
import com.omisoft.hsracer.dao.RaceDAO;
import com.omisoft.hsracer.dao.RaceDataDAO;
import com.omisoft.hsracer.model.Car;
import com.omisoft.hsracer.model.OBDData;
import com.omisoft.hsracer.model.Profile;
import com.omisoft.hsracer.model.Race;
import com.omisoft.hsracer.model.RaceDataEntity;

/**
 * Main database
 * Created by dido on 08.06.17.
 */
@Database(entities = {Profile.class, Car.class, Race.class, RaceDataEntity.class,
    OBDData.class}, version = 2)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

  public abstract CarDAO carDAO();

  public abstract ProfileDAO profileDAO();

  public abstract RaceDAO raceDAO();

  public abstract RaceDataDAO raceDataDAO();

  public abstract ObdDataDAO obdDataDAO();

}
