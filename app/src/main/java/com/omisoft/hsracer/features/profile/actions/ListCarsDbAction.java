package com.omisoft.hsracer.features.profile.actions;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.omisoft.hsracer.common.BaseApp;
import com.omisoft.hsracer.common.loader.BaseDbAction;
import com.omisoft.hsracer.constants.Constants;
import com.omisoft.hsracer.db.AppDatabase;
import com.omisoft.hsracer.features.profile.events.ListCarsEvent;
import com.omisoft.hsracer.model.Car;
import java.util.List;
import org.greenrobot.eventbus.EventBus;

/**
 * Create car in local DB
 * Created by dido on 08.06.17.
 */

public class ListCarsDbAction extends BaseDbAction implements Runnable {

  private final BaseApp baseApp;

  public ListCarsDbAction(BaseApp baseApp) {
    this.baseApp = baseApp;
  }


  @Override
  public void run() {
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(baseApp);
    final AppDatabase appDatabase = baseApp.getDB();
    List<Car> result = appDatabase.carDAO()
        .listById(sharedPreferences.getLong(Constants.ID, 10000));
    Log.wtf("CARS", "run: " + result.toString() );
    EventBus.getDefault().postSticky(new ListCarsEvent(result, this.getClass().getSimpleName()));

  }
}

