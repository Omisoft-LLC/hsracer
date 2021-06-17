package com.omisoft.hsracer.common;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.util.Log;
import com.omisoft.hsracer.common.events.NoopEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Base Fragment
 * Created by dido on 20.06.17.
 */

public class BaseFragment extends Fragment {

  protected String TAG;

  @Override
  public void onStart() {
    super.onStart();
    EventBus.getDefault().register(this);
  }

  public SharedPreferences getSharedPreferences() {
    BaseApp baseApp = (BaseApp) getActivity().getApplication();
    return baseApp.getSharedPreferences();
  }

  @Override
  public void onStop() {
    super.onStop();
    EventBus.getDefault().unregister(this);
  }

  /**
   * Do not delete
   */
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void noopEventListener(NoopEvent event) {
    Log.d(TAG, "NOOP EVENT");
  }
}
