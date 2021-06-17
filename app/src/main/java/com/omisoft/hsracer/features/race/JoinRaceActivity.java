package com.omisoft.hsracer.features.race;

import static com.omisoft.hsracer.constants.Constants.OBD_ENABLE_KEY;
import static com.omisoft.hsracer.features.race.threads.RaceWebSocketThread.JOIN_RACE;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import butterknife.BindView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.MessageListener;
import com.omisoft.hsracer.BuildConfig;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.common.BaseApp.Permissions;
import com.omisoft.hsracer.common.BaseToolbarActivity;
import com.omisoft.hsracer.common.events.ServiceCommandEvent;
import com.omisoft.hsracer.constants.Constants;
import com.omisoft.hsracer.constants.FirebaseEvent;
import com.omisoft.hsracer.features.race.events.JoinRaceErrorEvent;
import com.omisoft.hsracer.features.race.events.RaceWebSocketEvent;
import com.omisoft.hsracer.model.Car;
import com.omisoft.hsracer.services.RaceService;
import com.omisoft.hsracer.utils.AndroidUtils;
import com.omisoft.hsracer.ws.protocol.dto.JoinedDTO;
import com.omisoft.hsracer.ws.protocol.enums.MessageType;
import java.util.ArrayList;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Racers join the race from here
 * Created by Omisoft LLC. on 5/16/17.
 */

public class JoinRaceActivity extends BaseToolbarActivity implements ConnectionCallbacks,
    OnConnectionFailedListener {

  private static final String CARS_LIST = "cars";

  private ArrayList<Car> cars;

  @BindView(R.id.video_check_box)
  CheckBox videoCheckBox;
  @BindView(R.id.obd_check_box)
  CheckBox obdCheckBox;
  @BindView(R.id.video_stream_box)
  CheckBox videoStreamBox;
  @BindView(R.id.race_id_join_race)
  EditText joinRaceIDEditText;
  @BindView(R.id.join_race_btn)
  Button joinRaceButton;
  private GoogleApiClient mGoogleApiClient;
  private static final int REQUEST_RESOLVE_ERROR = 1001;
  private MessageListener mMessageListener;
  private AlertDialog joinAlertDialog;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_join_race);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      checkPreferences();
    } else {
      setVideoAndPreview(true);
    }
    if (getSharedPreferences().getBoolean(OBD_ENABLE_KEY, false)) {
      obdCheckBox.setEnabled(true);
    }

    if (savedInstanceState != null) {
      cars = savedInstanceState.getParcelableArrayList(CARS_LIST);
    } else {
      cars = getIntent().getParcelableArrayListExtra(CARS_LIST);
    }
    setOBDEnabled();
    initMyCarsSpinner();
    // Init nearby messages
    mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addApi(Nearby.MESSAGES_API)
        .addConnectionCallbacks(this)
        .enableAutoManage(this, this)
        .build();
    mMessageListener = new MessageListener() {
      @Override
      public void onFound(com.google.android.gms.nearby.messages.Message message) {
        String messageAsString = new String(message.getContent());
        Log.e(TAG, "Found message: " + messageAsString);
        showWhiteToastMessage("YEAH WE ARE HERE FINALLY ");
        String[] splitMessage = messageAsString.split(":");
        if (splitMessage[0].equals(BuildConfig.APPLICATION_ID)) {
          showDialogToJoinRace(splitMessage[1], splitMessage[2], splitMessage[3]);
        }

      }

      @Override
      public void onLost(com.google.android.gms.nearby.messages.Message message) {
        String messageAsString = new String(message.getContent());
        Log.d(TAG, "Lost sight of message: " + messageAsString);
      }
    };
  }


  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {
    savedInstanceState.putParcelableArrayList(CARS_LIST, cars);
    super.onSaveInstanceState(savedInstanceState);
  }

  @Override
  public void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    cars = savedInstanceState.getParcelableArrayList(CARS_LIST);

  }

  /**
   * Checks for permissions
   */
  public void onRequestPermissionsResult(int requestCode,
      @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    switch (requestCode) {
      case CAMERA_PERMISSION_REQUEST: {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
          setVideoAndPreview(false);

          videoCheckBox.setChecked(false);
          setStreamingAllowed(false);
          videoStreamBox.setChecked(false);
          showWhiteToastMessage(getString(R.string.write_to_external_storage_permission));
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
          setVideoAndPreview(false);
          videoCheckBox.setChecked(false);
          setStreamingAllowed(false);
          videoStreamBox.setChecked(false);
          showWhiteToastMessage(getString(R.string.camera_permission));
        } else {
          setVideoAndPreview(true);
        }

        break;
      }
    }
  }


  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    int itemSelected = item.getItemId();

    switch (itemSelected) {

      case android.R.id.home:
        onBackPressed();
        return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onStop() {
    unsubscribe();
    if (mGoogleApiClient.isConnected()) {
      mGoogleApiClient.disconnect();
    }

    super.onStop();
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
      Log.e(TAG, "THE CLIENT IS CONNECTED");
      mGoogleApiClient.connect();
    }
  }

  /**
   * When a user is found nearby (a user who created the race) this dialog is shown with a message
   * and asking if that user wants to participate in his race
   */
  private void showDialogToJoinRace(final String idRace, String creatorName, String raceName) {
    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
    alertBuilder.setTitle(getString(R.string.join_race_notification, creatorName))
        .setMessage(getString(R.string.invitation_message, creatorName, raceName))
        .setPositiveButton(getString(R.string.join_race_now), new OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            joinRaceIDEditText.setText(idRace);
            joinRaceButton.performClick();
          }
        }).setNegativeButton(getString(R.string.cancel), new OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        joinAlertDialog.dismiss();
      }
    });
    alertBuilder.setCancelable(true);
    joinAlertDialog = alertBuilder.create();
    joinAlertDialog.show();
  }

  /**
   * When the button for joining the race is clicked  the user is send to {@link PreRaceSummaryActivity}
   * @param view
   */
  public void joinRaceBtnOnClickListener(View view) {
    if (getApp().isConnected()) {
      if (getApp().checkIsGPSEnabled()) {
        String raceIdText = joinRaceIDEditText.getText().toString();
        if (TextUtils.isEmpty(raceIdText)) {
          showWhiteToastMessage(getString(R.string.enter_raceId));
          return;
        }
        Message m = Message.obtain();
        m.what = RaceService.START_WEBSOCKET_ACTION;
        EventBus.getDefault().post(new ServiceCommandEvent(m));
        Long raceId = Long.valueOf(raceIdText);
        Editor editor = getSharedPreferences().edit();
        editor.putBoolean(Constants.CREATOR, false);
        editor.putLong(Constants.RACE_ID, raceId);
        editor.apply();
        Bundle joinRace = new Bundle();
        joinRace.putLong(Constants.RACE_ID, raceId);
        sendCommandMessageToRaceWebSocketThread(joinRace, JOIN_RACE);
      } else {
        showAlertGPS();
      }
    } else {
      showWhiteToastMessage(getString(R.string.no_internet_connection));
    }
  }

  /**
   * Initialize a spinner in which are show the created cars
   */
  public void initMyCarsSpinner() {
    String[] carsArray = AndroidUtils.getCarsArray(cars);
    long carId = cars.get(0).getRestId();

    if (!cars.isEmpty()) {
      Editor editor = getSharedPreferences().edit();
      editor.putLong(Constants.CAR_REST_ID, carId);
      editor.apply();
    }

    final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, R.layout.spinner_row,
        R.id.text_view_row, carsArray);
    final Spinner myCars = findViewById(R.id.personal_cars_join_race_spinner);
    myCars.setAdapter(spinnerAdapter);
    myCars.setSelection(0);
    myCars.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Editor editor = getSharedPreferences().edit();
        editor.putLong(Constants.CAR_REST_ID, cars.get(position).getRestId());
        editor.apply();
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });
  }

  /**
   * Allows the video recording
   * @param enabledVideoAndPreview
   */

  private void setVideoAndPreview(boolean enabledVideoAndPreview) {
    SharedPreferences.Editor editor = getSharedPreferences().edit();
    editor.putBoolean(Constants.RECORD_VIDEO, enabledVideoAndPreview);
    editor.putBoolean(Constants.SHOW_PREVIEW,
        enabledVideoAndPreview);
    editor.apply();
    Permissions.Video = enabledVideoAndPreview;
  }

  /**
   * Check preferences
   */
  private void checkPreferences() {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        == PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        == PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        == PackageManager.PERMISSION_GRANTED) {
      setVideoAndPreview(true);
    } else {
      ActivityCompat.requestPermissions(this, new String[]{
          Manifest.permission.CAMERA,
          Manifest.permission.WRITE_EXTERNAL_STORAGE,
          Manifest.permission.RECORD_AUDIO
      }, CAMERA_PERMISSION_REQUEST);
    }
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onRaceWebSocketEvent(RaceWebSocketEvent event) {
    Message inputMessage = event.getMessage();
    switch (MessageType.fromCode(inputMessage.what)) {
      case JOINED: {
        JoinedDTO joinedDTO = inputMessage.getData().getParcelable(JoinedDTO.class.getName());
        if (!BuildConfig.DEBUG) {
          Bundle firebaseParamBundle = new Bundle();
          firebaseParamBundle.putLong("race_id", joinedDTO.getRaceId());

          mFirebaseAnalytics.logEvent(FirebaseEvent.JOIN_RACE, firebaseParamBundle);
        }
        Intent intent = new Intent(JoinRaceActivity.this, PreRaceSummaryActivity.class);
        intent.putExtra(JoinedDTO.class.getName(), joinedDTO);
        startActivity(intent);
      }
    }
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void errorFromJoininRace(JoinRaceErrorEvent event) {
    showWhiteToastMessage(event.getMessage());
  }

  /**
   * Enables and disables the OBD and Video recording when a particular checkbox is clicked
   * @param view
   */
  public void checkVideoAndOBD(View view) {
    switch (view.getId()) {
      case R.id.video_check_box:

        if (videoCheckBox.isChecked()) {
          if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
              == PackageManager.PERMISSION_GRANTED
              &&
              ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                  == PackageManager.PERMISSION_GRANTED) {
            setVideoAndPreview(true);

          } else {
            ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            }, CAMERA_PERMISSION_REQUEST);
            setVideoAndPreview(false);

          }
        } else {
          checkPreferences();
          setVideoAndPreview(false);

        }
        break;
      case R.id.obd_check_box:
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(Constants.LIVE_OBD_DATA, obdCheckBox.isChecked());
        editor.apply();
        break;
      case R.id.video_stream_box:

        if (videoStreamBox.isChecked()) {
          if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
              == PackageManager.PERMISSION_GRANTED
              &&
              ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                  == PackageManager.PERMISSION_GRANTED) {
            setStreamingAllowed(true);
          } else {
            ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            }, CAMERA_PERMISSION_REQUEST);
            setStreamingAllowed(false);
          }
        } else {
          checkPreferences();
          setStreamingAllowed(false);
        }
        break;
    }
  }

  /**
   * Enables the OBD checkbox if the OBD is turned on in Settings
   */
  private void setOBDEnabled() {
    if (AndroidUtils.isOBDReadReady(getSharedPreferences())) {
      obdCheckBox.setEnabled(true);
    } else {
      obdCheckBox.setEnabled(false);
    }
  }


  @Override
  public void onConnected(@Nullable Bundle bundle) {
    subscribe();

  }

  @Override
  public void onConnectionSuspended(int i) {

  }

  /**
   * Called when the connection to google nearby failed
   * @param result
   */
  @Override
  public void onConnectionFailed(ConnectionResult result) {
    if (result.hasResolution()) {
      try {
        result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
      } catch (SendIntentException e) {
        e.printStackTrace();
      }
    } else {
      Log.e(TAG, "GoogleApiClient connection failed");
    }
  }

  /**
   * Subscribes to google nearby
   */
  private void subscribe() {
    Log.i(TAG, "GOOGLE NEARBY SUBSCRIBED");
    Nearby.Messages.subscribe(mGoogleApiClient, mMessageListener);
  }

  /**
   * Un subscribe to Google nearby
   */
  private void unsubscribe() {
    Nearby.Messages.unsubscribe(mGoogleApiClient, mMessageListener);
  }

  /**
   * Called when pointer capture is enabled or disabled for the current window.
   *
   * @param hasCapture True if the window has pointer capture.
   */
  @Override
  public void onPointerCaptureChanged(boolean hasCapture) {

  }

  /**
   * Allows streaming
   */
  private void setStreamingAllowed(boolean enableStreaming) {
    SharedPreferences.Editor editor = getSharedPreferences().edit();
    editor.putBoolean(Constants.LIVE_STREAM, enableStreaming);
    editor.putBoolean(Constants.SHOW_PREVIEW,
        enableStreaming);
    editor.apply();
    Permissions.Video = enableStreaming;
  }

}
