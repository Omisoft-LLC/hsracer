package com.omisoft.hsracer.features.profile.actions;

import android.util.Log;
import com.omisoft.hsracer.common.BaseApp;
import com.omisoft.hsracer.common.loader.BaseDbAction;
import com.omisoft.hsracer.db.AppDatabase;
import com.omisoft.hsracer.model.Car;

/**
 * Delete car from DB
 * Created by dido on 08.06.17.
 */

public class DeleteCarDbAction extends BaseDbAction implements Runnable {

  private static final String TAG = DeleteCarDbAction.class.getName();
  private final BaseApp baseApp;
  private final Long carId;

  public DeleteCarDbAction(BaseApp baseApp, Long restId) {
    this.baseApp = baseApp;
    this.carId = restId;
  }


  @Override
  public void run() {
    try {
      final AppDatabase appDatabase = baseApp.getDB();
      Car car = appDatabase.carDAO().findById(carId);
      Log.wtf("DELETE CAR", "run: " + 3 + " " + car.toString());
      appDatabase.carDAO().delete(car);
      Log.wtf("DELETE CAR", "run: " + 4);
    } catch (Exception e) {
      Log.wtf(TAG, "run: " , e );
    }
  }

}
