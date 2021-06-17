package com.omisoft.hsracer.features.profile.actions;

import static com.omisoft.hsracer.constants.Constants.RACER_CREATES_RACE;
import static com.omisoft.hsracer.constants.Constants.RACER_JOINS_RACE;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.common.BaseApp;
import com.omisoft.hsracer.common.events.ErrorEvent;
import com.omisoft.hsracer.common.loader.BaseDbAction;
import com.omisoft.hsracer.constants.Constants;
import com.omisoft.hsracer.db.AppDatabase;
import com.omisoft.hsracer.features.profile.events.ListCarsArrayListCreateRaceEvent;
import com.omisoft.hsracer.features.profile.events.ListCarsArrayListJoinRaceEvent;
import com.omisoft.hsracer.model.Car;
import java.util.List;
import org.greenrobot.eventbus.EventBus;

/**
 * Create car in local DB
 * Created by dido on 08.06.17.
 */

public class ListCarsDbArrayListAction extends BaseDbAction implements Runnable {


  private static final String TAG = ListCarsDbArrayListAction.class.getName();
  private final BaseApp baseApp;

  //whether the racer creates the race or simply joins it
  private final int racerStatus;

  public ListCarsDbArrayListAction(BaseApp baseApp, int racerStatus) {
    this.racerStatus = racerStatus;
    this.baseApp = baseApp;
  }


  @Override
  public void run() {
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(baseApp);
    List<Car> result = null;
    if (sharedPreferences.contains(Constants.ID)) {
      final AppDatabase appDatabase = baseApp.getDB();
      try {
        result = appDatabase.carDAO()
            .listById(sharedPreferences.getLong(Constants.ID, 10000));
      } catch (Throwable t) {
        EventBus.getDefault()
            .post(new ErrorEvent(R.string.error_db, t, this.getClass().getSimpleName()));
      }

    }
    switch (racerStatus) {
      case RACER_CREATES_RACE:
        EventBus.getDefault().post(new ListCarsArrayListCreateRaceEvent(result));
        break;
      case RACER_JOINS_RACE:
        EventBus.getDefault().post(new ListCarsArrayListJoinRaceEvent(result));
        break;
    }
  }
}

