package com.omisoft.hsracer.features.profile.actions;

import com.omisoft.hsracer.common.BaseApp;
import com.omisoft.hsracer.model.Profile;

/**
 * Updates DB Action
 * Created by dido on 13.06.17.
 */

public class UpdateProfileDbAction implements Runnable {

  private final Profile profile;
  private final BaseApp baseApp;

  public UpdateProfileDbAction(BaseApp baseAppContext, Profile profile) {
    this.baseApp = baseAppContext;
    this.profile = profile;
  }

  @Override
  public void run() {
    baseApp.getDB().profileDAO().update(profile);

  }
}
