package com.omisoft.hsracer.features.profile.actions;

import android.util.Log;
import com.omisoft.hsracer.common.BaseApp;
import com.omisoft.hsracer.common.loader.BaseDbAction;
import com.omisoft.hsracer.features.profile.events.SuccessGetProfileDbEvent;
import com.omisoft.hsracer.model.Profile;
import java.util.List;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by Omisoft LLC. on 6/19/17.
 */

public class GetPersonalInformationDbAction extends BaseDbAction implements Runnable {

  private final Long profileId;
  private final BaseApp baseApp;

  public GetPersonalInformationDbAction(BaseApp baseApp, Long profileId) {
    this.baseApp = baseApp;
    this.profileId = profileId;
  }

  @Override
  public void run() {
    try {
      Profile profile = baseApp.getDB().profileDAO().findById(profileId);
      List<String> countries = baseApp.getReferenceDB().countryDao().list();
      EventBus.getDefault()
          .postSticky(
              new SuccessGetProfileDbEvent(profile, countries, this.getClass().getSimpleName()));
    } catch (Exception e) {
      Log.e(TAG, "run: ", e);
    }
  }
}