package com.omisoft.hsracer.common.mocklocation;

import android.content.Context;
import com.omisoft.hsracer.utils.MockLocationProvider;

/**
 * Created by developer on 09.08.17.
 * MockLocationThread used in RaceService
 */

public class MockLocationThread extends Thread {

  private static final String TAG = MockLocationThread.class.getName();
  private final Context context;
  private MockLocationProvider mock;
  private double startLat = 43.8468480;
  private double startLon = 25.9530680;
  private int counter;

  public MockLocationThread(Context context) {
    this.context = context;
    this.counter = 0;
  }

  private void setMock() {
    mock = new MockLocationProvider(context);
    //Set test location
    mock.pushLocation(43.8468480, 25.9530680);
  }

  @Override
  public void run() {
    if (mock == null) {
      setMock();
    }
    while (true) {
      if (mock != null) {
        if (counter < 400) {
          startLat += 0.000004;
          startLon += 0.0001;
        } else {
          startLat += 0.0002;
          startLon += 0.0003;
        }
        try {
          counter++;
          Thread.sleep(100);
        } catch (InterruptedException e) {
          return;
        }
        mock.pushLocation(startLat, startLon);
      }
    }
  }
}