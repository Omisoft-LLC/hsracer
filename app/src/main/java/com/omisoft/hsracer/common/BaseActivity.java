package com.omisoft.hsracer.common;


import static com.omisoft.hsracer.constants.Constants.FRAGMENT_TAG;
import static com.omisoft.hsracer.services.RaceService.STOP_WEBSOCKET_ACTION;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.CamcorderProfile;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import butterknife.ButterKnife;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.common.events.ErrorEvent;
import com.omisoft.hsracer.common.events.NoopEvent;
import com.omisoft.hsracer.common.events.ProgressBarEvent;
import com.omisoft.hsracer.common.events.ServiceCommandEvent;
import com.omisoft.hsracer.common.utils.AuthorizationInterceptor;
import com.omisoft.hsracer.constants.Constants;
import com.omisoft.hsracer.db.AppDatabase;
import com.omisoft.hsracer.exceptions.ServerException;
import com.omisoft.hsracer.features.MainActivity;
import com.omisoft.hsracer.features.buddyfinder.events.BuddyFinderWebSocketCommandEvent;
import com.omisoft.hsracer.features.login.LoginActivity;
import com.omisoft.hsracer.features.race.events.OBDConnectionErrorEvent;
import com.omisoft.hsracer.features.race.events.RaceGPSCommandEvent;
import com.omisoft.hsracer.features.race.events.RaceWebSocketCommandEvent;
import com.omisoft.hsracer.services.RaceService;
import com.omisoft.hsracer.utils.AndroidUtils;
import com.omisoft.hsracer.utils.Utils;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import okhttp3.OkHttpClient;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Base Activity
 * Created by dido on 23.05.17.
 */


public abstract class BaseActivity extends AppCompatActivity {

  public static final int GPS_PERMISSION_REQUEST = 1;
  public static final int CAMERA_PERMISSION_REQUEST = 2;
  private static final String DATE_PATTERN = "MM_dd_yyyy_hh_mm_ss";
  protected FirebaseAnalytics mFirebaseAnalytics;
  protected BroadcastReceiver broadcastReceiver;
  protected String TAG;
  private static final int DIVIDER_FOR_KM = 1000;
  private static final int DIVIDER_FOR_ML = 5280;
  private static final String DECIMAL_FORMAT_METRIC = "###.#";
  private static final String DECIMAL_FORMAT_IMPERIAL = "###.##";
  private static final DecimalFormat DFM = new DecimalFormat(DECIMAL_FORMAT_METRIC);
  private static final DecimalFormat DFI = new DecimalFormat(DECIMAL_FORMAT_IMPERIAL);

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    TAG = this.getClass().getSimpleName();
    mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getApp()));
    broadcastReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (Objects.equals(action, BluetoothAdapter.ACTION_STATE_CHANGED)) {
          final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
              BluetoothAdapter.ERROR);
          switch (state) {
            case BluetoothAdapter.STATE_OFF:
              break;
            case BluetoothAdapter.STATE_TURNING_OFF:
              stopOBD();
              break;
            case BluetoothAdapter.STATE_ON:
              if (AndroidUtils.isOBDReadReady(getSharedPreferences())) {
                startOBD();
              }
              break;
            case BluetoothAdapter.STATE_TURNING_ON:
              break;
          }
        }
      }
    };
  }

  static {
    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
  }

  @Override
  public void setContentView(int layoutResID) {
    super.setContentView(layoutResID);
    ButterKnife.bind(this);
  }

  /**
   * Get User Email
   */
  public String getUserEmail() {
    SharedPreferences sharedPreferences = getSharedPreferences();
    return sharedPreferences.getString(Constants.EMAIL, "");
  }

  /**
   * Get Shared preferences
   */
  public SharedPreferences getSharedPreferences() {
    return PreferenceManager.getDefaultSharedPreferences(this);
  }

  public String getAuthId() {
    return getApp().getAuthId();
  }


  /**
   * Shows toast message
   */
  public void showWhiteToastMessage(String message) {
    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
  }


  public void showWhiteToastMessage(int resourceId) {
    showWhiteToastMessage(getString(resourceId));
  }

  /**
   * Show notification
   *
   * @param title Title
   * @param content Content
   */
  public void showNotification(String title, String content) {
    NotificationCompat.Builder mBuilder =
        new NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle(title)
            .setContentText(content);
    NotificationManager mNotificationManager =
        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    mNotificationManager.notify(1, mBuilder.build());

  }

  /**
   * Show notification
   *
   * @param title Title
   * @param content Content
   */
  public void showNotification(String title, String content, PendingIntent intent) {
    NotificationCompat.Builder mBuilder =
        new NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle(title)
            .setContentText(content).setContentIntent(intent);
    NotificationManager mNotificationManager =
        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    mNotificationManager.notify(BaseApp.NOTIFICATION_ID, mBuilder.build());

  }

  public BaseApp getApp() {
    return (BaseApp) getApplication();
  }

  /**
   * Gets objectMapper. Use where JSON is required
   */
  public ObjectMapper getObjectMapper() {
    return getApp().getObjectMapper();
  }

  /**
   * Gets HTTP Client
   */
  public OkHttpClient getHttpClient() {
    return getApp().getHttpClient();
  }

  /**
   * Get Executor thread service
   */
  public ExecutorService getExecutor() {
    return getApp().getExecutor();
  }

  /**
   * Get eventbus service
   */
  public EventBus getEventBus() {
    return getApp().getEventBus();
  }

  /**
   * Gets main DB
   */
  public AppDatabase getDB() {
    return getApp().getDB();
  }


  @Override
  public void onStart() {
    super.onStart();
    EventBus.getDefault().register(this);
    registerReceiver();
  }

  @Override
  public void onStop() {
    super.onStop();
    EventBus.getDefault().unregister(this);
    unregisterReceiver();
  }

  /**
   * Do not delete
   */
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void noopEventListener(NoopEvent event) {
    Log.d(TAG, "NOOP EVENT");

  }

  /**
   * On server exception handler
   */
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onServerException(ServerException event) {
    if (event.getErrorDTO().getErrorCode() == 401) {
      Intent startIntent = new Intent(this, MainActivity.class);
      startActivity(startIntent);
    } else {
      showWhiteToastMessage(event.getErrorDTO().getDetailedMessage());
    }
  }

  /**
   * Removes progress bar
   */
  protected void removeProgressIndicator() {
    View progressBarView = findViewById(R.id.progress_bar);
    if (progressBarView != null) {
      progressBarView.setVisibility(View.GONE);

    }
  }

  protected void showProgressIndicator() {
    View progressBarView = findViewById(R.id.progress_bar);
    if (progressBarView != null) {
      progressBarView.setVisibility(View.VISIBLE);

    }
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void togglerogressIndicator(ProgressBarEvent e) {
    View progressBarView = findViewById(R.id.progress_bar);
    if (progressBarView != null) {
      switch (e.getVisibility()) {
        case View.GONE:
          removeProgressIndicator();
          break;
        case View.VISIBLE:
          showProgressIndicator();
          break;
        case View.INVISIBLE:
          removeProgressIndicator();
          break;
      }
    }
  }


  @Subscribe(threadMode = ThreadMode.MAIN)
  public void errorEventListener(ErrorEvent event) {
    removeProgressIndicator();
    showWhiteToastMessage(getString(event.getStringResource()));
    if (event.getFrom().equals(AuthorizationInterceptor.class.getName())) {
      Intent intent = new Intent(this, LoginActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
      startActivity(intent);

    }
  }


  /**
   * Send command to main race web socket thread
   */
  protected void sendCommandMessageToRaceWebSocketThread(Bundle bundle, int command) {
    Message message = Message.obtain();
    message.what = command;
    message.setData(bundle);
    EventBus.getDefault().postSticky(new RaceWebSocketCommandEvent(message));
    Log.wtf(TAG, "POST EVENT TO WEBSOCKET SENT");
  }

  /**
   * Send command to GPS thread
   */
  protected void sendCommandMessageToGPSThread(Bundle bundle, int command) {
    Message message = Message.obtain();
    message.what = command;
    message.setData(bundle);
    RaceGPSCommandEvent event = new RaceGPSCommandEvent(message);
    getEventBus().post(event);

  }

  /**
   * Send control command to DF web socket
   */
  protected void sendCommandMessageToBFWebSocketThread(Bundle bundle, int command) {
    Message message = Message.obtain();
    message.setData(bundle);
    message.what = command;
    EventBus.getDefault().post(new BuddyFinderWebSocketCommandEvent(message));
  }

  protected void sendCommandMessageToWebSocketThreadToStopThread() {
    Message message = Message.obtain();
    message.what = STOP_WEBSOCKET_ACTION;
    EventBus.getDefault().post(new ServiceCommandEvent(message));
  }


  /**
   * Function to show settings alert dialog
   */
  public void showAlertGPS() {
    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
    alertDialog.setTitle(getResources().getString(R.string.must_grant_gps));
    alertDialog.setMessage(getResources().getString(R.string.gps_not_enable));
    alertDialog.setPositiveButton(getResources().getString(R.string.settings),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
          }
        });
    alertDialog.setNegativeButton(getResources().getString(R.string.cancel),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
          }
        });

    alertDialog.show();

    //TODO: Remove that dialog because its leaking when its shown and screen is rotated
  }

  protected void registerReceiver() {
    IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
    registerReceiver(broadcastReceiver, filter);
  }

  protected void unregisterReceiver() {
    try {
      unregisterReceiver(broadcastReceiver);
    } catch (RuntimeException e) {
      Log.wtf(TAG, "UNREGISTERED RECEIVER", e);
    }
  }

  private void startOBD() {
    Message m = Message.obtain();
    m.what = RaceService.START_OBD_ACTION;
    EventBus.getDefault().post(new ServiceCommandEvent(m));
  }

  private void stopOBD() {
    Message OBDError = Message.obtain();
    /* Eventbus posts to RaceService class, OBDErrorEvent method */
    EventBus.getDefault().post(new OBDConnectionErrorEvent(OBDError));
  }


  public String distanceConverter(boolean metrical, int gpsDistance) {
    if (metrical) {
      return (gpsDistance < DIVIDER_FOR_KM ? getString(R.string.distance_var, gpsDistance)
          : getString(R.string.distance_km_var,
              String.valueOf(DFM.format(gpsDistance / DIVIDER_FOR_KM))));

    } else {
      return (gpsDistance < DIVIDER_FOR_ML ? getString(R.string.distance_var_imperial,
          AndroidUtils.fromMeterToFeet(gpsDistance))
          : getString(R.string.distance_miles_var_imperial,
              String.valueOf(
                  DFI.format(AndroidUtils.fromMeterToFeet(gpsDistance) / DIVIDER_FOR_ML))));
    }
  }

  /**
   * Start race service
   */
  protected void startRaceService() {
    if (!RaceService.isSERVICE_IS_STARTED()) {
      Intent intent = new Intent(this, RaceService.class);
      intent.setAction(RaceService.START_SERVICE);
      startService(intent);
      RaceService.setSERVICE_IS_STARTED(true);
    }
  }


  /**
   * Hides soft keyboard on click if field is not focusable
   */
  public void hideSoftKeyboard() {
    if (getCurrentFocus() != null) {
      InputMethodManager inputMethodManager =
          (InputMethodManager) getSystemService(
              Activity.INPUT_METHOD_SERVICE);
      if (inputMethodManager != null) {
        inputMethodManager.hideSoftInputFromWindow(
            getCurrentFocus().getWindowToken(), 0);
      }
    }
  }
  //ENABLE IF NEEDED
//  @Override
//  public boolean dispatchTouchEvent(MotionEvent ev) {
//    if (getCurrentFocus() != null) {
//      InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//      imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
//    }
//    return super.dispatchTouchEvent(ev);
//  }


  /**
   * Replaces the type of fragment
   */
  protected void replaceFragment(int fragmentId, Fragment fragment) {
    FragmentTransaction transaction = getFragmentManager().beginTransaction();
    transaction.replace(fragmentId, fragment, FRAGMENT_TAG);
    transaction.commit();
  }

  public String getCurrentTime() {
    Date date = Calendar.getInstance().getTime();
    SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN);
    return format.format(date);
  }


  /**
   * Gets the video quality from the options located in the drawer->settings of the application
   */
  public int getVideoQuality() {
    BaseApp context = Utils.getBaseApp();
    String quality = context.getSharedPreferences().getString(Constants.RESOLUTION_KEY, "");
    if (quality.equalsIgnoreCase("4k")) {
      if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
        return CamcorderProfile.QUALITY_2160P;
      } else {
        return CamcorderProfile.QUALITY_1080P;
      }
    } else if (quality.equalsIgnoreCase("1080p")) {
      return CamcorderProfile.QUALITY_1080P;
    } else if (quality.equalsIgnoreCase("720p")) {
      return CamcorderProfile.QUALITY_720P;
    } else if (quality.equalsIgnoreCase("480p")) {
      return CamcorderProfile.QUALITY_480P;
    } else if (quality.equalsIgnoreCase("144p")) {
      return CamcorderProfile.QUALITY_LOW;
    } else {
      return CamcorderProfile.QUALITY_HIGH;
    }
  }

  @SuppressLint("InlinedApi")
  protected void hideSystemUi(View view) {
    view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
        | View.SYSTEM_UI_FLAG_FULLSCREEN
        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
  }

}
