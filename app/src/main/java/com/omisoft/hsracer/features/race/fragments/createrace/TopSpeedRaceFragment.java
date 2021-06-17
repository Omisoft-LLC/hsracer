package com.omisoft.hsracer.features.race.fragments.createrace;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.common.BaseFragment;

/**
 * Created by Omisoft LLC. on 5/16/17.
 */

public class TopSpeedRaceFragment extends BaseFragment {


  public TopSpeedRaceFragment() {

  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      Bundle savedInstanceState) {
    View inflater1 = inflater.inflate(R.layout.fragment_top_speed_race, container, false);
    String metricSystem = getSharedPreferences()
        .getString(getString(R.string.pref_metric_key), getString(R.string.metric_system));
    if (metricSystem.equals(getString(R.string.metric_system))) {
      ((TextView) inflater1.findViewById(R.id.warning_for_units_speed)).setText(R.string.roll_metric_speed);
    } else if (metricSystem.equals(getString(R.string.imperial_system))) {
      ((TextView) inflater1.findViewById(R.id.warning_for_units_speed)).setText(R.string.roll_imperial_speed);
    }
    return inflater1;
  }
}
