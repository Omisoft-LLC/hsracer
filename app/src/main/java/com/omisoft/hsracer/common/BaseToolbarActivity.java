package com.omisoft.hsracer.common;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.features.race.structures.MonitorStatus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Shows a icons on the toolbar of activities that extend BaseToolbarActivity. They indicate if the
 * particular sensors are turned on (pulsing - turned off) Created by developer on 05.09.17.
 */

@SuppressLint("Registered")
public class BaseToolbarActivity extends BaseActivity {

  private MenuItem obd;
  private MenuItem gps;
  private MenuItem net;
  private Animation animation;
  private MonitorStatus prev;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onStart() {
    super.onStart();
  }

  @Override
  public void onStop() {
    super.onStop();

  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void sensorCheckUp(MonitorStatus event) {
    // TODO remove if when the espresso tests are ready
    if (!event.equals(prev)) {
      prev = event;

      manipulateImages(obd, event.isHasOBD());
      manipulateImages(gps, event.isHasGPS());
      manipulateImages(net, event.isHasNetwork());


    }
  }

  private void manipulateImages(MenuItem view, boolean threadStarted) {
    if (view != null) {
      if (!threadStarted) {
        view.setVisible(true);
        view.getActionView().startAnimation(animation);
      } else {
        if (view.getActionView().getAnimation() != null) {
          view.getActionView().clearAnimation();
        }
        view.setVisible(true);
      }
    }
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_toolbar_sensors, menu);
    animation = AnimationUtils.loadAnimation(getApp(), R.anim.blinking_icons_toolbar);
    obd = menu.getItem(0);
    obd.setActionView(R.layout.action_view_for_menu_obd);
    gps = menu.getItem(1);
    gps.setActionView(R.layout.action_view_for_menu_gps);
    net = menu.getItem(2);
    net.setActionView(R.layout.action_view_for_menu_net);

    return true;
  }


}
