package com.omisoft.hsracer.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.Log;

/**
 * Mock Location provider
 * Created by dido on 24.05.17.
 */
public class MockLocationProvider {

  private static final String TAG = MockLocationProvider.class.getName();
  private final Context ctx;

  public MockLocationProvider(Context ctx) {
    this.ctx = ctx;
    try {
      LocationManager lm = (LocationManager) ctx.getSystemService(
          Context.LOCATION_SERVICE);
      lm.addTestProvider(LocationManager.GPS_PROVIDER, false, false, false, false, false,
          true, true, 0, 5);
      lm.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);
    } catch (SecurityException e) {
      Log.wtf(TAG, "MOCK LOCATION PROVIDER NOT AVAILABLE", e);
    }
  }

  @SuppressLint("ObsoleteSdkInt")
  public void pushLocation(double lat, double lon) {
    LocationManager lm = (LocationManager) ctx.getSystemService(
        Context.LOCATION_SERVICE);

    Location mockLocation = new Location(LocationManager.GPS_PROVIDER);
    mockLocation.setLatitude(lat);
    mockLocation.setLongitude(lon);
    mockLocation.setAltitude(0);
    mockLocation.setAccuracy(1f);
    if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1) {
      mockLocation.setElapsedRealtimeNanos(System.nanoTime());
    }
    mockLocation.setTime(System.currentTimeMillis());
    try {
      lm.setTestProviderLocation(LocationManager.GPS_PROVIDER, mockLocation);
    } catch (SecurityException e) {
      Log.wtf(TAG, "MOCK GPS PROVIDER NOT AVAILABLE ");
    }
  }

  public void shutdown() {
    LocationManager lm = (LocationManager) ctx.getSystemService(
        Context.LOCATION_SERVICE);
    lm.removeTestProvider(LocationManager.GPS_PROVIDER);
  }
}