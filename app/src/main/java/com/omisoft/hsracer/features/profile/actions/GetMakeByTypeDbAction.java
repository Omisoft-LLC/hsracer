package com.omisoft.hsracer.features.profile.actions;

import android.util.Log;
import com.omisoft.hsracer.common.BaseApp;
import com.omisoft.hsracer.features.profile.events.ListVehicleTypeEvent;
import java.util.List;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by developer on 04.12.17.
 */

public class GetMakeByTypeDbAction implements Runnable {

  private BaseApp baseApp;
  private String type;

  public GetMakeByTypeDbAction(BaseApp baseApp, String type) {
    this.baseApp = baseApp;
    this.type = type;
  }

  @Override
  public void run() {
    try {
      List<String> listWithMakes = baseApp.getReferenceDB().carsDao().returnMakes(type);
      EventBus.getDefault()
          .post(new ListVehicleTypeEvent(listWithMakes, this.getClass().getSimpleName()));
    } catch (Exception e) {
      Log.wtf("Error get make", "run: ", e);
    }
  }
}
