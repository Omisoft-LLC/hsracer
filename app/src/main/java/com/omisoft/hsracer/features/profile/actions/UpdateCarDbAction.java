package com.omisoft.hsracer.features.profile.actions;

import com.omisoft.hsracer.common.BaseApp;
import com.omisoft.hsracer.common.loader.BaseDbAction;
import com.omisoft.hsracer.features.profile.events.SuccessfulDBSaveCarEvent;
import com.omisoft.hsracer.model.Car;
import org.greenrobot.eventbus.EventBus;

/**
 * Updates car in DB
 * Created by dido on 08.06.17.
 */

public class UpdateCarDbAction extends BaseDbAction implements Runnable {

  private final static String TAG = UpdateCarDbAction.class.getName();
  private final BaseApp baseApp;
  private final Car car;

  public UpdateCarDbAction(BaseApp baseApp, Car car) {
    this.baseApp = baseApp;
    this.car = car;
  }

  @Override
  public void run() {
    baseApp.getDB().carDAO().update(car);
    EventBus.getDefault().post(new SuccessfulDBSaveCarEvent(this.getClass().getSimpleName()));
  }

}
