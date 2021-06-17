package com.omisoft.hsracer.features.race.threads;

import static android.content.Context.LOCATION_SERVICE;

import android.location.LocationManager;
import com.omisoft.hsracer.features.race.structures.MonitorStatus;
import com.omisoft.hsracer.features.race.threads.LongMonitoredThread.ThreadState;
import com.omisoft.hsracer.utils.NetworkUtils;
import com.omisoft.hsracer.utils.Utils;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.Setter;
import org.greenrobot.eventbus.EventBus;

/**
 * Monitor thread is used to monitor specific threads and restart them if needed
 * Created by dido on 04.09.17.
 */

public class MonitorThread implements Runnable {

  private Map<String, LongMonitoredThread> threads;
  @Getter
  @Setter
  private volatile boolean runThread;


  public MonitorThread() {
    threads = new ConcurrentHashMap<>();
  }
  @Override
  public void run() {
    runThread = true;
    while (runThread) {
      MonitorStatus monitorStatus = new MonitorStatus();

      monitorStatus.setHasNetwork(NetworkUtils.isConnected());
      monitorStatus.setHasGPS(isGPSRunning());
      monitorStatus.setHasOBD(isOBDRunning());
      monitorStatus.setRecording(true);
      EventBus.getDefault().post(monitorStatus);
      restartThreadsIfNeeded();
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }


    }
  }



  /**
   * Check if OBD is running
   */
  private boolean isOBDRunning() {

    RaceOBDHandlerThread currentRaceOBDHandlerThread = (RaceOBDHandlerThread) threads
        .get(RaceOBDHandlerThread.class.getName());
    return !(currentRaceOBDHandlerThread == null
        || currentRaceOBDHandlerThread.getThreadState() == ThreadState.CRASHED);
  }

  /**
   * Restarts threads if they have crashed
   */
  private void restartThreadsIfNeeded() {
    RaceGPSHandlerThread currentRaceGPSHandlerThread = (RaceGPSHandlerThread) threads
        .get(RaceGPSHandlerThread.class.getName());
    if (currentRaceGPSHandlerThread != null
        && currentRaceGPSHandlerThread.getThreadState() == ThreadState.CRASHED) {
      RaceGPSHandlerThread newThread = new RaceGPSHandlerThread(currentRaceGPSHandlerThread);
      Thread thread = new Thread(newThread);
      thread.start();
      threads.put(RaceGPSHandlerThread.class.getName(), newThread);
    }
    RaceOBDHandlerThread currentRaceOBDHandlerThread = (RaceOBDHandlerThread) threads
        .get(RaceOBDHandlerThread.class.getName());
    if (currentRaceOBDHandlerThread != null
        && currentRaceOBDHandlerThread.getThreadState() == ThreadState.CRASHED) {
      RaceOBDHandlerThread newThread = new RaceOBDHandlerThread(currentRaceOBDHandlerThread);
      Thread thread = new Thread(newThread);
      thread.start();
      threads.put(RaceOBDHandlerThread.class.getName(), newThread);
    }

    RaceWebSocketThread currentWebSocketThread = (RaceWebSocketThread) threads
        .get(RaceWebSocketThread.class.getName());
    if (currentWebSocketThread != null
        && currentWebSocketThread.getThreadState() == ThreadState.CRASHED) {
      RaceWebSocketThread newThread = new RaceWebSocketThread(currentWebSocketThread);
      Thread thread = new Thread(newThread);
      thread.start();
      threads.put(RaceWebSocketThread.class.getName(), newThread);
    }

  }

  /**
   * Check if GPS is running
   * @return
   */
  private boolean isGPSRunning() {
    LocationManager locationManager = (LocationManager) Utils.getApp()
        .getSystemService(LOCATION_SERVICE);

    // getting GPS status
    if (!locationManager
        .isProviderEnabled(LocationManager.GPS_PROVIDER)) {
      return false;
    }

    RaceGPSHandlerThread raceGPSHandlerThread = (RaceGPSHandlerThread) threads
        .get(RaceGPSHandlerThread.class.getName());
    return !(raceGPSHandlerThread != null
        && raceGPSHandlerThread.getThreadState() == ThreadState.CRASHED) && Utils.getBaseApp()
        .isGPSFix();
  }

  /**
   * Add thread to monitor
   * @param name
   * @param longMonitoredThread
   */
  public void addThread(String name, LongMonitoredThread longMonitoredThread) {
    threads.put(name, longMonitoredThread);
  }

  /**
   * Stops all threads
   */
  public void stopAllMonitoredThreads() {
    Iterator<LongMonitoredThread> threadIterator = threads.values().iterator();
    while (threadIterator.hasNext()) {
      threadIterator.next().threadState=ThreadState.FINISHED;
      threadIterator.remove();
    }
  }
}


