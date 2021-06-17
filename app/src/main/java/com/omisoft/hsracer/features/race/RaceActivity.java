package com.omisoft.hsracer.features.race;


import static com.omisoft.hsracer.constants.Constants.ONE_HOUR;
import static com.omisoft.hsracer.constants.Constants.ONE_MINUTE;
import static com.omisoft.hsracer.constants.Constants.ONE_SECOND;
import static com.omisoft.hsracer.features.race.RaceActivity.CameraPreview.INIT_AND_TURN_ON_CAMERA_AND_MEDIA;
import static com.omisoft.hsracer.features.race.RaceActivity.CameraPreview.INIT_FIRST_TIME_CAMERA;
import static com.omisoft.hsracer.features.race.RaceActivity.CameraPreview.RELEASE_CAMERA_AND_MEDIA;
import static com.omisoft.hsracer.features.race.RaceActivity.CameraPreview.TURN_OFF_THREAD;
import static com.omisoft.hsracer.features.race.RaceActivity.CameraPreview.TURN_ON_CAMERA;
import static com.omisoft.hsracer.features.race.threads.RaceGPSHandlerThread.START_GETTING_GPS_DATA;
import static com.omisoft.hsracer.features.race.threads.RaceGPSHandlerThread.STOP_GETTING_GPS_DATA;
import static com.omisoft.hsracer.features.race.threads.RaceGPSHandlerThread.STOP_GPS;
import static com.omisoft.hsracer.features.race.threads.RaceWebSocketThread.CANCEL;
import static com.omisoft.hsracer.features.race.threads.RaceWebSocketThread.CANCEL_RACE;
import static com.omisoft.hsracer.features.race.threads.RaceWebSocketThread.FINISH_RACE;
import static com.omisoft.hsracer.features.race.threads.RaceWebSocketThread.START_RACE;
import static com.omisoft.hsracer.services.RaceService.STOP_SENSORS;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import com.omisoft.hsracer.BuildConfig;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.common.BaseApp;
import com.omisoft.hsracer.common.BaseApp.Permissions;
import com.omisoft.hsracer.common.BaseFullScreenActivity;
import com.omisoft.hsracer.common.events.ErrorEvent;
import com.omisoft.hsracer.common.events.MockLocationEvent;
import com.omisoft.hsracer.common.events.RaceDataEvent;
import com.omisoft.hsracer.common.events.ServiceCommandEvent;
import com.omisoft.hsracer.common.events.StartRaceEvent;
import com.omisoft.hsracer.constants.Constants;
import com.omisoft.hsracer.features.home.HomeActivity;
import com.omisoft.hsracer.features.race.events.RaceGPSEvent;
import com.omisoft.hsracer.features.race.events.RaceWebSocketEvent;
import com.omisoft.hsracer.features.race.events.StartReadingDataFromOBDEvent;
import com.omisoft.hsracer.features.race.structures.RaceDataDTO;
import com.omisoft.hsracer.features.race.threads.RaceGPSHandlerThread;
import com.omisoft.hsracer.utils.AndroidUtils;
import com.omisoft.hsracer.utils.CryptoUtils;
import com.omisoft.hsracer.utils.Utils;
import com.omisoft.hsracer.ws.protocol.dto.JoinedDTO;
import com.omisoft.hsracer.ws.protocol.dto.RaceStatusDTO;
import com.omisoft.hsracer.ws.protocol.dto.RacerSummary;
import com.omisoft.hsracer.ws.protocol.enums.RaceStatus;
import com.omisoft.hsracer.ws.protocol.enums.RaceType;
import com.yakivmospan.scytale.Store;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.Getter;
import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacpp.avutil;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;

/**
 * This is the race screen visible during racing
 * Created by Omisoft LLC. on 5/4/17.
 */

//TODO remove all editors for shared preferences in one method, so they can be edited with one method
public class RaceActivity extends BaseFullScreenActivity {

  private static final float START_SIZE = 100;
  private static final float END_SIZE = 200;
  private static final int ANIMATION_DURATION = 1000;
  private static final int COUNTDOWN_SECONDS = 5 * 1000;
  private static final int LEAVE_AFTER_GO_CREATOR_AND_RACER = 1;
  private static final int LEAVE_BEFORE_GO = 2;
  private static final int LEAVE_SINGLE_RACER = 3;
  private static final int CANNONBALL_OVERLAY_INDEX = 0;
  private static final int RACERS_OVERLAY_INDEX = 1;
  // TODO move to strings.xml
  private static final MessageFormat RACE_STATUS_FORMAT = new MessageFormat(
      "{0}. {1} - current speed {2}\n");
  private static final String STATUS_UPDATE_KEY = "STATUS_UPDATE_KEY";

  //  @BindView(R.id.frame_layout_race)
//  SurfaceView mSurfaceView;
  @BindView(R.id.map_in_race_activity)
  MapView mMapView;
  @BindView(R.id.map_panel_in_race_activity)
  RelativeLayout mMapPanel;
  @BindView(R.id.camera_panel_in_race_activity)
  RelativeLayout mCameraPanel;
  @BindView(R.id.image_show_map)
  ImageView mShowMap;
  @BindView(R.id.image_show_camera)
  ImageView cameraImageView;
  @BindView(R.id.count_down_text_view)
  TextView mCountDownTextView;
  @BindView(R.id.current_speed_text_view)
  TextView mCurrentSpeedTextView;
  @BindView(R.id.rpm_info)
  TextView mRpmInfo;
  @BindView(R.id.rpm_text_view)
  TextView mRpmTextView;
  @BindView(R.id.engine_temperature_info)
  TextView mEngineTempInfo;
  @BindView(R.id.engine_temperature_text_view)
  TextView mEngineTempTextView;
  @BindView(R.id.current_obd_speed_info)
  TextView mOBDSpeedInfo;
  @BindView(R.id.current_obd_speed_text_view)
  TextView mOBDSpeedTextView;
  @BindView(R.id.distance_text_view)
  TextView mDistanceTextView;
  @BindView(R.id.current_time_text_view)
  Chronometer mCurrentTimeChronometer;
  @BindView(R.id.acceleration_text_view)
  TextView mAccelerationTextView;
  @BindView(R.id.race_status_messages)
  TextView mRaceStatusMessages;
  @BindView(R.id.start_race_button)
  Button mStartRace;

  private String rtmpURL;


  private ValueAnimator mAnimator;
  private JoinedDTO mJoinedDTO;
  private CountDownTimer mCountDownTimer;
  private CameraPreview mCameraPreview;
  private Timer memoryTaskTimer;
  private TimerTask memoryTask;
  private final int[] mArrayOfDrawables = {R.drawable.map_marker_racer_1,
      R.drawable.map_marker_racer_2,
      R.drawable.map_marker_racer_3, R.drawable.map_marker_racer_5, R.drawable.map_marker_racer_6,
      R.drawable.map_marker_racer_7, R.drawable.map_marker_racer_8};
  private boolean mMetrical;
  private boolean mStartOBDRead;
  private boolean mCounting = false;
  private boolean mRaceStarted = false;
  private boolean mRaceCanceled = false;
  private boolean mRaceFinished = false;
  private Timer mRaceStatusTimer;
  @SuppressLint("HandlerLeak")
  private Handler handler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      Bundle bundle = msg.getData();
      if (mRaceStatusMessages != null) {
        mRaceStatusMessages.setText(bundle.getString(STATUS_UPDATE_KEY));
      }
    }
  };
  private boolean init;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_race);
    if (savedInstanceState == null) {
      mJoinedDTO = getIntent().getParcelableExtra(JoinedDTO.class.getName());

    }
  }


  private void initSurfaceLayout() {

    if (Permissions.Video) {
      prepareMemoryTimerTask();
      Long raceId = getSharedPreferences().getLong(Constants.RACE_ID, 0);
      Store store = new Store(getApplicationContext());
      String storedPass = getApp().getCrypto()
          .decrypt(getSharedPreferences().getString(Constants.PASSWORD, ""),
              store.getAsymmetricKey(BuildConfig.APPLICATION_ID, null));
      String aesKeyEncoded = getSharedPreferences().getString(Constants.AES_KEY, "");
      byte[] key = Base64.decode(aesKeyEncoded, Base64.NO_WRAP);
      SecretKey originalKey = new SecretKeySpec(key, "AES");
      byte[] encrypted = CryptoUtils
          .encrypt(originalKey, storedPass.getBytes(Charset.defaultCharset()));
      try {
        String streamURL =
            BuildConfig.RTMP_URL + getSharedPreferences().getString(Constants.API_KEY, "") + "?r="
                + URLEncoder.encode(String.valueOf(raceId), "utf-8") + "&p=" + URLEncoder
                .encode(Base64.encodeToString(encrypted, Base64.NO_WRAP), "utf-8");
        if (BuildConfig.DEBUG) {
          Log.e(TAG, "STREAM URL");
          Log.e(TAG, streamURL);
        }
        mCameraPreview = new CameraPreview(getApplicationContext(), raceId,
            streamURL, getSharedPreferences().getBoolean(Constants.LIVE_STREAM, false));
        LinearLayout.LayoutParams layoutParam = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        mCameraPanel.addView(mCameraPreview, 0, layoutParam);

      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
      startMemoryTimerTask();

      Log.v(TAG, "added cameraView to mainLayout");
    }
  }


  /**
   * On Resume adds the overlay for the finish position (if a cannonball race is specified), turns
   * off and on the camera, checks for obd and mock location.
   */
  @Override
  protected void onResume() {
    super.onResume();

    if (!init) {
      initSurfaceLayout();
      init = true;
    }
    if (mJoinedDTO.getRaceType() == RaceType.CANNONBALL) {
      Marker marker = new Marker(mMapView);
      GeoPoint geoPoint = new GeoPoint(mJoinedDTO.getFinishLat(), mJoinedDTO.getFinishLng());
      marker.setPosition(geoPoint);
      marker.setTitle(getString(R.string.finish_location, mJoinedDTO.getFinishAddress()));
      marker.setAnchor(Marker.ANCHOR_CENTER,
          Marker.ANCHOR_BOTTOM);
      mMapView.getOverlays().add(CANNONBALL_OVERLAY_INDEX, marker);
      mMapView.invalidate();
    }
    if (!mRaceStarted) {
      mStartRace.setText(getString(R.string.go));
      checkWhatSystemIsUsed();
      setStartOBDRead();
      obdLiveDataCheck();
      isMockEnabled();
      init();
      if (Permissions.Video) {
        if (!AndroidUtils.isCameraUsedByApp()) {
          sendMessageToCameraPreviewThread(INIT_FIRST_TIME_CAMERA);
        }
      }

    } else {
      if (mRaceCanceled || mRaceFinished) {
        mStartRace.setVisibility(View.INVISIBLE);
        mRaceStatusMessages.setText(getString(R.string.message_to_canceled_user));
        clearCameraPanel();
      } else {
        if (Permissions.Video) {
          setStartOBDRead();
          obdLiveDataCheck();
          checkWhatSystemIsUsed();
          isMockEnabled();
          mStartRace.setText(getString(R.string.end_race));
          if (!AndroidUtils.isCameraUsedByApp()) {
            sendMessageToCameraPreviewThread(INIT_AND_TURN_ON_CAMERA_AND_MEDIA);
          }
        } else {
          mStartRace.setText(getString(R.string.end_race));
          clearCameraPanel();
        }
      }
    }
  }

  /**
   * Suspends the camera when the power button is pressed
   */
  @Override
  protected void onPause() {
    super.onPause();
    if (Permissions.Video) {
      if (!AndroidUtils.isCameraUsedByApp()) {
        sendMessageToCameraPreviewThread(RELEASE_CAMERA_AND_MEDIA);
      }
    }
  }

  /**
   * Turns off the camera and the memory task
   */
  @Override
  protected void onDestroy() {
    if (Permissions.Video) {
      if (memoryTaskTimer != null) {
        stopMemoryTimerTask();
      }

      sendMessageToCameraPreviewThread(TURN_OFF_THREAD);

    }
    if (mRaceStatusTimer != null) {
      mRaceStatusTimer.cancel();
      mRaceStatusTimer.purge();
    }
    super.onDestroy();
  }


  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {
    savedInstanceState.putParcelable(JoinedDTO.class.getName(), mJoinedDTO);
    savedInstanceState.putBoolean("mMetrical", mMetrical);
    savedInstanceState.putBoolean("mStartOBDRead", mStartOBDRead);
    super.onSaveInstanceState(savedInstanceState);
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    mJoinedDTO = savedInstanceState.getParcelable(JoinedDTO.class.getName());
    mMetrical = savedInstanceState.getBoolean("mMetrical");
    mStartOBDRead = savedInstanceState.getBoolean("mStartOBDRead");
  }

  /**
   * Sends messages to the camera thread
   */
  private void sendMessageToCameraPreviewThread(int messageCase) {
    Message message = Message.obtain();
    message.what = messageCase;

    mCameraPreview.execute(message);

  }

  /**
   * Memory task which checks if the memory on the device is depleted
   */
  private void prepareMemoryTimerTask() {
    memoryTask = new TimerTask() {
      @Override
      public void run() {
        if (AndroidUtils.megabytesAvailable(mCameraPreview.getDir())
            < Constants.MINIMUM_MEMORY_MB) {
          sendMessageToCameraPreviewThread(TURN_OFF_THREAD);
          Toast
              .makeText(RaceActivity.this, getString(R.string.error_low_memory), Toast.LENGTH_LONG)
              .show();
        }
      }
    };
    memoryTaskTimer = new Timer();
  }

  /**
   * Starts the memory task
   */
  private void startMemoryTimerTask() {
    memoryTaskTimer.scheduleAtFixedRate(memoryTask, 0L, 1000L);
  }

  /**
   * Stops the memory task
   */
  public void stopMemoryTimerTask() {
    if (memoryTaskTimer != null) {
      memoryTaskTimer.cancel();
      memoryTaskTimer.purge();
      memoryTaskTimer = null;
    }
  }

  /**
   * Check what system is used so the values from the gps to be displayed and converted in the right
   * way
   */
  private void checkWhatSystemIsUsed() {
    SharedPreferences metricPreferences = getSharedPreferences();
    mMetrical = metricPreferences
        .getString(getString(R.string.pref_metric_key), getString(R.string.metric_system))
        .equals(getString(R.string.metric_system));
  }


  /**
   * Init the camera and all the views that needs configuration from the beginning
   */
  private void init() {
    mCountDownTimer = new CountDownTimer(COUNTDOWN_SECONDS, 1000) {
      public void onTick(long millisUntilFinished) {
        mCountDownTextView.setText(getString(R.string.countdown_var, millisUntilFinished / 1000));
        mAnimator.start();
      }

      public void onFinish() {
        Editor editor = getSharedPreferences().edit();
        editor.putBoolean(Constants.RACE_IS_RUNNING, true);
        editor.apply();
        mCountDownTextView.setText(getResources().getString(R.string.go));
        startRaceSound();
        showGO();
      }
    };
    initStartStopButtons();
  }

  /**
   * Show the button if you are creator
   */
  private void initStartStopButtons() {
    if (getSharedPreferences().getBoolean(Constants.CREATOR, false)) {
      mStartRace.setEnabled(true);
    } else {
      mStartRace.setEnabled(false);
      mStartRace.setVisibility(View.INVISIBLE);
    }
  }

  /**
   * The button that starts the race
   */
  public void startAndStopRace(View view) {
    if (getApp().isConnected()) {
      if (getApp().checkIsGPSEnabled()) {
        if (!mRaceStarted) {

          SharedPreferences.Editor sharedPreferences = getSharedPreferences().edit();
          sharedPreferences.putBoolean("mCounting", true);
          sharedPreferences.apply();

          mStartRace.setVisibility(View.INVISIBLE);
          sendCommandMessageToRaceWebSocketThread(null, START_RACE);
        } else {
          if (mJoinedDTO.getCountRacers() > 1) {
            cancelRace();
            removeTextViewsAndCancelRace();
          } else {
            cancelRace();
          }
        }
      } else {
        showAlertGPS();
      }
    } else {
      showWhiteToastMessage(getString(R.string.no_internet_connection));
    }
  }

  /**
   * Shows the front panel in which is located the camera
   */
  public void buttonShowCamera(View view) {
    mCameraPanel.setVisibility(View.VISIBLE);
  }

  /**
   * Shows the back panel in which is located the map
   */
  public void buttonShowMap(View view) {
    mCameraPanel.setVisibility(View.GONE);
  }

  /**
   * Removes the values displayed on the screen after the race is stopped
   */
  private void removeTextViewsAndCancelRace() {
    mStartRace.setVisibility(View.INVISIBLE);
    mCurrentTimeChronometer.stop();
    mCurrentTimeChronometer.setText("");
    mAccelerationTextView.setText("");
    mDistanceTextView.setText("");
    mRpmTextView.setText("");
    mOBDSpeedTextView.setText("");
    mEngineTempTextView.setText("");
    mCurrentSpeedTextView.setText("");
    mRaceStatusMessages.setText(R.string.message_to_canceled_user);
  }

  /**
   * Finishes the race when the button is clicked or when the distance or the speed is reached
   */
  private void cancelRace() {

    mRaceCanceled = true;

    // TODO RELEASE CAMERA AND PREVIEW
    sendMessageToCameraPreviewThread(TURN_OFF_THREAD);
    sendCommandMessageToGPSThread(null, STOP_GETTING_GPS_DATA);
    sendCommandMessageToRaceWebSocketThread(null, CANCEL);
    Message message = Message.obtain();
    message.what = STOP_SENSORS;
    EventBus.getDefault().post(new ServiceCommandEvent(message));
  }

  /**
   * After the start button is clicked the race is started
   */
  private void startCountDown() {
    mAnimator = ValueAnimator.ofFloat(START_SIZE, END_SIZE);
    mAnimator.setDuration(ANIMATION_DURATION);
    mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator valueAnimator) {
        float animatedValue = (float) valueAnimator.getAnimatedValue();
        mCountDownTextView.setTextSize(animatedValue);
      }
    });
    mAnimator.start();
    mCountDownTimer.start();
  }

  /**
   * After the race is started a short sound signal is turned on
   */
  private void startRaceSound() {
    try {
      Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
      Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
      r.play();
    } catch (Exception e) {
      EventBus.getDefault()
          .post(new ErrorEvent(R.string.error_playback, e, this.getClass().getSimpleName()));

    }
  }

  /**
   * After the race is started the GPS is turned on so it can display information to the user on
   * the screen
   */
  private void showGO() {
    new CountDownTimer(1000, 500) {
      @Override
      public void onTick(long millisUntilFinished) {
      }

      @Override
      public void onFinish() {
        mCounting = false;
        mCountDownTextView.setText("");
        mShowMap.setVisibility(View.VISIBLE);
        Bundle raceTypeBundle = new Bundle();
        raceTypeBundle.putParcelable(JoinedDTO.class.getName(), mJoinedDTO);
        sendCommandMessageToGPSThread(raceTypeBundle, START_GETTING_GPS_DATA);
        EventBus.getDefault().post(new StartRaceEvent(this.getClass().getSimpleName()));
        RaceGPSHandlerThread.raceRunning = true;
        obdCheckAndPost();
        mStartRace.setVisibility(View.VISIBLE);
        mStartRace.setText(R.string.end_race);
        if (!getSharedPreferences().getBoolean(Constants.CREATOR, false)) {
          mStartRace.setEnabled(true);
        }
        if (!Permissions.Video) {
          clearCameraPanel();
        }
        setupChronometerForRace();
        startStatusUpdateTimer();

      }
    }.start();
  }


  /**
   * Shows a timer on the screen (very smooth timer) The listener works on the base of taking the
   * current time and dividing it by 3600000 for hours 60000 for minutes and 1000 for seconds. The
   * result is returned like a test to the chronometer.
   */
  private void setupChronometerForRace() {
    mCurrentTimeChronometer.setOnChronometerTickListener(new OnChronometerTickListener() {
      @Override
      public void onChronometerTick(Chronometer chronometer) {
        long time = SystemClock.elapsedRealtime() - chronometer.getBase();
        int h = (int) (time / ONE_HOUR);
        int m = (int) (time - h * ONE_HOUR) / ONE_MINUTE;
        int s = (int) (time - h * ONE_HOUR - m * ONE_MINUTE) / ONE_SECOND;
        chronometer.setText(AndroidUtils.formatTimeRacing(time));
      }
    });
    mCurrentTimeChronometer.setBase(SystemClock.elapsedRealtime());
    mCurrentTimeChronometer.start();
  }

  /**
   * Updates racers positions
   */
  private void startStatusUpdateTimer() {
    TimerTask updateRaceStatusTask = new TimerTask() {

      @Override
      public void run() {
        RaceStatusDTO raceStatusDTO = AndroidUtils
            .copy(getApp().getRaceDataManager().getLastRaceStatusMessage());
        List<String> stringList = new ArrayList<>();
        for (RacerSummary r : raceStatusDTO.getSummaryList()) {

          stringList.add(RACE_STATUS_FORMAT
              .format(new Object[]{r.getPosition(), r.getRacerNickname(), r.getCurrentSpeed()}));
        }
        for (String s : stringList) {
          try {

            Message msg = Message.obtain();
            Bundle bundle = new Bundle();
            bundle.putString(STATUS_UPDATE_KEY, s);
            msg.setData(bundle);
            handler.sendMessage(msg);

            Thread.sleep(2500);

          } catch (InterruptedException e) {
            Log.e(TAG, "Thread interrupted", e);
          }

        }
      }

    };

    mRaceStatusTimer = new Timer();
    mRaceStatusTimer.schedule(updateRaceStatusTask, 0, 3000L * mJoinedDTO.getCountRacers());
  }


  /**
   * HIdes the camera panel and shows the map beneath it
   */
  private void clearCameraPanel() {
    mCameraPanel.setVisibility(View.GONE);
    cameraImageView.setVisibility(View.GONE);
  }

  /**
   * The race here is started and finished by all racers. Here is the case also where the racers can
   * cancel the race before it started
   */
  // TODO Refactor to use data manager
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onRaceWebSocketEvent(RaceWebSocketEvent event) throws IOException {

    Message inputMessage = event.getMessage();
    switch (RaceStatus.fromCode(inputMessage.what)) {
      case START_RACE:

        mRaceStarted = true;
        if (Permissions.Video) {
          sendMessageToCameraPreviewThread(TURN_ON_CAMERA);
        }
        startCountDown();
        break;
      case RACE_OVER:
        try {
          if (Permissions.Video) {
            sendMessageToCameraPreviewThread(TURN_OFF_THREAD);
          }
          stopSensors();
          RaceStatusDTO raceStatusDTO = inputMessage.getData()
              .getParcelable(RaceStatusDTO.class.getName());
          Intent intent = new Intent(RaceActivity.this, RaceSummaryActivity.class);
          intent.putExtra(RaceStatusDTO.class.getName(), raceStatusDTO);
          startActivity(intent);
        } catch (Exception e) {
          Log.wtf(TAG, "Error ", e);
        }
        break;

      case CANCELED:
        Intent cancelIntent = new Intent(RaceActivity.this, HomeActivity.class);
        startActivity(cancelIntent);
        finish();
        break;

      case RUNNING:
        RaceStatusDTO runningRaceStatusDTO = inputMessage.getData()
            .getParcelable(RaceStatusDTO.class.getName());
        if (runningRaceStatusDTO != null) {
          populateMapWithMarkers(runningRaceStatusDTO.getSummaryList());
        }
        break;
    }
  }


  /**
   * Shows markers on the map allocating all the racers participating in the race
   */
  private void populateMapWithMarkers(List<RacerSummary> summaryList) {
    if (mMapView.getOverlays().get(RACERS_OVERLAY_INDEX) != null) {
      mMapView.getOverlays().remove(RACERS_OVERLAY_INDEX);
    }
    Overlay overlay = new ItemizedIconOverlay<>(
        RaceActivity.this,
        addItemsToList(summaryList), null);
    mMapView.getOverlays().add(RACERS_OVERLAY_INDEX, overlay);
    mMapView.invalidate();
  }

  /**
   * Fill the list with with overlays for the map view in which are marked the places of the
   * opponents including you 9with a red marker)
   */
  private List<OverlayItem> addItemsToList(List<RacerSummary> summaryList) {
    int drawableIndex = 0;
    ArrayList<OverlayItem> overlayItemArrayList = new ArrayList<>();
    for (int i = 0; i < summaryList.size(); i++) {
      if (summaryList.get(i).getRacerNickname() == null
          || summaryList.get(i).getRacerNickname()
          .equals(getSharedPreferences().getString(Constants.NICK_NAME, ""))) {
        overlayItemArrayList.add(privateCreateOverlayItem(summaryList.get(i).getCurrentLat(),
            summaryList.get(i).getCurrentLng(), summaryList.get(i).getRacerNickname(),
            R.drawable.map_marker_racer_4));
        GeoPoint playerGeoPoint = new GeoPoint(summaryList.get(i).getCurrentLat(),
            summaryList.get(i).getCurrentLng());
        mMapView.getController().zoomTo(11);
        mMapView.getController().setCenter(playerGeoPoint);
      } else {
        overlayItemArrayList.add(privateCreateOverlayItem(summaryList.get(i).getCurrentLat(),
            summaryList.get(i).getCurrentLng(), summaryList.get(i).getRacerNickname(),
            mArrayOfDrawables[drawableIndex]));
        drawableIndex++;
      }
    }
    return overlayItemArrayList;
  }

  /**
   * Creates a single item which will be placed in the overlay and the overlay on the map
   */
  private OverlayItem privateCreateOverlayItem(Double currentLat, Double currentLng,
      String racerNickname, int drawableResources) {
    GeoPoint playerGeoPoint = new GeoPoint(currentLat, currentLng);
    OverlayItem overlayItem = new OverlayItem(racerNickname, racerNickname, playerGeoPoint);
    Drawable markerDrawable = getResources()
        .getDrawable(drawableResources);
    overlayItem.setMarker(markerDrawable);
    return overlayItem;
  }

  /**
   * Stops the streaming thread
   */
  public void stopSensors() {
    Message message = Message.obtain();
    message.what = STOP_SENSORS;
    EventBus.getDefault().post(new ServiceCommandEvent(message));
  }

  /**
   * Displays information on the screen about the speed, time, acceleration and etc
   */
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onRaceDataEvent(RaceDataEvent event) {
    final RaceDataDTO raceDataDTO = event.getRaceDataDTO();

    if (!mMetrical) { // convert data

      mCurrentSpeedTextView
          .setText(getString(R.string.current_speed_var_imperial,
              AndroidUtils.fromKilometresToMiles(raceDataDTO.getGpsSpeed())));
      mAccelerationTextView
          .setText(getString(R.string.acceleration_var_imperial,
              AndroidUtils.fromMeterToFeet(raceDataDTO.getGpsAcceleration())));

    } else {
      mCurrentSpeedTextView
          .setText(getString(R.string.current_speed_var, raceDataDTO.getGpsSpeed()));
      mAccelerationTextView
          .setText(getString(R.string.acceleration_var, raceDataDTO.getGpsAcceleration()));
    }
    mDistanceTextView.setText(distanceConverter(mMetrical, raceDataDTO.getGpsDistance()));
    if (getSharedPreferences().getBoolean(Constants.LIVE_OBD_DATA, false)) {
      mRpmTextView.setText(getString(R.string.rpm_var, raceDataDTO.getObdRpm()));
      mOBDSpeedTextView
          .setText(getString(R.string.format_distance_kilometers, raceDataDTO.getObdSpeed()));
      if (!getSharedPreferences()
          .getString(Constants.TEMPERATURE_SCALE_KEY, getString(R.string.celsius))
          .equalsIgnoreCase(getString(R.string.celsius))) {
        mEngineTempTextView.setText(getString(R.string.temp_fahrenheit_var,
            AndroidUtils.fromCelsiusToFahrenheit(raceDataDTO.getEngineCoolantTemp())));
      } else {
        mEngineTempTextView
            .setText(getString(R.string.temp_celsius_var, raceDataDTO.getEngineCoolantTemp()));
      }
    }
  }

  /**
   * THe race is stopped or finished and the applications stops to get GPS data. It sends command to
   * the service where the sensors are stopped.
   */
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onRaceGPSEvent(RaceGPSEvent event) {
    Message inputMessage = event.getMessage();
    switch (inputMessage.what) {
      case STOP_GPS: {
        mRaceFinished = true;
        removeTextViewsAndCancelRace();
        Log.wtf("FINISHED", "STOP GETTING GPS DATA AND FINISH RACE SEND TO WEBSOCKET");
        Editor editor = getSharedPreferences().edit();
        editor.putBoolean(Constants.RACE_IS_RUNNING, false);
        editor.apply();
        sendCommandMessageToGPSThread(null, STOP_GETTING_GPS_DATA);
        stopSensors();
        sendMessageToCameraPreviewThread(TURN_OFF_THREAD);
        sendCommandMessageToRaceWebSocketThread(null, FINISH_RACE);
        break;
      }
    }
  }

  /**
   * When the back is pressed an alert dialog is displayed so it can warn the racer that he can
   * cancel or quit the race
   */
  @Override
  public void onBackPressed() {
    View linearLayout = this.getLayoutInflater()
        .inflate(R.layout.fragment_navigate_back_warning, null);
    if (mJoinedDTO.getCountRacers() > 1) {
      if (!mRaceStarted) {
        ((TextView) linearLayout.findViewById(R.id.message1))
            .setText(R.string.are_you_sure_leave_race_racer);
        ((TextView) linearLayout.findViewById(R.id.message2))
            .setText(R.string.creator_warning_message);
        ((TextView) linearLayout.findViewById(R.id.message3))
            .setText(R.string.joiner_warning_message);
        showDialogOnBackPressed(linearLayout, LEAVE_BEFORE_GO);
      } else if (!mRaceCanceled) {
        ((TextView) linearLayout.findViewById(R.id.message1))
            .setText(R.string.are_you_sure_stop_race_racer);
        linearLayout.findViewById(R.id.message2).setVisibility(View.GONE);
        linearLayout.findViewById(R.id.message3).setVisibility(View.GONE);
        showDialogOnBackPressed(linearLayout, LEAVE_AFTER_GO_CREATOR_AND_RACER);
      } else {
        showWhiteToastMessage(getString(R.string.race_in_progress));
      }
    } else {
      ((TextView) linearLayout.findViewById(R.id.message1))
          .setText(R.string.are_you_sure_leave_race_creator);
      linearLayout.findViewById(R.id.message2).setVisibility(View.GONE);
      linearLayout.findViewById(R.id.message3).setVisibility(View.GONE);
      showDialogOnBackPressed(linearLayout, LEAVE_SINGLE_RACER);
    }
  }

  /**
   * Dialog for canceling the race
   */
  private void showDialogOnBackPressed(View layout, final int event) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(R.string.are_you_sure_race)
        .setView(layout)
        .setCancelable(false)
        .setPositiveButton(R.string.okay, new OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            switch (event) {

              case LEAVE_AFTER_GO_CREATOR_AND_RACER: {
                cancelRace();
                removeTextViewsAndCancelRace();
                break;
              }
              case LEAVE_BEFORE_GO: {
                if (mCounting) {
                  Toast.makeText(RaceActivity.this, R.string.finish_countdown,
                      Toast.LENGTH_SHORT).show();
                  break;
                }
                if (getSharedPreferences().getBoolean(Constants.CREATOR, false)) {
                  Bundle raceBundle = new Bundle();
                  raceBundle.putLong(Constants.RACE_ID, mJoinedDTO.getRaceId());
                  sendCommandMessageToRaceWebSocketThread(raceBundle, CANCEL_RACE);
                } else {
                  Bundle raceBundle = new Bundle();
                  raceBundle.putLong(Constants.RACE_ID, mJoinedDTO.getRaceId());
                  sendCommandMessageToRaceWebSocketThread(raceBundle, CANCEL_RACE);
                  Intent intent = new Intent(RaceActivity.this, HomeActivity.class);
                  startActivity(intent);
                  finish();
                }
                break;
              }
              case LEAVE_SINGLE_RACER: {
                if (!mRaceStarted) {
                  Bundle raceBundle = new Bundle();
                  raceBundle.putLong(Constants.RACE_ID, mJoinedDTO.getRaceId());
                  sendCommandMessageToRaceWebSocketThread(raceBundle, CANCEL_RACE);
                } else {
                  if (mCounting) {
                    Toast.makeText(RaceActivity.this, R.string.finish_countdown,
                        Toast.LENGTH_SHORT).show();
                    break;
                  }
                  cancelRace();
                }
                break;
              }
            }
          }
        }).setNeutralButton(R.string.cancel, new OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {

      }
    });
    builder.show();
  }

  /**
   * Enables Mock if its enabled
   */
  private void setStartOBDRead() {
    mStartOBDRead = AndroidUtils.isOBDReadReady(getSharedPreferences()) &&
        getSharedPreferences().getBoolean(Constants.LIVE_OBD_DATA, false);
  }

  /**
   * Check if OBD is available
   */
  private void obdCheckAndPost() {
    if (mStartOBDRead) {
      Message OBDRead = Message.obtain();
      EventBus.getDefault().post(new StartReadingDataFromOBDEvent(OBDRead));
    }
  }

  /**
   * Enables Mock if its available
   */
  private void isMockEnabled() {
    //REMOVE IF NEEDED
    if (BuildConfig.DEBUG) {
      SharedPreferences.Editor editor = getSharedPreferences().edit();
      editor.putBoolean(Constants.MOCK_LOCATION, true);
      editor.apply();
    }
    char enableCheck = 'e';
    EventBus.getDefault().post(new MockLocationEvent(enableCheck));
  }

  /**
   * Check if we have OBD enabled
   */
  private void obdLiveDataCheck() {
    if (!getSharedPreferences().getBoolean(Constants.LIVE_OBD_DATA, false)) {
      mRpmTextView.setText(getString(R.string.na));
      mOBDSpeedTextView.setTextKeepState(getString(R.string.na));
      mEngineTempTextView.setText(getString(R.string.na));
    }
  }


  /**
   * Camera Preview
   * Created by developer on 10.10.17.
   */


  class CameraPreview extends android.view.SurfaceView implements
      android.view.SurfaceHolder.Callback, android.hardware.Camera.PreviewCallback {

    private static final int STREAM_IMAGE_WIDTH = 320;
    private static final int STREAM_IMAGE_HEIGHT = 240;
    private static final int STREAM_IMAGE_FRAME_RATE = 15;

    public static final int INIT_FIRST_TIME_CAMERA = 1;
    public static final int TURN_ON_CAMERA = 2;
    public static final int INIT_AND_TURN_ON_CAMERA_AND_MEDIA = 3;
    public static final int RELEASE_CAMERA_AND_MEDIA = 4;
    public static final int TURN_OFF_THREAD = 5;
    private final boolean streamingAllowed;


    private Camera mCamera;
    private final String streamURL;
    private MediaRecorder mMediaRecorder;
    private String dateForVideo;
    private String folderForVideo;
    private Context mContext;
    @Getter
    private File dir;
    private FFmpegFrameRecorder recorder;
    private Frame yuvImage;
    private long startTime;
    private boolean streaming;
    private long videoTimestamp;


    public CameraPreview(Context context, long raceID, String streamURL,
        final boolean streamingAllowed) {

      super(context);
      this.streamURL = streamURL;
      this.streamingAllowed = streamingAllowed;
      this.mContext = context;
      dir = new File(
          Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
              + context.getString(R.string.app_name) + File.separator
              + raceID);
      folderForVideo = dir.getPath();
      dir.mkdirs();
    }


    /**
     * This thread controls the camera.
     */
    public void execute(Message message) {

      switch (message.what) {
        case INIT_FIRST_TIME_CAMERA:
          initCameraAndMedia();
          initStreamer();
          break;
        case TURN_ON_CAMERA:
          startMediaRecorderPreview();
          startStreaming();
          break;
        case INIT_AND_TURN_ON_CAMERA_AND_MEDIA:
          initCameraAndMedia();
          Runnable delayTask = new Runnable() {
            @Override
            public void run() {
              startMediaRecorderPreview();
              startStreaming();

            }
          };
          this.postDelayed(delayTask, 1500);
          break;
        case RELEASE_CAMERA_AND_MEDIA:
          releaseMediaRecorder();
          releaseCameraAndPreview();
          stopStreaming();
          break;
        case TURN_OFF_THREAD:
          Log.wtf("I WAS STOPPED", "STOP");
          releaseMediaRecorder();
          releaseCameraAndPreview();
          stopStreaming();
          break;
      }

    }


    /**
     * Initializing the camera and media recorder
     */
    private void initCameraAndMedia() {
      try {
        if (mCamera == null) {
          mCamera = Camera.open();
          Camera.Parameters param = mCamera.getParameters();
          param.set("cam_mode", 1);
          mCamera.setParameters(param);
          SurfaceHolder holder = this.getHolder();
          holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
          mCamera.setPreviewDisplay(holder);
          mCamera.unlock();
          if (mMediaRecorder == null) {
            dateForVideo = getCurrentTime();
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setCamera(mCamera);
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mMediaRecorder.setProfile(CamcorderProfile.get(getVideoQuality()));
            mMediaRecorder.setOutputFile(getPath());
            mMediaRecorder.setPreviewDisplay(this.getHolder().getSurface());
          }

        }
      } catch (RuntimeException e) {
        Log.wtf("CAMERA", "CameraFragment: ", e);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    /**
     * Gets the video from folder HSRacer by a given date.
     */
    public String getPath() {
      return folderForVideo + File.separator + dateForVideo + ".mp4";
    }

    /**
     * Takes the current time so it can be used like a name for each file
     */
    public String getCurrentTime() {
      Date date = Calendar.getInstance().getTime();
      SimpleDateFormat format = new SimpleDateFormat("MM_dd_yyyy_hh_mm_ss");
      return format.format(date);
    }

    /**
     * Start the media recorder
     */
    private void startMediaRecorderPreview() {
      Log.wtf("CAMERA", "startMediaRecorderPreview: ");
      try {
        mMediaRecorder.prepare();
        mMediaRecorder.start();
      } catch (IOException e) {
        Log.wtf("CAMERA", "startMediaRecorderPreview: ", e);
      } catch (IllegalStateException e) {
        Log.wtf("CAMERA", "COULD NOT START VIDEO RECORDING ", e);
      } catch (NullPointerException e) {
        Log.wtf("CAMERA", "COULD NOT START VIDEO RECORDING NULL POINTER", e);
      } catch (RuntimeException e) {
        Log.wtf("CAMERA", "RUNTIME EXCEPTION ERROR", e);
      }
    }

    /**
     * Releases camera
     */
    public void releaseCameraAndPreview() {
      if (mCamera != null) {
        mCamera.stopPreview();
        mCamera.setPreviewCallback(null);
        mCamera.release();
        mCamera = null;
      }
    }

    /**
     * Releases media recorder and sends a broadcast to the video gallery. The broadcast is used to
     * notify the device that there eis a new video
     */
    private void releaseMediaRecorder() {
      if (mCamera != null) {
        try {
          mMediaRecorder.stop();
          mMediaRecorder.reset();
          mMediaRecorder.release();
          mMediaRecorder = null;
          mContext.sendBroadcast(
              new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(getPath()))));
        } catch (IllegalStateException e) {
          File emptyFile = new File(getPath());
          if (emptyFile.exists() && emptyFile.length() == 0) {
            emptyFile.delete();
          }
          mMediaRecorder.release();
          mMediaRecorder = null;
        } catch (NullPointerException e) {
          Log.wtf("CAMERA", "ERROR BECAUSE MEDIA RECORDING IS NULL", e);
        }
      }

    }


    /**
     * Gets the video quality from the options located in the drawer->settings of the application
     */
    private int getVideoQuality() {
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

    // streaming implementation
    // TODO Play with settings to  find optimal
    private void initStreamer() {
      if (recorder == null) {
        Log.e(TAG, "initStreamer");

        // region
        yuvImage = new Frame(STREAM_IMAGE_WIDTH, STREAM_IMAGE_HEIGHT, Frame.DEPTH_UBYTE, 2);
        Log.e(TAG, "IplImage.create");
        // endregion

        recorder = new FFmpegFrameRecorder(streamURL, STREAM_IMAGE_WIDTH, STREAM_IMAGE_HEIGHT, 1);
        Log.e(TAG,
            "FFmpegFrameRecorder: " + streamURL + " imageWidth: " + STREAM_IMAGE_WIDTH
                + " imageHeight "
                + STREAM_IMAGE_HEIGHT);

        recorder.setFormat("flv");
        recorder.setVideoOption("preset", "ultrafast");
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        recorder.setAudioCodec(0);
        recorder.setFrameRate(15);
        recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
//    recorder.setVideoBitrate(5000);
//    recorder.setOption("content_type","video/webm");
        Log.v(TAG, "recorder.setFormat(\"mp4\")");

        Log.v(TAG, "recorder.setSampleRate(sampleAudioRateInHz)");

        // re-set in the surface changed method as well
        recorder.setFrameRate(STREAM_IMAGE_FRAME_RATE);
        Log.v(TAG, "recorder.setFrameRate(frameRate)");
      }

    }

    // Start the capture
    public void startStreaming() {
      if (streamingAllowed) {
        initStreamer();

        try {
          recorder.start();
          startTime = System.currentTimeMillis();
          streaming = true;
//      audioThread.start();
        } catch (FFmpegFrameRecorder.Exception e) {
          e.printStackTrace();
        }
      }
    }

    public void stopStreaming() {
      // This should stop the audio thread from running

      if (recorder != null && streaming) {
        streaming = false;
        Log.v(TAG, "Finishing streaming, calling stop and release on recorder");
        try {
          recorder.stop();
          recorder.release();
        } catch (FFmpegFrameRecorder.Exception e) {
          e.printStackTrace();
        }
        recorder = null;
      }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
      mCamera = Camera.open(CameraInfo.CAMERA_FACING_BACK);

      try {
        mCamera.setPreviewDisplay(holder);
        mCamera.setPreviewCallback(this);

        Camera.Parameters currentParams = mCamera.getParameters();
        Log.v(TAG, "Preview Framerate: " + currentParams.getPreviewFrameRate());
        Log.v(TAG, "Preview imageWidth: " + currentParams.getPreviewSize().width + " imageHeight: "
            + currentParams.getPreviewSize().height);

        // Use these values

//      bitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ALPHA_8);


	        	/*
        Log.v(LOG_TAG,"Creating previewBuffer size: " + imageWidth * imageHeight * ImageFormat.getBitsPerPixel(currentParams.getPreviewFormat())/8);
	        	previewBuffer = new byte[imageWidth * imageHeight * ImageFormat.getBitsPerPixel(currentParams.getPreviewFormat())/8];
				camera.addCallbackBuffer(previewBuffer);
	            camera.setPreviewCallbackWithBuffer(this);
	        	*/

        mCamera.startPreview();
      } catch (IOException e) {
        Log.v(TAG, e.getMessage());
        e.printStackTrace();
      }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
      Log.v(TAG, "Surface Changed: width " + width + " height: " + height);

      // We would do this if we want to reset the camera parameters
            /*
            if (!streaming) {
    			if (previewRunning){
    				camera.stopPreview();
    			}

    			try {
    				//Camera.Parameters cameraParameters = camera.getParameters();
    				//p.setPreviewSize(imageWidth, imageHeight);
    			    //p.setPreviewFrameRate(frameRate);
    				//camera.setParameters(cameraParameters);

    				camera.setPreviewDisplay(holder);
    				camera.startPreview();
    				previewRunning = true;
    			}
    			catch (IOException e) {
    				Log.e(LOG_TAG,e.getMessage());
    				e.printStackTrace();
    			}
    		}
            */

      // Get the current parameters
      Camera.Parameters currentParams = mCamera.getParameters();
      Log.v(TAG, "Preview Framerate: " + currentParams.getPreviewFrameRate());
      Log.v(TAG, "Preview imageWidth: " + currentParams.getPreviewSize().width + " imageHeight: "
          + currentParams.getPreviewSize().height);

      // Create the yuvIplimage if needed
      //yuvIplimage = IplImage.create(imageWidth, imageHeight, IPL_DEPTH_8U, 2);
      yuvImage = new Frame(STREAM_IMAGE_WIDTH, STREAM_IMAGE_HEIGHT, Frame.DEPTH_UBYTE, 2);
      //yuvIplimage = IplImage.create(imageWidth, imageHeight, IPL_DEPTH_32S, 2);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
      try {
        mCamera.setPreviewCallback(null);

        mCamera.release();

      } catch (RuntimeException e) {
        Log.v(TAG, e.getMessage());
        e.printStackTrace();
      }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

      if (yuvImage != null && streaming) {
        videoTimestamp = 1000 * (System.currentTimeMillis() - startTime);

        // Put the camera preview frame right into the yuvIplimage object
        //yuvIplimage.getByteBuffer().put(data);

        // region
        ((ByteBuffer) yuvImage.image[0].position(0)).put(data);
        // endregion

        // FAQ about IplImage:
        // - For custom raw processing of data, getByteBuffer() returns an NIO direct
        //   buffer wrapped around the memory pointed by imageData, and under Android we can
        //   also use that Buffer with Bitmap.copyPixelsFromBuffer() and copyPixelsToBuffer().
        // - To get a BufferedImage from an IplImage, we may call getBufferedImage().
        // - The createFrom() factory method can construct an IplImage from a BufferedImage.
        // - There are also a few copy*() methods for BufferedImage<->IplImage data transfers.

        // Let's try it..
        // This works but only on transparency
        // Need to find the right Bitmap and IplImage matching types

            	/*
              bitmap.copyPixelsFromBuffer(yuvIplimage.getByteBuffer());
            	//bitmap.setPixel(10,10,Color.MAGENTA);

            	canvas = new Canvas(bitmap);
            	Paint paint = new Paint();
            	paint.setColor(Color.GREEN);
            	float leftx = 20;
            	float topy = 20;
            	float rightx = 50;
            	float bottomy = 100;
            	RectF rectangle = new RectF(leftx,topy,rightx,bottomy);
            	canvas.drawRect(rectangle, paint);

            	bitmap.copyPixelsToBuffer(yuvIplimage.getByteBuffer());
                */
        //Log.v(LOG_TAG,"Writing Frame");

        try {

          // Get the correct time
          recorder.setTimestamp(videoTimestamp);

          // Record the image into FFmpegFrameRecorder
          //recorder.record(yuvIplimage);

          // region
          recorder.record(yuvImage);
          // endregion

        } catch (FFmpegFrameRecorder.Exception e) {
          Log.v(TAG, e.getMessage());
          e.printStackTrace();
        }
      }
    }
  }
}
