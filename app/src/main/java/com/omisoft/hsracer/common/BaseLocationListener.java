package com.omisoft.hsracer.common;

import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.SystemClock;
import com.omisoft.hsracer.common.events.LocationEvent;
import com.omisoft.hsracer.utils.Utils;
import org.greenrobot.eventbus.EventBus;

/**
 * Generic location listener, sends  location updates to threads
 * Created by dido on 17.08.17.
 */

public class BaseLocationListener implements LocationListener, GpsStatus.Listener {

  private static final String TAG = BaseLocationListener.class.getSimpleName();
  private long mLastLocationMillis;
  private Location mLastLocation;
  private volatile boolean isGPSFix;


  @Override
  public void onLocationChanged(Location location) {
    if (location != null) {
      mLastLocationMillis = SystemClock.elapsedRealtime();
      mLastLocation = location;

      EventBus.getDefault()
          .post(new LocationEvent(location, this.getClass().getSimpleName()));
    }
  }

  @Override
  public void onStatusChanged(String s, int i, Bundle bundle) {

  }

  @Override
  public void onProviderEnabled(String s) {

  }

  @Override
  public void onProviderDisabled(String s) {

  }

  /**
   * Called to report changes in the GPS status.
   * The event number is one of:
   * <ul>
   * <li> {@link GpsStatus#GPS_EVENT_STARTED}
   * <li> {@link GpsStatus#GPS_EVENT_STOPPED}
   * <li> {@link GpsStatus#GPS_EVENT_FIRST_FIX}
   * <li> {@link GpsStatus#GPS_EVENT_SATELLITE_STATUS}
   * </ul>
   *
   * When this method is called, the client should call
   * {@link android.location.LocationManager#getGpsStatus(GpsStatus)}} to get additional
   * status information.
   *
   * @param event event number for this notification
   */
  @Override
  public void onGpsStatusChanged(int event) {
    switch (event) {
      case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
        if (mLastLocation != null)
        // See https://stackoverflow.com/questions/2021176/how-can-i-check-the-current-status-of-the-gps-receiver
        {
          isGPSFix = (SystemClock.elapsedRealtime() - mLastLocationMillis) < 10500;
        }
        Utils.getBaseApp().setGPSFix(isGPSFix);

        break;
      case GpsStatus.GPS_EVENT_FIRST_FIX:
        // Do something.
        isGPSFix = true;

        break;
    }
  }
}
