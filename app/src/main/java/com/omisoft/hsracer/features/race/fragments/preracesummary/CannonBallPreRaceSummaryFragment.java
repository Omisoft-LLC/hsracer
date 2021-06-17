package com.omisoft.hsracer.features.race.fragments.preracesummary;


import static com.omisoft.hsracer.constants.Constants.CONSTANT_LATITUDE;
import static com.omisoft.hsracer.constants.Constants.CONSTANT_LONGITUDE;
import static com.omisoft.hsracer.constants.Constants.DISPLAYED_LOCATION;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.common.BaseFragment;
import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

/**
 * A simple {@link Fragment} subclass.
 */
public class CannonBallPreRaceSummaryFragment extends BaseFragment {


  public CannonBallPreRaceSummaryFragment() {

  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_cannon_ball_pre_race_summary, container, false);
    double latitude = getArguments().getDouble(CONSTANT_LATITUDE);
    double longitude = getArguments().getDouble(CONSTANT_LONGITUDE);
    String raceDestination = getArguments().getString(DISPLAYED_LOCATION);
    MapView mMapView = view.findViewById(R.id.mv_in_pre_race_summary);
    Marker marker = new Marker(mMapView);
    GeoPoint geoPoint = new GeoPoint(latitude, longitude);
    marker.setPosition(geoPoint);
    marker.setTitle(
        getString(R.string.finish_location_with_coordinates, raceDestination, latitude, longitude));
    marker.setAnchor(Marker.ANCHOR_CENTER,
        Marker.ANCHOR_BOTTOM);
    mMapView.getOverlays().add(marker);
    IMapController mapController = mMapView.getController();
    mapController.setZoom(18);
    mapController.setCenter(geoPoint);
    return view;
  }

  @Override
  public void onStop() {
    super.onStop();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }
}
