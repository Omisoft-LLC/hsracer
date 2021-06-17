package com.omisoft.hsracer.features.race.fragments.preracesummary;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.common.BaseFragment;
import com.omisoft.hsracer.utils.AndroidUtils;
import com.omisoft.hsracer.ws.protocol.dto.JoinedDTO;

/**
 * PreRace top speed fragment
 * Created by Omisoft LLC. on 5/16/17.
 */

public class TopSpeedPreRaceSummaryFragment extends BaseFragment {

  private JoinedDTO joinedDTO;
  private boolean mMetrical;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      Bundle savedInstanceState) {
    this.joinedDTO = getArguments().getParcelable(JoinedDTO.class.getName());
    this.mMetrical = getArguments().getBoolean("mMetrical");
    return inflater.inflate(R.layout.fragment_top_speed_pre_race_summary, container, false);
  }

  @SuppressLint("SetTextI18n")
  @Override
  public void onStart() {
    super.onStart();
    TextView endSpeed = getView().findViewById(R.id.end_speed_pre_summary_race);

    if (endSpeed != null) {
      int endKm = joinedDTO.getEndSpeed();
      int endMiles = AndroidUtils.fromKilometresToMiles(joinedDTO.getEndSpeed());
      if (mMetrical) {
        endSpeed.setText(String.valueOf(endKm) + " " + getString(R.string.roll_metric_speed));
      } else {
        endSpeed.setText(String.valueOf(endMiles) + " " + getString(R.string.roll_imperial_speed));
      }
    }
  }
}
