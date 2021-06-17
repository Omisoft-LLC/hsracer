package com.omisoft.hsracer.features.race.fragments.createrace;

import static com.omisoft.hsracer.features.race.CreateRaceActivity.CANNONBALL_MAP_OK;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.common.BaseFragment;
import com.omisoft.hsracer.features.race.CannonballMapActivity;

/**
 * Cannonball fragment
 * Created by Omisoft LLC. on 5/16/17.
 */

public class CannonballFragment extends BaseFragment {


  public CannonballFragment() {

  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_cannonball_race, container, false);
    ButterKnife.bind(this, view);

    return view;
  }

  @OnClick(R.id.select_cannonball_btn)
  public void cannonballBtnClickListener(View view) {
    Intent navIntent = new Intent(getActivity(), CannonballMapActivity.class);
    getActivity().startActivityForResult(navIntent, CANNONBALL_MAP_OK);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    Log.wtf(TAG, "onActivityResult: FRAGMENT");
  }
}
