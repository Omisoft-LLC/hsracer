package com.omisoft.hsracer.features.home;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.common.BaseFragment;


/**
 * A simple {@link Fragment} subclass. That fragment class inflates the layout in which are
 * visualized the buttons which are navigating the user through the main features
 */
public class HomeFragment extends BaseFragment {


  public HomeFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment

    return inflater.inflate(R.layout.fragment_home, container, false);
  }


}
