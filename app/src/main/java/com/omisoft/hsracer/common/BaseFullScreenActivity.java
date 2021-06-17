package com.omisoft.hsracer.common;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import butterknife.BindView;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.features.race.structures.MonitorStatus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Full Screen activity
 * Created by dido on 05.09.17.
 */

@SuppressLint("Registered")
public class BaseFullScreenActivity extends BaseActivity {


  @BindView(R.id.icon_title_race_net)
  ImageView net;
  @BindView(R.id.icon_title_race_camera)
  ImageView video;
  @BindView(R.id.icon_title_race_obd)
  ImageView obd;
  @BindView(R.id.icon_title_race_gps)
  ImageView gps;
  private Animation animation;
  private MonitorStatus previousMonitorEvent;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    animation = AnimationUtils.loadAnimation(getApp(), R.anim.blinking_icons_toolbar);

  }

  @Override
  public void onStart() {
    super.onStart();
  }

  @Override
  public void onStop() {
    super.onStop();

  }

  // TODO Add check for espresso test
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void sensorCheckUp(MonitorStatus event) {
    if (!event.equals(previousMonitorEvent)) {
      previousMonitorEvent = event;
      manipulateImages(gps, event.isHasGPS());
      manipulateImages(net, event.isHasNetwork());
      manipulateImages(obd, event.isHasOBD());
      manipulateImages(video, event.isRecording());
    }
  }

  /**
   * Hides the images when the thread is not turned on(for each one) and stops the animation. If the
   * thread is started it starts an animation.
   */
  private void manipulateImages(View view, boolean threadStarted) {
    if (!threadStarted) {
      view.setVisibility(View.VISIBLE);
      view.startAnimation(animation);
    } else {
      if (view.getAnimation() != null) {
        view.clearAnimation();
      }
      view.setVisibility(View.VISIBLE);
    }
  }

  /**
   * Hides and shows the original title bar (status bar)
   */
  public void showAndHideTitle(final View view) {

    final WindowManager.LayoutParams attrs = getWindow().getAttributes();
    view.setVisibility(View.GONE);
    view.postDelayed(new Runnable() {
      @Override
      public void run() {
        view.setVisibility(View.VISIBLE);
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
      }
    }, 8000);
    attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
    getWindow().setAttributes(attrs);
  }
}
