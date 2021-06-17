package com.omisoft.hsracer.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import com.omisoft.hsracer.dao.CarsDAO;
import com.omisoft.hsracer.dao.CountryDao;
import com.omisoft.hsracer.model.Cars;
import com.omisoft.hsracer.model.Country;


/**
 * Reference database
 * Created by developer on 01.12.17.
 */
@Database(entities = {Cars.class, Country.class}, version = 2)
public abstract class AppReferenceDatabase extends RoomDatabase {

  public abstract CarsDAO carsDao();

  public abstract CountryDao countryDao();

}
