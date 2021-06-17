package com.omisoft.hsracer.features.profile.actions;

import com.omisoft.hsracer.common.BaseApp;
import com.omisoft.hsracer.features.profile.events.SuccessfulDBSaveCarEvent;
import com.omisoft.hsracer.model.Car;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by developer on 06.12.17.
 */

public class SaveCarInDbAction implements Runnable {

  private Car car;
  private BaseApp baseApp;

  public SaveCarInDbAction(Car car, BaseApp baseApp) {
    this.baseApp = baseApp;
    this.car = car;
  }

  @Override
  public void run() {

    baseApp.getDB().carDAO().create(car);
    EventBus.getDefault().post(new SuccessfulDBSaveCarEvent(this.getClass().getSimpleName()));

  }
}
