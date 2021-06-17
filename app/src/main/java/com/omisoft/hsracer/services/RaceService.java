package com.omisoft.hsracer.services;

import static android.graphics.BitmapFactory.decodeResource;
import static com.omisoft.hsracer.features.race.threads.RaceGPSHandlerThread.STOP_GETTING_GPS_DATA;
import static com.omisoft.hsracer.features.race.threads.RaceWebSocketThread.POISON_PILL;
import static com.omisoft.hsracer.utils.ThreadUtils.threadRunning;

import android.Manifest.permission;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.omisoft.hsracer.BuildConfig;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.common.BaseApp;
import com.omisoft.hsracer.common.BaseLocationListener;
import com.omisoft.hsracer.common.events.ErrorEvent;
import com.omisoft.hsracer.common.events.LocationEvent;
import com.omisoft.hsracer.common.events.MockLocationEvent;
import com.omisoft.hsracer.common.events.ServiceCommandEvent;
import com.omisoft.hsracer.common.events.StartRaceEvent;
import com.omisoft.hsracer.common.mocklocation.MockLocationThread;
import com.omisoft.hsracer.constants.Constants;
import com.omisoft.hsracer.exceptions.ServerException;
import com.omisoft.hsracer.features.MainActivity;
import com.omisoft.hsracer.features.buddyfinder.events.BuddyFinderLifeCycleCommandEvent;
import com.omisoft.hsracer.features.buddyfinder.threads.BuddyFinderLifeCycleThread;
import com.omisoft.hsracer.features.race.events.OBDConnectionErrorEvent;
import com.omisoft.hsracer.features.race.events.RaceGPSCommandEvent;
import com.omisoft.hsracer.features.race.events.RaceOBDCommandEvent;
import com.omisoft.hsracer.features.race.events.RaceWebSocketCommandEvent;
import com.omisoft.hsracer.features.race.events.StartReadingDataFromOBDEvent;
import com.omisoft.hsracer.features.race.threads.MonitorThread;
import com.omisoft.hsracer.features.race.threads.OBDTimerTask;
import com.omisoft.hsracer.features.race.threads.RaceDataTimerTask;
import com.omisoft.hsracer.features.race.threads.RaceGPSHandlerThread;
import com.omisoft.hsracer.features.race.threads.RaceOBDHandlerThread;
import com.omisoft.hsracer.features.race.threads.RaceWebSocketThread;
import com.omisoft.hsracer.utils.AndroidUtils;
import com.omisoft.hsracer.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Holds long-running logic for hsracer (WebSocket, GPS, VideoRecording OBD II)
 */
public class RaceService extends Service {

  private static final String TAG = RaceService.class.getName();
  @Getter
  @Setter
  private volatile static boolean SERVICE_IS_STARTED = false;

  public static final int START_WEBSOCKET_ACTION = 1;
  public static final int STOP_WEBSOCKET_ACTION = 2;
  public static final int START_GPS_ACTION = 3;
  public static final int STOP_GPS_ACTION = 4;
  public static final int START_BUDDY_FINDER_ACTION = 7;
  public static final int STOP_BUDDY_FINDER_ACTION = 8;
  public static final int START_BUDDY_FINDER_LIFECYCLE_ACTION = 9;
  public static final int STOP_BUDDY_FINDER_LIFECYCLE_ACTION = 10;
  public static final int STOP_SENSORS = 11;
  public static final int START_OBD_ACTION = 12;
  public static final int STOP_OBD_ACTION = 13;
  public static final int STOP_OBD_TIMER_TASK = 14;
  public static final int START_TIMER_THREAD = 15;
  public static final String START_SERVICE = "0";
  // Timer tasks' refresh rates
  private static final long OBD_TIMER_REFRESH_RATE = 500L;
  private static final int GPS_TIMER_REFRESH_RATE = 1000;

  private RaceWebSocketThread raceWebSocketThread;
  private RaceGPSHandlerThread raceGpsThread;
  private BuddyFinderLifeCycleThread buddyFinderLifeCycleThread;
  private Thread wsthread;
  private Thread gpsThread;
  private Thread bflThread;
  private Thread obdThread;

  private RaceOBDHandlerThread raceOBDHandlerThread;
  private OBDTimerTask obdTimerTask;
  private MockLocationThread mockLocationThread;
  private RaceDataTimerTask raceDataTimerTask;
  private MonitorThread monitor;

  /**
   * Sets a notification for the application so the user can navigate back to the last visible
   * activity. Also here are initialized the location listeners.
   */
  @Override
  public void onCreate() {
    super.onCreate();
    EventBus.getDefault().register(this);
    BaseApp baseApp = (BaseApp) getApplication();
    baseApp.clearRaceSharedPreferences();
    PendingIntent intent = BaseApp.prepareIntentForNotification(this, MainActivity.class);
    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
    notificationBuilder.setContentTitle(getString(R.string.app_name))
        .setContentText(getString(R.string.click_to_open))
        .setSmallIcon(R.drawable.ic_stat_name)
        .setOngoing(true)
        .setChannelId("42")
        .setLargeIcon(decodeResource(getResources(), R.drawable.ic_logo))
        .setContentIntent(intent);
    Notification notification = notificationBuilder.build();
    //    mNotificationManager.notify(BaseApp.NOTIFICATION_ID, notification);
    startForeground(BaseApp.NOTIFICATION_ID, notification);
    // code moved from start service
    try {
      if (baseApp.hasPermission(permission.ACCESS_COARSE_LOCATION) && baseApp
          .hasPermission(permission.ACCESS_FINE_LOCATION)) {
        LocationListener listenerGPS = new BaseLocationListener();
        LocationManager locationManager = (LocationManager) getSystemService(
            Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, listenerGPS);
        locationManager.addGpsStatusListener((Listener) listenerGPS);
        SERVICE_IS_STARTED = true;
        monitor = new MonitorThread();
        Thread thread = new Thread(monitor);
        thread.start();
      } else {
        SERVICE_IS_STARTED = false;
        EventBus.getDefault()
            .post(new ErrorEvent(R.string.error_permissions, this.getClass().getSimpleName()));
        stopSelf();
        Log.e(TAG, "Does not have permissions to start service");

      }
    } catch (SecurityException e) {
      Log.e(TAG, "Sec error starting service", e);
      SERVICE_IS_STARTED = false;
      EventBus.getDefault()
          .post(new ErrorEvent(R.string.gps_not_enable, this.getClass().getSimpleName()));
      stopSelf();
    }
  }

  @Override
  public boolean onUnbind(Intent intent) {
    return true;
  }

  @Override
  public IBinder onBind(Intent intent) {
    throw new UnsupportedOperationException("Not  implemented");
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.wtf(TAG, "CALLING ON START COMMAND");
    return START_STICKY;
  }

  /**
   * Handle commands from {@link com.omisoft.hsracer.features.race.RaceActivity}. These commands are
   * based on stopping sensors and the threads controlling them.
   */
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void handleServiceCommand(ServiceCommandEvent event) throws InterruptedException {
    Message message = event.getMessage();
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

    switch (message.what) {
      case START_WEBSOCKET_ACTION:
        //TODO Protect this state
        try {
          raceWebSocketThread = new RaceWebSocketThread();
          // Add to monitor
          monitor.addThread(RaceWebSocketThread.class.getName(), raceWebSocketThread);

          wsthread = new Thread(raceWebSocketThread);
          wsthread.setPriority(2);
          wsthread.start();


        } catch (Exception e) {
          Log.wtf(TAG, "ERROR_WEBSOCKET_ACTION", e);
          EventBus.getDefault().post(new ServerException(e));
        }
        break;
      case START_GPS_ACTION:
        if (!threadRunning(gpsThread)) {
          raceGpsThread = new RaceGPSHandlerThread();
          monitor.addThread(RaceGPSHandlerThread.class.getName(), raceGpsThread);

          gpsThread = new Thread(raceGpsThread);
          gpsThread.setPriority(6);
          gpsThread.start();
        }
        break;
      case START_OBD_ACTION:
        if (!threadRunning(obdThread)) {
          raceOBDHandlerThread = new RaceOBDHandlerThread();
          monitor.addThread(RaceOBDHandlerThread.class.getName(), raceOBDHandlerThread);

          obdThread = new Thread(raceOBDHandlerThread);
          obdThread.setPriority(4);
          obdThread.start();
        }
        break;

      case START_BUDDY_FINDER_LIFECYCLE_ACTION: {
        if (!threadRunning(bflThread)) {
          buddyFinderLifeCycleThread = new BuddyFinderLifeCycleThread();
          bflThread = new Thread(buddyFinderLifeCycleThread);
          bflThread.setPriority(6);
          bflThread.start();
        }
        break;
      }
      case STOP_BUDDY_FINDER_LIFECYCLE_ACTION: {
        if (threadRunning(bflThread)) {
          bflThread.interrupt();
        }
        break;
      }
      case STOP_GPS_ACTION: {
        if (threadRunning(gpsThread)) {
          Message poisonPill = Message.obtain();
          poisonPill.what = STOP_GETTING_GPS_DATA;
          try {
            raceGpsThread.getCommandQueue().put(poisonPill);
          } catch (InterruptedException e) {
            Log.e(TAG, "Error stopping GPS Thread", e);
          }
        }
        break;
      }
      case STOP_OBD_ACTION:

        stopDataFromOBD();

        break;

      case STOP_WEBSOCKET_ACTION: {
        if (threadRunning(wsthread)) {
          Message poisonPill = Message.obtain();
          poisonPill.what = POISON_PILL;
          try {
            raceWebSocketThread.getCommandQueue().put(poisonPill);
          } catch (InterruptedException e) {
            Log.e(TAG, "Error stopping WebSocket Thread", e);
          }
        }
        break;
      }
      case STOP_SENSORS: {
        if (raceDataTimerTask != null && raceDataTimerTask.isRunning()) {
          raceDataTimerTask.setRunning(false);
        }
        Message poisonPill = Message.obtain();
        poisonPill.what = STOP_GETTING_GPS_DATA;
        raceGpsThread.getCommandQueue().put(poisonPill);

        if (threadRunning(bflThread)) {
          bflThread.interrupt();
        }
        stopDataFromOBD();
        disableMockLocationThread();
        break;
      }
      case STOP_OBD_TIMER_TASK:
        stopDataFromOBD();
        break;
    }
  }

  /**
   * Accepts commands to websocket and puts them into blocking queue
   */

  @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
  public void webSocketCommandEvent(RaceWebSocketCommandEvent event) {
    Message inputMessage = event.getMessage();

    try {
      if (raceWebSocketThread != null) {
        raceWebSocketThread.getCommandQueue().put(inputMessage);
      }
    } catch (InterruptedException e) {
      Log.wtf(TAG, "Interrupt Exception", e);
    }
  }

  /**
   * Recieves location updates
   */
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void locationEventListener(LocationEvent event) throws InterruptedException {
    if (threadRunning(gpsThread)) {
      Message message = Message.obtain();
      message.what = RaceGPSHandlerThread.LOCATION_DATA;
      message.getData().putParcelable(Location.class.getName(), event.getLocation());
      raceGpsThread.getCommandQueue().put(message);
    }
  }

  /**
   * Accepts commands to GPS thread
   */
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void gpsCommandEvent(RaceGPSCommandEvent event) {
    Message inputMessage = event.getMessage();
    try {
      raceGpsThread.getCommandQueue().put(inputMessage);
    } catch (InterruptedException e) {
      Log.wtf(TAG, "Interrupt Exception", e);
    }
  }


  /**
   * Accepts commands to OBD thread
   */
  @Deprecated
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void OBDCommandEvent(RaceOBDCommandEvent event) {
    Message inputMessage = event.getMessage();
    try {
      raceOBDHandlerThread.getCommandQueue().put(inputMessage);
    } catch (InterruptedException e) {
      Log.wtf(TAG, "Interrupt Exception", e);
    }
  }

  /**
   * Event to stop threads in case of connection error
   */
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void OBDErrorEvent(OBDConnectionErrorEvent event) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    Message inputMessage = event.getMessage();
    if (threadRunning(obdThread)) {

      stopDataFromOBD();
    }
  }

  /**
   * Accept commands from {@link com.omisoft.hsracer.features.buddyfinder.features.BuddyFinderActivity}
   * and place them in a queue
   */
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void buddyFinderLifeCycleCommandEvent(BuddyFinderLifeCycleCommandEvent event) {
    Message inputMessage = event.getMessage();
    try {
      buddyFinderLifeCycleThread.getCommandQueue().put(inputMessage);
    } catch (InterruptedException e) {
      Log.wtf(TAG, "Interrupt Exception", e);
    }
  }

  /**
   * Starts a thread in which is pushed all the information from the race into queue and pushed to
   * the websocket which n his place is transferring the info to the server.
   */
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void startRace(StartRaceEvent event) {
    Utils.getBaseApp().getRaceDataManager().clear();
    raceDataTimerTask = new RaceDataTimerTask();
    Thread thread = new Thread(raceDataTimerTask);
    thread.start();
  }

  /**
   * Called from showGO method in {@link com.omisoft.hsracer.features.race.RaceActivity}. Its
   * starting thread in which the information for the OBD is shown in RaceActivity.
   */
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void readDataFromOBD(StartReadingDataFromOBDEvent event) {
    obdTimerTask = new OBDTimerTask(raceOBDHandlerThread.getCommandQueue());
    Thread thread = new Thread(obdTimerTask);
    thread.start();
  }

  /**
   * Stops the information that is coming from OBD
   */
  private void stopDataFromOBD() {
    if (obdTimerTask != null) {
      obdTimerTask.getCommandQueue().clear();
      obdTimerTask.setRunning(false);
    }
  }

  @Override
  public void onTaskRemoved(Intent intent) {
    stopSelf();
  }

  @Override
  public void onDestroy() {
    Log.wtf("EVENTBUS", "EVENT BUS IN SERVICE WAS UNREGISTERED");
    EventBus.getDefault().unregister(this);
    SERVICE_IS_STARTED = false;
    monitor.stopAllMonitoredThreads();
    // End monitor thread
    monitor.setRunThread(false);
    if (threadRunning(obdThread)) {
      obdThread.interrupt();
    }

  }


  /**
   * method used in RaceService class, checkAndEnableMockThread method
   */

  private void enableAndStartMockLocationThread() {
    if (!threadRunning(mockLocationThread)) {
      mockLocationThread = new MockLocationThread(getApplication());
      mockLocationThread.start();
    }
  }

  /**
   * method used in RaceService class, handleServiceCommand method, case STOP_RACE and
   * in mockLocationEnableDisable method
   */
  private void disableMockLocationThread() {
    if (mockLocationThread != null) {
      mockLocationThread.interrupt();
      mockLocationThread = null;
    }
  }

  /**
   * method used in RaceService class, mockLocationEnableDisable method
   */
  private void checkAndEnableMockThread(SharedPreferences prefs) {
    Log.wtf(TAG, "checkAndEnableMockThread: " + prefs.getBoolean(Constants.MOCK_LOCATION, false));
    if (prefs.getBoolean(Constants.MOCK_LOCATION, false) && !AndroidUtils.isEmulator()
        && BuildConfig.DEBUG && AndroidUtils.isMockSettingsON(this) && AndroidUtils
        .areThereMockPermissionApps(this)) {
      enableAndStartMockLocationThread();
    }
  }

  /**
   * eventbus gets events from {@link com.omisoft.hsracer.features.home.HomeActivity} isMockDisabled
   * method and from RaceActivity, onCreate, isMockEnabled method
   */
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void mockLocationEnableDisable(MockLocationEvent event) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    switch (event.getMessage()) {
      case 'e':
        checkAndEnableMockThread(prefs);
        break;
      case 'd':
        disableMockLocationThread();
        break;
    }
  }
}
