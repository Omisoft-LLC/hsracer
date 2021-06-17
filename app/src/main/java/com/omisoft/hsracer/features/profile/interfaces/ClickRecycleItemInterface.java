package com.omisoft.hsracer.features.profile.interfaces;

import android.view.View;

/**
 * Recycle Interface
 * Created by developer on 02.10.17.
 */

public interface ClickRecycleItemInterface {

  void clickOnView(View view, int position);

  void deleteVehicle(int position);
}
