package com.omisoft.hsracer.features.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.common.BaseActivity;

/**
 * Settings activity
 * Created by Omisoft LLC. on 5/30/17.
 */

public class SettingsActivity extends BaseActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle(R.string.action_settings);
    getFragmentManager()
        .beginTransaction()
        .replace(android.R.id.content, new SettingsFragment()).commit();
  }
}