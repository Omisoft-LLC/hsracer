package com.omisoft.hsracer.features.profile.actions;

import com.omisoft.hsracer.common.BaseApp;
import com.omisoft.hsracer.features.profile.events.ListOfModelsByMakeEvent;
import java.util.List;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by developer on 04.12.17.
 */

public class GetModelsByMakeDbAction implements Runnable {

  private BaseApp baseApp;
  private String make;
  private String type;

  public GetModelsByMakeDbAction(BaseApp baseApp, String make, String type) {
    this.baseApp = baseApp;
    this.make = make;
    this.type = type;
  }

  @Override
  public void run() {
    List<String> listOfModels = baseApp.getReferenceDB().carsDao()
        .listWithModelsFromAChosenMake(make, type);
    EventBus.getDefault()
        .post(new ListOfModelsByMakeEvent(listOfModels, this.getClass().getSimpleName()));
  }
}
