package com.omisoft.hsracer.features.race.fragments.preracesummary;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.common.BaseFragment;

/**
 * Drag Pre Race Summary Fragment
 * Created by Omisoft LLC. on 5/16/17.
 */

public class DragPreRaceSummaryFragment extends BaseFragment {


  private String distance;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_drag_pre_race_summary, container, false);
    TextView mTextView = view.findViewById((R.id.race_distance_sprint_race_pre_summary));
    mTextView.setText(distance);
    return view;
  }

  public void setDistance(String distance) {
    this.distance = distance;
  }

}
