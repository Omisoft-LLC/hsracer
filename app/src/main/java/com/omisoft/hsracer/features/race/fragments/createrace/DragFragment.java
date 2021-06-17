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
 * Drag Race Fragment
 * Created by Omisoft LLC. on 5/16/17.
 */

public class DragFragment extends BaseFragment {

  public DragFragment() {

  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View inflater1 = inflater.inflate(R.layout.fragment_drag, container, false);
    String metricSystem = getSharedPreferences()
        .getString(getString(R.string.pref_metric_key), getString(R.string.metric_system));
    if (metricSystem.equals(getString(R.string.metric_system))) {
      ((TextView) inflater1.findViewById(R.id.warning_for_units_drag))
          .setText(R.string.set_distance_meters);
    } else if (metricSystem.equals(getString(R.string.imperial_system))) {
      ((TextView) inflater1.findViewById(R.id.warning_for_units_drag))
          .setText(R.string.set_distance_foots);
    }
    return inflater1;
  }
}
